// Backend AlertRuleResponse: id, sensorId, ruleName, condition, threshold, isActive

const CONDITION_LABELS = {
    GREATER_THAN: '> Greater than',
    LESS_THAN: '< Less than',
    EQUAL: '= Equal to',
    BETWEEN: '↔ Between',
}

export default function RuleList({ rules, onEdit, onDelete }) {
    return (
        <div className="table-wrapper">
            <table>
                <thead>
                    <tr>
                        <th>Name</th>
                        <th>Condition</th>
                        <th>Threshold</th>
                        <th>Status</th>
                        <th>Actions</th>
                    </tr>
                </thead>
                <tbody>
                    {rules.map(rule => (
                        <tr key={rule.id}>
                            <td style={{ fontWeight: 500 }}>
                                {rule.ruleName || rule.name || '—'}
                            </td>
                            <td>
                                <span className="chip">
                                    {CONDITION_LABELS[rule.condition] || rule.condition || '—'}
                                </span>
                            </td>
                            <td style={{ fontWeight: 600, fontFamily: 'var(--font-mono)' }}>
                                {rule.threshold ?? '—'}
                            </td>
                            <td>
                                {rule.isActive !== undefined ? (
                                    <span className={`badge ${rule.isActive ? 'badge-active' : 'badge-inactive'}`}>
                                        <span className="badge-dot"></span>
                                        {rule.isActive ? 'Active' : 'Inactive'}
                                    </span>
                                ) : '—'}
                            </td>
                            <td>
                                <div className="btn-group">
                                    <button className="btn btn-secondary btn-sm" onClick={() => onEdit(rule)}>✏️</button>
                                    <button className="btn btn-danger btn-sm" onClick={() => onDelete(rule)}>🗑️</button>
                                </div>
                            </td>
                        </tr>
                    ))}
                </tbody>
            </table>
        </div>
    )
}
