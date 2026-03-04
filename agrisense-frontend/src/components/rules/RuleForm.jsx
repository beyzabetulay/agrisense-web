import { useState, useEffect } from 'react'
import Modal from '../common/Modal'

// Backend ECondition enum değerleri (sadece bunlar kabul edilir)
const CONDITIONS = [
    { value: 'GREATER_THAN', label: '> Greater than' },
    { value: 'LESS_THAN', label: '< Less than' },
    { value: 'EQUAL', label: '= Equal to' },
    { value: 'BETWEEN', label: '↔ Between' },
]

const INITIAL = { name: '', condition: 'GREATER_THAN', threshold: '', description: '' }

export default function RuleForm({ isOpen, onClose, onSubmit, initial, title }) {
    const [form, setForm] = useState(INITIAL)
    const [loading, setLoading] = useState(false)
    const [error, setError] = useState(null)

    useEffect(() => {
        if (isOpen) {
            // Backend'den gelen alanlar: id, sensorId, ruleName, condition, threshold, isActive
            setForm(initial ? {
                name: initial.ruleName || initial.name || '',
                condition: initial.condition || 'GREATER_THAN',
                threshold: initial.threshold != null ? String(initial.threshold) : '',
                description: initial.description || '',
            } : INITIAL)
            setError(null)
        }
    }, [isOpen, initial])

    function handle(e) {
        setForm(f => ({ ...f, [e.target.name]: e.target.value }))
    }

    async function submit(e) {
        e.preventDefault()
        if (!form.name?.trim()) { setError('Rule name is required.'); return }
        if (!form.condition) { setError('Condition is required.'); return }
        if (!form.threshold) { setError('Threshold value is required.'); return }
        if (Number(form.threshold) <= 0) { setError('Threshold must be a positive number.'); return }

        setLoading(true); setError(null)
        try {
            // Backend CreateAlertRuleRequest: name, condition, threshold, description
            await onSubmit({
                name: form.name.trim(),
                condition: form.condition,
                threshold: Number(form.threshold),
                description: form.description || '',
            })
            onClose()
        } catch (err) {
            setError(err.message || 'Failed to save rule.')
        } finally {
            setLoading(false)
        }
    }

    return (
        <Modal isOpen={isOpen} onClose={onClose} title={title || 'Alert Rule'}
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

            {/* Rule Name */}
            <div className="form-group">
                <label className="form-label">Rule Name *</label>
                <input
                    className="form-input"
                    name="name"
                    placeholder="e.g. High Temperature Alert"
                    value={form.name}
                    onChange={handle}
                    autoFocus
                />
            </div>

            <div className="form-row">
                {/* Condition */}
                <div className="form-group">
                    <label className="form-label">Condition *</label>
                    <select className="form-select" name="condition" value={form.condition} onChange={handle}>
                        {CONDITIONS.map(c => (
                            <option key={c.value} value={c.value}>{c.label}</option>
                        ))}
                    </select>
                </div>

                {/* Threshold */}
                <div className="form-group">
                    <label className="form-label">Threshold *</label>
                    <input
                        className="form-input"
                        name="threshold"
                        type="number"
                        step="any"
                        min="0.001"
                        placeholder="e.g. 35"
                        value={form.threshold}
                        onChange={handle}
                    />
                </div>
            </div>

            {/* Description */}
            <div className="form-group">
                <label className="form-label">Description</label>
                <input
                    className="form-input"
                    name="description"
                    placeholder="Optional description..."
                    value={form.description}
                    onChange={handle}
                />
            </div>

            <div className="form-hint">
                ⓘ Alert fires when the measured value meets the condition against the threshold.
            </div>
        </Modal>
    )
}
