import { useState, useEffect, useCallback } from 'react'
import { useParams, useNavigate, Link } from 'react-router-dom'
import { getSensorById, updateSensor, deleteSensor } from '../api/sensors'
import { getRules, createRule, updateRule, deleteRule } from '../api/rules'
import { getMeasurements } from '../api/measurements'
import { getAlerts } from '../api/alerts'
import { extractList } from '../utils/apiShape'
import { formatDateTime, formatValue } from '../utils/format'
import Badge from '../components/common/Badge'
import Loader from '../components/common/Loader'
import ErrorState from '../components/common/ErrorState'
import EmptyState from '../components/common/EmptyState'
import Modal from '../components/common/Modal'
import SensorForm from '../components/sensors/SensorForm'
import RuleList from '../components/rules/RuleList'
import RuleForm from '../components/rules/RuleForm'

export default function SensorDetail() {
    const { id } = useParams()
    const navigate = useNavigate()

    const [sensor, setSensor] = useState(null)
    const [loading, setLoading] = useState(true)
    const [error, setError] = useState(null)
    const [editOpen, setEditOpen] = useState(false)
    const [deleteOpen, setDeleteOpen] = useState(false)
    const [deleting, setDeleting] = useState(false)

    const fetchSensor = useCallback(async () => {
        setLoading(true); setError(null)
        try {
            const data = await getSensorById(id)
            setSensor(data)
        } catch (e) {
            setError(e.message)
        } finally {
            setLoading(false)
        }
    }, [id])

    useEffect(() => { fetchSensor() }, [fetchSensor])

    async function handleEdit(form) {
        const updated = await updateSensor(id, form)
        setSensor(updated)
    }

    async function handleDelete() {
        setDeleting(true)
        try {
            await deleteSensor(id)
            navigate('/sensors')
        } catch (e) {
            setError(e.message)
            setDeleting(false)
        }
    }

    if (loading) return <Loader text="Loading sensor..." />
    if (error) return (
        <div>
            <div className="page-header">
                <Link to="/sensors" className="btn btn-ghost">← Back</Link>
            </div>
            <ErrorState message={error} onRetry={fetchSensor} />
        </div>
    )
    if (!sensor) return null

    const name = sensor.name || sensor.sensorName || `Sensor ${sensor.id}`
    const type = sensor.type || sensor.sensorType || '—'
    const status = sensor.status || '—'
    const location = sensor.location || sensor.fieldId || '—'

    return (
        <div>
            {/* Header */}
            <div className="page-header">
                <div>
                    <div style={{ display: 'flex', alignItems: 'center', gap: 12 }}>
                        <Link to="/sensors" className="btn btn-ghost btn-sm">← Back</Link>
                        <h2 className="page-title">{name}</h2>
                        <Badge status={status} />
                    </div>
                    <p className="page-subtitle">Sensor ID: {id}</p>
                </div>
                <div className="btn-group">
                    <button className="btn btn-secondary" onClick={() => setEditOpen(true)}>✏️ Edit</button>
                    <button className="btn btn-danger" onClick={() => setDeleteOpen(true)}>🗑️ Delete</button>
                    <Link
                        to={`/measurements?sensorId=${id}`}
                        className="btn btn-secondary"
                    >
                        📈 All Measurements
                    </Link>
                </div>
            </div>

            {/* Sensor Info Card */}
            <div className="card" style={{ marginBottom: 20 }}>
                <div className="card-header">
                    <span className="card-title">📡 Sensor Information</span>
                </div>
                <div className="form-row">
                    <InfoRow label="Name" value={name} />
                    <InfoRow label="Type" value={type} />
                    <InfoRow label="Status" value={<Badge status={status} />} />
                    <InfoRow label="Field" value={location} />
                    {sensor.apiKey && <InfoRow label="API Key" value={
                        <code style={{ fontSize: '0.8rem', background: 'var(--color-surface-2)', padding: '2px 8px', borderRadius: 4 }}>
                            {sensor.apiKey}
                        </code>
                    } />}
                </div>
            </div>

            {/* Recent Measurements */}
            <div className="card" style={{ marginBottom: 20 }}>
                <div className="card-header">
                    <span className="card-title">📊 Recent Measurements</span>
                    <Link to={`/measurements?sensorId=${id}`} className="btn btn-ghost btn-sm">View All →</Link>
                </div>
                <RecentMeasurements sensorId={id} />
            </div>

            {/* Alert History */}
            <div className="card" style={{ marginBottom: 20 }}>
                <div className="card-header">
                    <span className="card-title">🔔 Alert History</span>
                    <Link to="/alerts" className="btn btn-ghost btn-sm">View All →</Link>
                </div>
                <AlertHistory sensorId={id} />
            </div>

            {/* Alert Rules Section */}
            <div className="card">
                <div className="card-header">
                    <span className="card-title">⚙️ Alert Rules</span>
                </div>
                <AlertRulesSection sensorId={id} />
            </div>

            {/* Edit Modal */}
            <SensorForm
                isOpen={editOpen}
                onClose={() => setEditOpen(false)}
                onSubmit={handleEdit}
                initial={sensor}
                title="✏️ Edit Sensor"
            />

            {/* Delete Confirm */}
            <Modal
                isOpen={deleteOpen}
                onClose={() => setDeleteOpen(false)}
                title="🗑️ Delete Sensor"
                footer={
                    <>
                        <button className="btn btn-secondary" onClick={() => setDeleteOpen(false)} disabled={deleting}>Cancel</button>
                        <button className="btn btn-danger" onClick={handleDelete} disabled={deleting}>
                            {deleting ? '⏳ Deleting...' : '🗑️ Delete'}
                        </button>
                    </>
                }
            >
                <div className="confirm-dialog">
                    <div className="confirm-icon">⚠️</div>
                    <div style={{ fontWeight: 600, marginBottom: 8 }}>Delete this sensor?</div>
                    <div className="confirm-message">
                        Deleting <strong>{name}</strong> will remove all associated data. This cannot be undone.
                    </div>
                </div>
            </Modal>
        </div>
    )
}

function RecentMeasurements({ sensorId }) {
    const [data, setData] = useState([])
    const [loading, setLoading] = useState(true)

    useEffect(() => {
        getMeasurements({ sensorId: sensorId, size: 5 })
            .then(d => setData(extractList(d)))
            .catch(() => setData([]))
            .finally(() => setLoading(false))
    }, [sensorId])

    if (loading) return <Loader text="Loading..." />
    if (data.length === 0) return (
        <div style={{ padding: '24px 0', textAlign: 'center', color: 'var(--color-text-muted)', fontSize: '0.85rem' }}>
            <div style={{ fontSize: '1.5rem', marginBottom: 6, opacity: 0.4 }}>📊</div>
            No measurements recorded yet.
        </div>
    )

    return (
        <div className="table-wrapper">
            <table>
                <thead>
                    <tr>
                        <th>Value</th>
                        <th>Unit</th>
                        <th>Time</th>
                    </tr>
                </thead>
                <tbody>
                    {data.map((m, i) => (
                        <tr key={m.id || i}>
                            <td style={{ fontWeight: 600, fontFamily: 'var(--font-mono)', fontSize: '0.9rem' }}>
                                {formatValue(m.value)}
                            </td>
                            <td className="text-muted text-sm">{m.unit || '—'}</td>
                            <td className="text-muted text-sm">{formatDateTime(m.timestamp || m.createdAt)}</td>
                        </tr>
                    ))}
                </tbody>
            </table>
        </div>
    )
}

function AlertHistory({ sensorId }) {
    const [data, setData] = useState([])
    const [loading, setLoading] = useState(true)

    useEffect(() => {
        getAlerts({ sensorId, size: 10 })
            .then(d => setData(extractList(d)))
            .catch(() => setData([]))
            .finally(() => setLoading(false))
    }, [sensorId])

    if (loading) return <Loader text="Loading..." />
    if (data.length === 0) return (
        <div style={{ padding: '24px 0', textAlign: 'center', color: 'var(--color-text-muted)', fontSize: '0.85rem' }}>
            <div style={{ fontSize: '1.5rem', marginBottom: 6, opacity: 0.4 }}>🔔</div>
            No alerts for this sensor.
        </div>
    )

    return (
        <div className="table-wrapper">
            <table>
                <thead>
                    <tr>
                        <th>Status</th>
                        <th>Message</th>
                        <th>Time</th>
                    </tr>
                </thead>
                <tbody>
                    {data.map((a, i) => (
                        <tr key={a.id || i}>
                            <td><Badge status={a.status} /></td>
                            <td style={{ maxWidth: 300 }}>
                                <span className="truncate">{a.message || '—'}</span>
                            </td>
                            <td className="text-muted text-sm">{formatDateTime(a.createdAt)}</td>
                        </tr>
                    ))}
                </tbody>
            </table>
        </div>
    )
}

function InfoRow({ label, value }) {
    return (
        <div className="form-group">
            <div className="form-label">{label}</div>
            <div style={{ color: 'var(--color-text)', fontSize: '0.9rem' }}>{value || '—'}</div>
        </div>
    )
}

function AlertRulesSection({ sensorId }) {
    const [rules, setRules] = useState([])
    const [loading, setLoading] = useState(true)
    const [error, setError] = useState(null)
    const [createOpen, setCreateOpen] = useState(false)
    const [editRule, setEditRule] = useState(null)
    const [deleteRule2, setDeleteRule2] = useState(null)
    const [deleting, setDeleting] = useState(false)

    const fetchRules = useCallback(async () => {
        setLoading(true); setError(null)
        try {
            const data = await getRules(sensorId)
            setRules(extractList(data))
        } catch (e) {
            setError(e.message)
        } finally {
            setLoading(false)
        }
    }, [sensorId])

    useEffect(() => { fetchRules() }, [fetchRules])

    async function handleCreate(form) {
        await createRule(sensorId, form)
        await fetchRules()
    }

    async function handleEdit(form) {
        await updateRule(sensorId, editRule.id, form)
        await fetchRules()
    }

    async function handleDelete() {
        setDeleting(true)
        try {
            await deleteRule(sensorId, deleteRule2.id)
            setDeleteRule2(null)
            await fetchRules()
        } catch (e) {
            setError(e.message)
        } finally {
            setDeleting(false)
        }
    }

    return (
        <>
            <div style={{ marginBottom: 16 }}>
                <button className="btn btn-primary btn-sm" onClick={() => setCreateOpen(true)}>
                    + Add Rule
                </button>
            </div>

            {loading && <Loader text="Loading rules..." />}
            {error && <ErrorState message={error} onRetry={fetchRules} />}

            {!loading && !error && rules.length === 0 && (
                <EmptyState icon="🔕" title="No alert rules" description="Add rules to trigger alerts for this sensor." />
            )}

            {!loading && !error && rules.length > 0 && (
                <RuleList
                    rules={rules}
                    onEdit={r => setEditRule(r)}
                    onDelete={r => setDeleteRule2(r)}
                />
            )}

            <RuleForm isOpen={createOpen} onClose={() => setCreateOpen(false)} onSubmit={handleCreate} title="➕ Add Alert Rule" />
            <RuleForm isOpen={!!editRule} onClose={() => setEditRule(null)} onSubmit={handleEdit} initial={editRule} title="✏️ Edit Alert Rule" />

            <Modal
                isOpen={!!deleteRule2}
                onClose={() => setDeleteRule2(null)}
                title="🗑️ Delete Rule"
                footer={
                    <>
                        <button className="btn btn-secondary" onClick={() => setDeleteRule2(null)} disabled={deleting}>Cancel</button>
                        <button className="btn btn-danger" onClick={handleDelete} disabled={deleting}>
                            {deleting ? '⏳...' : '🗑️ Delete'}
                        </button>
                    </>
                }
            >
                <div className="confirm-dialog">
                    <div className="confirm-icon">⚠️</div>
                    <div className="confirm-message">Delete rule "{deleteRule2?.name || deleteRule2?.id}"? This cannot be undone.</div>
                </div>
            </Modal>
        </>
    )
}
