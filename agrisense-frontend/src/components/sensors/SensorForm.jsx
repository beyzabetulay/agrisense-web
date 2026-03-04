import { useState, useEffect } from 'react'
import Modal from '../common/Modal'
import { getFields, createField } from '../../api/fields'

// Backend ESensorType enum değerleri (sadece bunlar kabul edilir)
const SENSOR_TYPES = [
    { value: 'TEMPERATURE', label: '🌡️ Temperature' },
    { value: 'MOISTURE', label: '💧 Moisture' },
    { value: 'PH', label: '🧪 pH' },
]

// Backend ESensorStatus enum değerleri
const STATUS_OPTIONS = [
    { value: 'ACTIVE', label: '✅ Active' },
    { value: 'INACTIVE', label: '⏸️ Inactive' },
    { value: 'MALFUNCTION', label: '⚠️ Malfunction' },
]

const INITIAL = { name: '', type: '', status: 'ACTIVE', apiKey: '', fieldId: '' }

export default function SensorForm({ isOpen, onClose, onSubmit, initial, title }) {
    const [form, setForm] = useState(initial || INITIAL)
    const [loading, setLoading] = useState(false)
    const [error, setError] = useState(null)
    const [fields, setFields] = useState([])
    const [showNewField, setShowNewField] = useState(false)
    const [newFieldName, setNewFieldName] = useState('')
    const [newFieldLocation, setNewFieldLocation] = useState('')
    const [creatingField, setCreatingField] = useState(false)

    // Modal her açıldığında formu sıfırla / doldur
    useEffect(() => {
        if (isOpen) {
            setForm(initial ? {
                name: initial.name || '',
                type: initial.type || '',
                status: initial.status || 'ACTIVE',
                apiKey: initial.apiKey || '',
                fieldId: initial.fieldId != null ? String(initial.fieldId) : '',
            } : INITIAL)
            setError(null)
            setShowNewField(false)
            fetchFields()
        }
    }, [isOpen, initial])

    async function fetchFields() {
        try {
            const data = await getFields()
            setFields(Array.isArray(data) ? data : [])
        } catch {
            setFields([])
        }
    }

    function handle(e) {
        setForm(f => ({ ...f, [e.target.name]: e.target.value }))
    }

    async function handleCreateField() {
        if (!newFieldName.trim()) return
        setCreatingField(true)
        try {
            const created = await createField({ name: newFieldName.trim(), location: newFieldLocation.trim() })
            await fetchFields()
            setForm(f => ({ ...f, fieldId: String(created.id) }))
            setShowNewField(false)
            setNewFieldName('')
            setNewFieldLocation('')
        } catch (err) {
            setError(err.message || 'Failed to create field.')
        } finally {
            setCreatingField(false)
        }
    }

    async function submit(e) {
        e.preventDefault()
        if (!form.name?.trim()) { setError('Sensor name is required.'); return }
        if (!form.type) { setError('Sensor type is required.'); return }
        if (!form.apiKey?.trim()) { setError('API key is required.'); return }
        if (!form.fieldId) { setError('Field is required.'); return }

        setLoading(true); setError(null)
        try {
            await onSubmit({
                name: form.name.trim(),
                type: form.type,
                status: form.status || 'ACTIVE',
                apiKey: form.apiKey.trim(),
                fieldId: Number(form.fieldId),
            })
            onClose()
        } catch (err) {
            setError(err.message || 'Failed to save sensor.')
        } finally {
            setLoading(false)
        }
    }

    const isEdit = !!initial

    return (
        <Modal isOpen={isOpen} onClose={onClose} title={title || 'Sensor'}
            footer={
                <>
                    <button className="btn btn-secondary" onClick={onClose} disabled={loading}>Cancel</button>
                    <button className="btn btn-primary" onClick={submit} disabled={loading}>
                        {loading ? '⏳ Saving...' : '💾 Save'}
                    </button>
                </>
            }
        >
            {error && (
                <div className="error-state" style={{ marginBottom: 16 }}>
                    <div style={{ display: 'flex', alignItems: 'center', gap: 8 }}>
                        <span>⚠️</span>
                        <span className="error-text">{error}</span>
                    </div>
                </div>
            )}

            {/* Name */}
            <div className="form-group">
                <label className="form-label">Name *</label>
                <input
                    className="form-input"
                    name="name"
                    placeholder="e.g. Field Sensor A1"
                    value={form.name}
                    onChange={handle}
                    autoFocus
                />
            </div>

            {/* Type + Status row */}
            <div className="form-row">
                <div className="form-group">
                    <label className="form-label">Type *</label>
                    <select className="form-select" name="type" value={form.type} onChange={handle}>
                        <option value="">— Select type —</option>
                        {SENSOR_TYPES.map(t => (
                            <option key={t.value} value={t.value}>{t.label}</option>
                        ))}
                    </select>
                </div>
                <div className="form-group">
                    <label className="form-label">Status</label>
                    <select className="form-select" name="status" value={form.status} onChange={handle}>
                        {STATUS_OPTIONS.map(s => (
                            <option key={s.value} value={s.value}>{s.label}</option>
                        ))}
                    </select>
                </div>
            </div>

            {/* API Key */}
            <div className="form-group">
                <label className="form-label">API Key *</label>
                <input
                    className="form-input"
                    name="apiKey"
                    placeholder="e.g. sensor-api-key-abc123"
                    value={form.apiKey}
                    onChange={handle}
                    style={{ fontFamily: 'var(--font-mono)', fontSize: '0.82rem' }}
                />
                {isEdit && (
                    <span className="form-hint">Existing key pre-filled — change only if needed.</span>
                )}
            </div>

            {/* Field selection */}
            <div className="form-group">
                <label className="form-label">Field *</label>
                <div style={{ display: 'flex', gap: 8, alignItems: 'center' }}>
                    <select
                        className="form-select"
                        name="fieldId"
                        value={form.fieldId}
                        onChange={handle}
                        style={{ flex: 1 }}
                    >
                        <option value="">— Select field —</option>
                        {fields.map(f => (
                            <option key={f.id} value={f.id}>
                                {f.name}{f.location ? ` (${f.location})` : ''}
                            </option>
                        ))}
                    </select>
                    <button
                        type="button"
                        className="btn btn-secondary btn-sm"
                        onClick={() => setShowNewField(!showNewField)}
                        style={{ whiteSpace: 'nowrap' }}
                    >
                        + New Field
                    </button>
                </div>

                {/* Inline new field form */}
                {showNewField && (
                    <div style={{
                        marginTop: 8,
                        padding: 12,
                        borderRadius: 8,
                        background: 'var(--color-surface, #1e293b)',
                        border: '1px solid var(--color-border, #334155)',
                    }}>
                        <div className="form-group" style={{ marginBottom: 8 }}>
                            <input
                                className="form-input"
                                placeholder="Field name *"
                                value={newFieldName}
                                onChange={e => setNewFieldName(e.target.value)}
                            />
                        </div>
                        <div className="form-group" style={{ marginBottom: 8 }}>
                            <input
                                className="form-input"
                                placeholder="Location (optional)"
                                value={newFieldLocation}
                                onChange={e => setNewFieldLocation(e.target.value)}
                            />
                        </div>
                        <button
                            type="button"
                            className="btn btn-primary btn-sm"
                            onClick={handleCreateField}
                            disabled={creatingField || !newFieldName.trim()}
                        >
                            {creatingField ? '⏳ Creating...' : '✅ Create Field'}
                        </button>
                    </div>
                )}
                <span className="form-hint">The field/farm this sensor belongs to.</span>
            </div>
        </Modal>
    )
}

