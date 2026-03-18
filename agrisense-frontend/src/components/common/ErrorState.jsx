export default function ErrorState({ message, onRetry }) {
    return (
        <div className="error-state">
            <div style={{ display: 'flex', alignItems: 'center', gap: 10 }}>
                <span style={{ fontSize: '1.25rem' }}>⚠️</span>
                <span className="error-text">{message || 'An error occurred.'}</span>
            </div>
            {onRetry && (
                <button className="btn btn-secondary btn-sm" onClick={onRetry}>
                    🔄 Retry
                </button>
            )}
        </div>
    )
}
