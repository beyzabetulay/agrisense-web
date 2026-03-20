import { useState } from 'react'
import { getSensors } from '../api/sensors'

export default function Settings() {
    const [status, setStatus] = useState(null)
    const [testing, setTesting] = useState(false)

    async function testConnection() {
        setTesting(true)
        setStatus(null)
        try {
            await getSensors()
            setStatus({ ok: true, msg: 'Backend connection successful.' })
        } catch (e) {
            setStatus({ ok: false, msg: e.message || 'Connection failed.' })
        } finally {
            setTesting(false)
        }
    }

    const apiUrl = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080'

    return (
        <div>
            <div className="page-header">
                <div>
                    <h2 className="page-title">Settings</h2>
                    <p className="page-subtitle">API configuration and system preferences</p>
                </div>
            </div>

            <div style={{ display: 'flex', flexDirection: 'column', gap: 16, maxWidth: 600 }}>

                {/* API Config Card */}
                <div className="card">
                    <div className="card-header">
                        <span className="card-title">
                            <span className="card-icon">🔌</span>
                            API Configuration
                        </span>
                    </div>

                    <div style={{ display: 'flex', flexDirection: 'column', gap: 16 }}>
                        <div className="form-group">
                            <label className="form-label">API Base URL</label>
                            <input
                                className="form-input"
                                value={apiUrl}
                                readOnly
                                style={{ fontFamily: 'var(--font-mono)', fontSize: '0.85rem' }}
                            />
                            <span className="form-hint">
                                Set via <code style={{ fontFamily: 'var(--font-mono)', background: 'var(--color-surface-2)', padding: '1px 5px', borderRadius: 3 }}>VITE_API_BASE_URL</code> in .env
                            </span>
                        </div>

                        <div style={{ display: 'flex', alignItems: 'center', gap: 12, flexWrap: 'wrap' }}>
                            <button
                                className="btn btn-primary"
                                onClick={testConnection}
                                disabled={testing}
                                id="test-connection-btn"
                            >
                                {testing ? '⏳ Testing...' : '🔌 Test Connection'}
                            </button>

                            {status && (
                                <div className={`info-strip ${status.ok ? '' : ''}`} style={{
                                    flex: 1,
                                    borderColor: status.ok ? 'rgba(57,211,83,0.25)' : 'rgba(255,107,107,0.25)',
                                    background: status.ok ? 'var(--color-primary-dim)' : 'var(--color-danger-dim)',
                                    color: status.ok ? 'var(--color-primary)' : 'var(--color-danger)',
                                }}>
                                    <span>{status.ok ? '✅' : '❌'}</span>
                                    {status.msg}
                                </div>
                            )}
                        </div>
                    </div>
                </div>

                {/* System Info Card */}
                <div className="card">
                    <div className="card-header">
                        <span className="card-title">
                            <span className="card-icon">🌱</span>
                            System Info
                        </span>
                    </div>
                    <div className="stat-row">
                        <span className="stat-label">Platform</span>
                        <span className="stat-value">AgriSense IoT</span>
                    </div>
                    <div className="stat-row">
                        <span className="stat-label">Version</span>
                        <span className="stat-value" style={{ fontFamily: 'var(--font-mono)', fontSize: '0.83rem' }}>v1.0.0</span>
                    </div>
                    <div className="stat-row">
                        <span className="stat-label">Mode</span>
                        <span className="stat-value">Local Development</span>
                    </div>
                    <div className="stat-row">
                        <span className="stat-label">Backend</span>
                        <span className="stat-value" style={{ fontFamily: 'var(--font-mono)', fontSize: '0.8rem', color: 'var(--color-text-muted)' }}>{apiUrl}</span>
                    </div>
                </div>

            </div>
        </div>
    )
}
