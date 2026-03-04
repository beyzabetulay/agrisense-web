export default function Profile() {
    return (
        <div>
            <div className="page-header">
                <div>
                    <h2 className="page-title">Profile</h2>
                    <p className="page-subtitle">User profile and preferences</p>
                </div>
            </div>
            <div className="card" style={{ maxWidth: 480 }}>
                <div className="card-header">
                    <span className="card-title">👤 User Profile</span>
                </div>
                <div className="flex items-center gap-3" style={{ marginBottom: 16 }}>
                    <div style={{
                        width: 56, height: 56, borderRadius: '50%',
                        background: 'linear-gradient(135deg, var(--color-primary), var(--color-accent))',
                        display: 'flex', alignItems: 'center', justifyContent: 'center',
                        fontSize: '1.5rem', flexShrink: 0
                    }}>🌱</div>
                    <div>
                        <div className="font-semibold">Local User</div>
                        <div className="text-muted text-sm">Local mode — no authentication required</div>
                    </div>
                </div>
                <div className="divider" />
                <div className="text-muted text-sm">
                    AgriSense is running in <strong>local mode</strong>. All data is fetched directly from the configured backend URL.
                </div>
            </div>
        </div>
    )
}
