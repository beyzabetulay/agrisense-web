const statusMap = {
    OPEN: { cls: 'badge-open', label: 'Open' },
    ACTIVE: { cls: 'badge-active', label: 'Active' },
    RESOLVED: { cls: 'badge-resolved', label: 'Resolved' },
    SUCCESS: { cls: 'badge-success', label: 'Success' },
    CLOSED: { cls: 'badge-closed', label: 'Closed' },
    ERROR: { cls: 'badge-danger', label: 'Error' },
    MALFUNCTION: { cls: 'badge-danger', label: 'Malfunction' },
    INACTIVE: { cls: 'badge-inactive', label: 'Inactive' },
    INFO: { cls: 'badge-info', label: 'Info' },
    UNKNOWN: { cls: 'badge-secondary', label: 'Unknown' },
}

export default function Badge({ status, children, variant }) {
    const key = status?.toUpperCase()
    const mapped = statusMap[key] || { cls: 'badge-secondary', label: status || '—' }
    const cls = variant ? `badge badge-${variant}` : `badge ${mapped.cls}`

    return (
        <span className={cls}>
            <span className="badge-dot" />
            {children || mapped.label}
        </span>
    )
}
