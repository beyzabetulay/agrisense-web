import { useState, useEffect, useCallback } from 'react'
import { getSensors, deleteSensor, createSensor, updateSensor } from '../api/sensors'
import { extractList } from '../utils/apiShape'
import { useToast } from '../components/common/Toast'
import SensorCard from '../components/sensors/SensorCard'
import SensorForm from '../components/sensors/SensorForm'
import Modal from '../components/common/Modal'
import Loader from '../components/common/Loader'
import ErrorState from '../components/common/ErrorState'
import EmptyState from '../components/common/EmptyState'

export default function Sensors() {
    const toast = useToast()
    const [sensors, setSensors] = useState([])
    const [loading, setLoading] = useState(true)
    const [error, setError] = useState(null)
    const [search, setSearch] = useState('')
    const [createOpen, setCreateOpen] = useState(false)
    const [editSensor, setEditSensor] = useState(null)
    const [deleteTarget, setDeleteTarget] = useState(null)
    const [deleting, setDeleting] = useState(false)

    const fetchSensors = useCallback(async () => {
        setLoading(true); setError(null)
        try {
            const data = await getSensors()
            setSensors(extractList(data))
        } catch (e) {
            setError(e.message)
        } finally {
            setLoading(false)
        }
    }, [])

    useEffect(() => { fetchSensors() }, [fetchSensors])

    async function handleCreate(form) {
        await createSensor(form)
        toast.success('Sensor created successfully!')
        await fetchSensors()
    }

    async function handleEdit(form) {
        await updateSensor(editSensor.id, form)
        toast.success('Sensor updated successfully!')
        await fetchSensors()
    }

    async function handleDelete() {
        setDeleting(true)
        try {
            await deleteSensor(deleteTarget.id)
            toast.success('Sensor deleted.')
            setDeleteTarget(null)
            await fetchSensors()
        } catch (e) {
            toast.error(e.message)
            setError(e.message)
        } finally {
            setDeleting(false)
        }
    }

    const filtered = sensors.filter(s => {
        const q = search.toLowerCase()
        return (
            (s.name || '').toLowerCase().includes(q) ||
            (s.type || '').toLowerCase().includes(q) ||
            (s.location || '').toLowerCase().includes(q) ||
            String(s.id || '').includes(q)
        )
    })

    return (
        <div>
            <div className="page-header">
                <div>
                    <h2 className="page-title">Sensors</h2>
                    <p className="page-subtitle">Manage your IoT sensor inventory</p>
                </div>
                <button className="btn btn-primary" onClick={() => setCreateOpen(true)} id="create-sensor-btn">
                    + Add Sensor
                </button>
            </div>

            {/* Search */}
            <div className="filters-bar" style={{ marginBottom: 20 }}>
                <div className="search-box">
                    <span className="search-box-icon">🔍</span>
                    <input
                        className="form-input"
                        placeholder="Search sensors..."
                        value={search}
                        onChange={e => setSearch(e.target.value)}
                    />
                </div>
                <span className="text-muted text-sm">{filtered.length} sensor{filtered.length !== 1 ? 's' : ''}</span>
            </div>

            {loading && <Loader text="Fetching sensors..." />}
            {error && <ErrorState message={error} onRetry={fetchSensors} />}

            {!loading && !error && filtered.length === 0 && (
                <EmptyState
                    icon="📡"
                    title="No sensors found"
                    description={search ? 'Try adjusting your search.' : 'Add your first sensor to get started.'}
                    action={
                        !search && (
                            <button className="btn btn-primary" onClick={() => setCreateOpen(true)}>+ Add Sensor</button>
                        )
                    }
                />
            )}

            {!loading && !error && filtered.length > 0 && (
                <div className="card-grid">
                    {filtered.map(sensor => (
                        <SensorCard
                            key={sensor.id}
                            sensor={sensor}
                            onEdit={s => setEditSensor(s)}
                            onDelete={s => setDeleteTarget(s)}
                        />
                    ))}
                </div>
            )}

            {/* Create Modal */}
            <SensorForm
                isOpen={createOpen}
                onClose={() => setCreateOpen(false)}
                onSubmit={handleCreate}
                title="➕ Add New Sensor"
            />

            {/* Edit Modal */}
            <SensorForm
                isOpen={!!editSensor}
                onClose={() => setEditSensor(null)}
                onSubmit={handleEdit}
                initial={editSensor}
                title="✏️ Edit Sensor"
            />

            {/* Delete Confirm */}
            <Modal
                isOpen={!!deleteTarget}
                onClose={() => setDeleteTarget(null)}
                title="🗑️ Delete Sensor"
                footer={
                    <>
                        <button className="btn btn-secondary" onClick={() => setDeleteTarget(null)} disabled={deleting}>
                            Cancel
                        </button>
                        <button className="btn btn-danger" onClick={handleDelete} disabled={deleting}>
                            {deleting ? '⏳ Deleting...' : '🗑️ Delete'}
                        </button>
                    </>
                }
            >
                <div className="confirm-dialog">
                    <div className="confirm-icon">⚠️</div>
                    <div style={{ fontWeight: 600, marginBottom: 8 }}>
                        Delete "{deleteTarget?.name || deleteTarget?.id}"?
                    </div>
                    <div className="confirm-message">
                        This will permanently delete the sensor and all its data. This action cannot be undone.
                    </div>
                </div>
            </Modal>
        </div>
    )
}
