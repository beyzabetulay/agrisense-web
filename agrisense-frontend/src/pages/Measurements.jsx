import { useState, useEffect, useCallback } from 'react'
import { useSearchParams } from 'react-router-dom'
import { getMeasurements, postMeasurement } from '../api/measurements'
import { getSensors } from '../api/sensors'
import { extractList, extractPagination } from '../utils/apiShape'
import { formatDateTime, formatValue } from '../utils/format'
import { useToast } from '../components/common/Toast'
import Loader from '../components/common/Loader'
import ErrorState from '../components/common/ErrorState'
import EmptyState from '../components/common/EmptyState'
import Pagination from '../components/common/Pagination'

const UNIT_OPTIONS = [
    { value: 'CELSIUS', label: '°C — Celsius' },
    { value: 'PERCENT', label: '% — Percent' },
    { value: 'PH', label: 'pH — Acidity' },
]

const TYPE_TO_UNIT = {
    TEMPERATURE: 'CELSIUS',
    MOISTURE: 'PERCENT',
    PH: 'PH',
}

export default function Measurements() {
    const toast = useToast()
    const [searchParams] = useSearchParams()
    const prefillSensorId = searchParams.get('sensorId') || ''
    const prefillFieldId = searchParams.get('fieldId') || ''

    const [measurements, setMeasurements] = useState([])
    const [loading, setLoading] = useState(false)
    const [error, setError] = useState(null)
    const [page, setPage] = useState(1)
    const [totalPages, setTotalPages] = useState(null)

    // Sensors list for dropdowns
    const [sensors, setSensors] = useState([])
    useEffect(() => {
        getSensors()
            .then(data => setSensors(extractList(data)))
            .catch(() => { })
    }, [])

    // Filters
    const [sensorIdFilter, setSensorIdFilter] = useState(prefillSensorId)
    const [fieldId, setFieldId] = useState(prefillFieldId)
    const [from, setFrom] = useState('')
    const [to, setTo] = useState('')
    const [size, setSize] = useState(20)

    // Form
    const [form, setForm] = useState({ sensorId: prefillSensorId || prefillFieldId, value: '', unit: '' })
    const [formLoading, setFormLoading] = useState(false)
    const [formSuccess, setFormSuccess] = useState(false)
    const [formError, setFormError] = useState(null)

    const fetchMeasurements = useCallback(async () => {
        setLoading(true); setError(null)
        try {
            const params = { page, size }
            if (sensorIdFilter) params.sensorId = sensorIdFilter
            else if (fieldId) params.fieldId = fieldId
            if (from) params.from = new Date(from).toISOString()
            if (to) params.to = new Date(to).toISOString()
            const data = await getMeasurements(params)
            setMeasurements(extractList(data))
            const pag = extractPagination(data)
            setTotalPages(pag.totalPages || null)
        } catch (e) {
            setError(e.message)
        } finally {
            setLoading(false)
        }
    }, [sensorIdFilter, fieldId, from, to, page, size])

    useEffect(() => { fetchMeasurements() }, [fetchMeasurements])

    async function handlePost(e) {
        e.preventDefault()
        if (!form.sensorId?.trim()) { toast.warning('Please select a sensor.'); return }
        if (!form.value) { toast.warning('Value is required.'); return }
        setFormLoading(true); setFormError(null); setFormSuccess(false)
        try {
            await postMeasurement({ sensorId: form.sensorId, value: Number(form.value), unit: form.unit })
            setFormSuccess(true)
            toast.success('Measurement submitted successfully!')
            window.dispatchEvent(new Event('alertsChanged'))
            setTimeout(() => setFormSuccess(false), 4000)
            setForm(f => ({ ...f, value: '' }))
            if (!fieldId || fieldId === form.sensorId) fetchMeasurements()
        } catch (e) {
            toast.error(e.message)
            setFormError(e.message)
        } finally {
            setFormLoading(false)
        }
    }

    return (
        <div>
            <div className="page-header">
                <div>
                    <h2 className="page-title">Measurements</h2>
                    <p className="page-subtitle">Query sensor measurements and ingest new data</p>
                </div>
            </div>

            {/* Add Measurement Form */}
            <div className="card" style={{ marginBottom: 24 }}>
                <div className="card-header">
                    <span className="card-title">➕ Add Measurement</span>
                </div>
                <form onSubmit={handlePost}>
                    {formError && <div className="error-state" style={{ marginBottom: 16 }}><div className="error-text">{formError}</div></div>}
                    {formSuccess && (
                        <div className="mt-2 mb-4 text-success" style={{ display: 'flex', alignItems: 'center', gap: 8 }}>
                            ✅ Measurement processed successfully!
                        </div>
                    )}
                    <div className="form-row">
                        <div className="form-group">
                            <label className="form-label">Sensor *</label>
                            <select className="form-select"
                                value={form.sensorId}
                                onChange={e => {
                                    const id = e.target.value
                                    const sensor = sensors.find(s => String(s.id) === id)
                                    const unit = sensor ? (TYPE_TO_UNIT[sensor.type] || '') : ''
                                    setForm(f => ({ ...f, sensorId: id, unit }))
                                }}>
                                <option value="">— Select sensor —</option>
                                {sensors.map(s => (
                                    <option key={s.id} value={s.id}>
                                        {s.name || `Sensor ${s.id}`} (#{s.id})
                                    </option>
                                ))}
                            </select>
                        </div>
                        <div className="form-group">
                            <label className="form-label">Value *</label>
                            <input className="form-input" type="number" step="any" placeholder="e.g. 25.4"
                                value={form.value}
                                onChange={e => setForm(f => ({ ...f, value: e.target.value }))} />
                        </div>
                        <div className="form-group">
                            <label className="form-label">Unit</label>
                            <select className="form-select"
                                value={form.unit}
                                onChange={e => setForm(f => ({ ...f, unit: e.target.value }))}>
                                <option value="">— Select unit —</option>
                                {UNIT_OPTIONS.map(u => (
                                    <option key={u.value} value={u.value}>{u.label}</option>
                                ))}
                            </select>
                        </div>
                    </div>
                    <button type="submit" className="btn btn-primary" disabled={formLoading} style={{ marginTop: 8 }}>
                        {formLoading ? '⏳ Sending...' : '📤 Submit Measurement'}
                    </button>
                </form>
            </div>

            {/* Query Filters */}
            <div className="card" style={{ marginBottom: 24 }}>
                <div className="card-header">
                    <span className="card-title">🔍 Query Measurements</span>
                </div>
                <div className="filters-bar">
                    <div className="form-group" style={{ flex: 1, minWidth: 180 }}>
                        <label className="form-label">Sensor</label>
                        <select className="form-select"
                            value={fieldId}
                            onChange={e => { setFieldId(e.target.value); setPage(1) }}>
                            <option value="">All sensors</option>
                            {sensors.map(s => (
                                <option key={s.id} value={s.id}>
                                    {s.name || `Sensor ${s.id}`} (#{s.id})
                                </option>
                            ))}
                        </select>
                    </div>
                    <div className="form-group" style={{ flex: 1, minWidth: 160 }}>
                        <label className="form-label">From</label>
                        <input className="form-input" type="datetime-local"
                            value={from} onChange={e => { setFrom(e.target.value); setPage(1) }} />
                    </div>
                    <div className="form-group" style={{ flex: 1, minWidth: 160 }}>
                        <label className="form-label">To</label>
                        <input className="form-input" type="datetime-local"
                            value={to} onChange={e => { setTo(e.target.value); setPage(1) }} />
                    </div>
                    <div className="form-group" style={{ minWidth: 100 }}>
                        <label className="form-label">Page Size</label>
                        <select className="form-select" value={size} onChange={e => { setSize(Number(e.target.value)); setPage(1) }}>
                            <option value={10}>10</option>
                            <option value={20}>20</option>
                            <option value={50}>50</option>
                        </select>
                    </div>
                    <button className="btn btn-primary btn-sm" onClick={() => { setPage(1); fetchMeasurements() }}
                        style={{ marginTop: 'auto', marginBottom: 0, height: 36 }}>
                        🔍 Search
                    </button>
                </div>

                {loading && <Loader text="Fetching measurements..." />}
                {error && <ErrorState message={error} onRetry={fetchMeasurements} />}
                {!loading && !error && measurements.length === 0 && (
                    <EmptyState icon="📈" title="No measurements" description="No measurements found for the given filters." />
                )}
                {!loading && !error && measurements.length > 0 && (
                    <>
                        <div className="table-wrapper">
                            <table>
                                <thead>
                                    <tr>
                                        <th>Sensor / Field ID</th>
                                        <th>Value</th>
                                        <th>Unit</th>
                                        <th>Timestamp</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    {measurements.map((m, i) => (
                                        <tr key={m.id || i}>
                                            <td className="text-muted text-sm">{m.sensorId || m.fieldId || '—'}</td>
                                            <td style={{ fontWeight: 600 }}>{formatValue(m.value, '')}</td>
                                            <td>{m.unit || '—'}</td>
                                            <td className="text-muted text-sm">{formatDateTime(m.timestamp || m.createdAt || m.measuredAt)}</td>
                                        </tr>
                                    ))}
                                </tbody>
                            </table>
                        </div>
                        <Pagination
                            page={page}
                            totalPages={totalPages}
                            onPrev={() => setPage(p => Math.max(1, p - 1))}
                            onNext={() => setPage(p => p + 1)}
                        />
                    </>
                )}
            </div>
        </div>
    )
}
