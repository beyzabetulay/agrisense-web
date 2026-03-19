import { useState, useEffect } from 'react'
import { Link } from 'react-router-dom'
import { getSensors } from '../api/sensors'
import { getAlerts } from '../api/alerts'
import { getMeasurements } from '../api/measurements'
import { extractList } from '../utils/apiShape'
import { formatDateTime } from '../utils/format'
import { AreaChart, Area, XAxis, YAxis, Tooltip, ResponsiveContainer } from 'recharts'
import Badge from '../components/common/Badge'
import Loader from '../components/common/Loader'
import ErrorState from '../components/common/ErrorState'

const CHART_COLORS = {
    TEMPERATURE: { stroke: '#ef6c35', fill: 'rgba(239,108,53,0.15)' },
    MOISTURE: { stroke: '#58a6ff', fill: 'rgba(88,166,255,0.15)' },
    PH: { stroke: '#a371f7', fill: 'rgba(163,113,247,0.15)' },
    DEFAULT: { stroke: '#2d8a4e', fill: 'rgba(45,138,78,0.15)' },
}

const UNIT_LABELS = { CELSIUS: '°C', PERCENT: '%', PH: 'pH' }

function CustomTooltip({ active, payload }) {
    if (!active || !payload?.length) return null
    const d = payload[0].payload
    return (
        <div style={{
            background: 'var(--color-surface-2)',
            border: '1px solid var(--color-border)',
            borderRadius: 8,
            padding: '8px 12px',
            fontSize: '0.78rem',
            boxShadow: '0 4px 12px rgba(0,0,0,0.4)',
        }}>
            <div style={{ color: 'var(--color-text)', fontWeight: 600, fontFamily: 'var(--font-mono)' }}>
                {d.value}{UNIT_LABELS[d.unit] || ''}
            </div>
            <div style={{ color: 'var(--color-text-faint)', fontSize: '0.72rem', marginTop: 2 }}>
                {d.time}
            </div>
        </div>
    )
}

export default function Dashboard() {
    const [sensorCount, setSensorCount] = useState(null)
    const [sensors, setSensors] = useState([])
    const [openAlerts, setOpenAlerts] = useState([])
    const [recentAlerts, setRecentAlerts] = useState([])
    const [loading, setLoading] = useState(true)
    const [error, setError] = useState(null)
    const [chartData, setChartData] = useState({})

    useEffect(() => {
        async function load() {
            setLoading(true); setError(null)
            try {
                const [sensorsData, alertsData] = await Promise.all([
                    getSensors().catch(() => []),
                    getAlerts({ status: 'OPEN', size: 5 }).catch(() => []),
                ])
                const sensorList = extractList(sensorsData)
                const open = extractList(alertsData)
                setSensors(sensorList)
                setSensorCount(sensorList.length)
                setOpenAlerts(open)

                const recentData = await getAlerts({ size: 5 }).catch(() => [])
                setRecentAlerts(extractList(recentData))

                // Fetch measurements for each sensor (last 20 readings)
                const charts = {}
                await Promise.all(sensorList.map(async (s) => {
                    try {
                        const mData = await getMeasurements({ sensorId: s.id, size: 20 })
                        const list = extractList(mData)
                        charts[s.id] = list.reverse().map(m => ({
                            value: Number(m.value),
                            unit: m.unit || '',
                            time: new Date(m.timestamp || m.createdAt).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' }),
                        }))
                    } catch {
                        charts[s.id] = []
                    }
                }))
                setChartData(charts)
            } catch (e) {
                setError(e.message)
            } finally {
                setLoading(false)
            }
        }
        load()
    }, [])

    if (loading) return <Loader text="Loading dashboard..." />
    if (error) return <ErrorState message={error} onRetry={() => window.location.reload()} />

    return (
        <div>
            {/* Page Header */}
            <div className="page-header">
                <div>
                    <h2 className="page-title">Dashboard</h2>
                    <p className="page-subtitle">AgriSense IoT monitoring overview</p>
                </div>
            </div>

            {/* KPI Cards */}
            <div className="kpi-grid">
                <Link to="/sensors" style={{ textDecoration: 'none', color: 'inherit' }}>
                    <div className="kpi-card">
                        <div className="kpi-card-top">
                            <div className="kpi-icon green">📡</div>
                        </div>
                        <div className="kpi-label">Total Sensors</div>
                        <div className="kpi-value">{sensorCount ?? '—'}</div>
                        <div className="kpi-change">Registered IoT devices</div>
                    </div>
                </Link>

                <Link to="/alerts/open" style={{ textDecoration: 'none', color: 'inherit' }}>
                    <div className="kpi-card">
                        <div className="kpi-card-top">
                            <div className="kpi-icon orange">⚠️</div>
                        </div>
                        <div className="kpi-label">Open Alerts</div>
                        <div className="kpi-value" style={{
                            color: openAlerts.length > 0 ? 'var(--color-warning)' : 'var(--color-text)'
                        }}>
                            {openAlerts.length}
                        </div>
                        <div className="kpi-change">Requires attention</div>
                    </div>
                </Link>

                <Link to="/measurements" style={{ textDecoration: 'none', color: 'inherit' }}>
                    <div className="kpi-card">
                        <div className="kpi-card-top">
                            <div className="kpi-icon green">🌱</div>
                        </div>
                        <div className="kpi-label">Platform</div>
                        <div className="kpi-value" style={{ fontSize: '1.3rem', letterSpacing: '-0.5px' }}>AgriSense</div>
                        <div className="kpi-change">v1.0.0 · Local Mode</div>
                    </div>
                </Link>
            </div>

            {/* Sensor Mini Charts */}
            {sensors.length > 0 && (
                <div className="card" style={{ marginBottom: 20 }}>
                    <div className="card-header">
                        <span className="card-title">
                            <span className="card-icon">📊</span>
                            Sensor Readings
                        </span>
                        <Link to="/measurements" className="btn btn-ghost btn-sm">View All →</Link>
                    </div>
                    <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fill, minmax(300px, 1fr))', gap: 16 }}>
                        {sensors.map(s => {
                            const data = chartData[s.id] || []
                            const colors = CHART_COLORS[s.type] || CHART_COLORS.DEFAULT
                            const name = s.name || `Sensor ${s.id}`
                            return (
                                <Link key={s.id} to={`/sensors/${s.id}`} style={{ textDecoration: 'none', color: 'inherit' }}>
                                    <div style={{
                                        background: 'var(--color-surface-2)',
                                        border: '1px solid var(--color-border)',
                                        borderRadius: 'var(--radius-md)',
                                        padding: '16px',
                                        transition: 'border-color 0.2s',
                                    }}
                                        onMouseEnter={e => e.currentTarget.style.borderColor = 'var(--color-border-hover)'}
                                        onMouseLeave={e => e.currentTarget.style.borderColor = 'var(--color-border)'}
                                    >
                                        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 12 }}>
                                            <div>
                                                <div style={{ fontSize: '0.85rem', fontWeight: 600, color: 'var(--color-text)' }}>{name}</div>
                                                <div style={{ fontSize: '0.72rem', color: 'var(--color-text-faint)' }}>{s.type}</div>
                                            </div>
                                            {data.length > 0 && (
                                                <div style={{
                                                    fontFamily: 'var(--font-mono)',
                                                    fontWeight: 700,
                                                    fontSize: '1.1rem',
                                                    color: colors.stroke,
                                                }}>
                                                    {data[data.length - 1].value}{UNIT_LABELS[data[data.length - 1].unit] || ''}
                                                </div>
                                            )}
                                        </div>
                                        {data.length > 1 ? (
                                            <ResponsiveContainer width="100%" height={80}>
                                                <AreaChart data={data}>
                                                    <defs>
                                                        <linearGradient id={`grad-${s.id}`} x1="0" y1="0" x2="0" y2="1">
                                                            <stop offset="0%" stopColor={colors.stroke} stopOpacity={0.3} />
                                                            <stop offset="100%" stopColor={colors.stroke} stopOpacity={0} />
                                                        </linearGradient>
                                                    </defs>
                                                    <XAxis dataKey="time" hide />
                                                    <YAxis hide domain={['dataMin - 1', 'dataMax + 1']} />
                                                    <Tooltip content={<CustomTooltip />} />
                                                    <Area
                                                        type="monotone"
                                                        dataKey="value"
                                                        stroke={colors.stroke}
                                                        strokeWidth={2}
                                                        fill={`url(#grad-${s.id})`}
                                                        dot={false}
                                                        activeDot={{ r: 4, fill: colors.stroke }}
                                                    />
                                                </AreaChart>
                                            </ResponsiveContainer>
                                        ) : (
                                            <div style={{
                                                height: 80,
                                                display: 'flex',
                                                alignItems: 'center',
                                                justifyContent: 'center',
                                                color: 'var(--color-text-faint)',
                                                fontSize: '0.78rem',
                                            }}>
                                                {data.length === 1 ? '1 reading — need more for chart' : 'No data yet'}
                                            </div>
                                        )}
                                    </div>
                                </Link>
                            )
                        })}
                    </div>
                </div>
            )}

            {/* Quick Actions */}
            <div className="card" style={{ marginBottom: 20 }}>
                <div className="card-header">
                    <span className="card-title">
                        <span className="card-icon">⚡</span>
                        Quick Actions
                    </span>
                </div>
                <div className="btn-group">
                    <Link to="/sensors" className="btn btn-secondary">📡 Manage Sensors</Link>
                    <Link to="/alerts/open" className="btn btn-secondary">⚠️ Open Alerts</Link>
                    <Link to="/measurements" className="btn btn-secondary">📈 Measurements</Link>
                    <Link to="/settings" className="btn btn-ghost">⚙️ Settings</Link>
                </div>
            </div>

            {/* Recent Alerts */}
            <div className="card">
                <div className="card-header">
                    <span className="card-title">
                        <span className="card-icon">🔔</span>
                        Recent Alerts
                    </span>
                    <Link to="/alerts" className="btn btn-ghost btn-sm">View All →</Link>
                </div>

                {recentAlerts.length === 0 ? (
                    <div style={{
                        padding: '40px 0',
                        textAlign: 'center',
                        color: 'var(--color-text-muted)',
                        fontSize: '0.875rem',
                    }}>
                        <div style={{ fontSize: '2rem', marginBottom: 10, opacity: 0.4 }}>🔔</div>
                        No recent alerts — everything looks good!
                    </div>
                ) : (
                    <div className="table-wrapper">
                        <table>
                            <thead>
                                <tr>
                                    <th>Status</th>
                                    <th>Message</th>
                                    <th>Sensor</th>
                                    <th>Time</th>
                                </tr>
                            </thead>
                            <tbody>
                                {recentAlerts.map((a, i) => (
                                    <tr key={a.id || i}
                                        className={
                                            a.status === 'OPEN' ? 'alert-row-open'
                                                : a.status === 'CLOSED' || a.status === 'RESOLVED' ? 'alert-row-closed'
                                                    : 'alert-row-error'
                                        }
                                    >
                                        <td><Badge status={a.status} /></td>
                                        <td style={{ maxWidth: 260 }}>
                                            <span className="truncate">{a.message || '—'}</span>
                                        </td>
                                        <td className="text-muted text-sm text-mono">
                                            {a.sensorId || a.fieldId || '—'}
                                        </td>
                                        <td className="text-muted text-sm">
                                            {formatDateTime(a.createdAt)}
                                        </td>
                                    </tr>
                                ))}
                            </tbody>
                        </table>
                    </div>
                )}
            </div>
        </div>
    )
}
