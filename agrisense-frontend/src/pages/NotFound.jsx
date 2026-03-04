import { Link } from 'react-router-dom'

export default function NotFound() {
    return (
        <div className="empty-state" style={{ minHeight: '70vh' }}>
            <div style={{
                fontSize: '5rem',
                fontWeight: 800,
                background: 'linear-gradient(135deg, var(--color-text-faint), var(--color-text-muted))',
                WebkitBackgroundClip: 'text',
                WebkitTextFillColor: 'transparent',
                backgroundClip: 'text',
                lineHeight: 1,
                letterSpacing: '-4px',
                marginBottom: 8,
            }}>
                404
            </div>
            <div className="empty-title">Page Not Found</div>
            <div className="empty-desc">
                The page you're looking for doesn't exist or has been moved.
            </div>
            <Link to="/dashboard" className="btn btn-primary" style={{ marginTop: 12 }}>
                ← Back to Dashboard
            </Link>
        </div>
    )
}
