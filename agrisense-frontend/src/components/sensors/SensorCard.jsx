import { useState, useEffect } from 'react'
import { Link } from 'react-router-dom'
import { getMeasurements } from '../../api/measurements'
import { extractList } from '../../utils/apiShape'
import { formatValue } from '../../utils/format'
import Badge from '../common/Badge'

const typeIcons = {
    TEMPERATURE: '🌡️',
    MOISTURE: '💧',
    PH: '🧪',
}

const UNIT_LABELS = {
    CELSIUS: '°C',
    PERCENT: '%',
    PH: 'pH',
}

function timeAgo(dateStr) {
    if (!dateStr) return ''
    const diff = Date.now() - new Date(dateStr).getTime()
    const mins = Math.floor(diff / 60000)
    if (mins < 1) return 'just now'
    if (mins < 60) return `${mins}m ago`
    const hours = Math.floor(mins / 60)
    if (hours < 24) return `${hours}h ago`
    const days = Math.floor(hours / 24)
    return `${days}d ago`
}

export default function SensorCard({ sensor, onEdit, onDelete }) {
    const name = sensor.name || `Sensor ${sensor.id}`
    const type = sensor.type || '—'
    const status = sensor.status || '—'
    const icon = typeIcons[type] || '📡'
    const fieldId = sensor.fieldId != null ? String(sensor.fieldId) : '—'

    const [lastMeasurement, setLastMeasurement] = useState(null)

    useEffect(() => {
        getMeasurements({ sensorId: sensor.id, size: 1 })
            .then(data => {
                const list = extractList(data)
                if (list.length > 0) setLastMeasurement(list[0])
            })
            .catch(() => { })
    }, [sensor.id])

    return (
        <div className="sensor-card">
            {/* Header */}
            <div className="sensor-card-header">
                <div className="sensor-card-icon">{icon}</div>
                <div style={{ flex: 1, minWidth: 0 }}>
                    <div style={{ display: 'flex', alignItems: 'center', gap: 8 }}>
                        <span className="sensor-card-title truncate">{name}</span>
                        <Badge status={status} />
                    </div>
                    <div className="sensor-card-id">#{sensor.id}</div>
                </div>
            </div>

            {/* Type */}
            <div className="sensor-card-type">
                <span style={{ opacity: 0.5 }}>●</span>
                {type}
            </div>

            {/* Last Measurement */}
            {lastMeasurement ? (
                <div className="sensor-last-measurement">
                    <span>Last:</span>
                    <span className="sensor-last-value">
                        {formatValue(lastMeasurement.value)}{UNIT_LABELS[lastMeasurement.unit] || ''}
                    </span>
                    <span className="sensor-last-time">
                        {timeAgo(lastMeasurement.timestamp || lastMeasurement.createdAt)}
                    </span>
                </div>
            ) : (
                <div className="sensor-last-measurement">
                    <span style={{ opacity: 0.5 }}>No measurements yet</span>
                </div>
            )}

            {/* Meta */}
            <div className="sensor-card-meta">
                <div className="sensor-meta-row">
                    <span className="sensor-meta-label">Field ID</span>
                    <span className="sensor-meta-value">{fieldId}</span>
                </div>
                <div className="sensor-meta-row">
                    <span className="sensor-meta-label">API Key</span>
                    <span className="sensor-meta-value" style={{
                        maxWidth: 130,
                        overflow: 'hidden',
                        textOverflow: 'ellipsis',
                        whiteSpace: 'nowrap',
                        display: 'inline-block',
                    }}>
                        {sensor.apiKey || '—'}
                    </span>
                </div>
            </div>

            {/* Actions */}
            <div className="sensor-card-actions">
                <Link to={`/sensors/${sensor.id}`} className="btn btn-ghost btn-sm" style={{ flex: 1, justifyContent: 'center' }}>
                    🔍 Detail
                </Link>
                <button className="btn btn-ghost btn-sm" onClick={() => onEdit(sensor)} title="Edit">✏️</button>
                <button className="btn btn-danger btn-sm" onClick={() => onDelete(sensor)} title="Delete">🗑️</button>
            </div>
        </div>
    )
}
