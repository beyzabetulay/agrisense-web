import Badge from '../common/Badge'
import { formatDateTime } from '../../utils/format'

export default function AlertList({ alerts, onClose }) {
    if (!alerts.length) return null

    return (
        <div className="table-wrapper">
            <table>
                <thead>
                    <tr>
                        <th>Status</th>
                        <th>Message</th>
                        <th>Sensor</th>
                        <th>Time</th>
                        <th>Actions</th>
                    </tr>
                </thead>
                <tbody>
                    {alerts.map((alert, i) => {
                        const statusKey = (alert.status || '').toUpperCase()
                        const rowCls =
                            statusKey === 'OPEN' ? 'alert-row-open'
                                : statusKey === 'CLOSED' || statusKey === 'RESOLVED' ? 'alert-row-closed'
                                    : statusKey === 'ERROR' || statusKey === 'MALFUNCTION' ? 'alert-row-error'
                                        : ''

                        return (
                            <tr key={alert.id || i} className={rowCls}>
                                <td><Badge status={alert.status} /></td>
                                <td style={{ maxWidth: 300 }}>
                                    <span className="truncate" title={alert.message}>
                                        {alert.message || '—'}
                                    </span>
                                </td>
                                <td className="text-muted text-sm text-mono">
                                    {alert.sensorId || alert.fieldId || '—'}
                                </td>
                                <td className="text-muted text-sm">
                                    {formatDateTime(alert.createdAt || alert.timestamp)}
                                </td>
                                <td>
                                    {statusKey === 'OPEN' && onClose ? (
                                        <button
                                            className="btn btn-secondary btn-sm"
                                            onClick={() => onClose(alert.id)}
                                            title="Resolve this alert"
                                        >
                                            ✅ Resolve
                                        </button>
                                    ) : '—'}
                                </td>
                            </tr>
                        )
                    })}
                </tbody>
            </table>
        </div>
    )
}
