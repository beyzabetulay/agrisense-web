import { NavLink } from 'react-router-dom'
import { useState, useEffect } from 'react'
import { getAlerts } from '../../api/alerts'

const navItems = [
    {
        section: 'Overview',
        links: [
            { to: '/dashboard', icon: '▦', label: 'Dashboard' },
        ],
    },
    {
        section: 'Management',
        links: [
            { to: '/sensors', icon: '⬡', label: 'Sensors' },
            { to: '/measurements', icon: '∿', label: 'Measurements' },
        ],
    },
    {
        section: 'Alerts',
        links: [
            { to: '/alerts', icon: '◈', label: 'All Alerts' },
            { to: '/alerts/open', icon: '⚠', label: 'Open Alerts', badge: true },
        ],
    },
    {
        section: 'System',
        links: [
            { to: '/settings', icon: '⚙', label: 'Settings' },
            { to: '/profile', icon: '○', label: 'Profile' },
        ],
    },
]

export default function Sidebar({ isOpen, onClose }) {
    const [openAlertCount, setOpenAlertCount] = useState(0)

    useEffect(() => {
        const fetchCount = async () => {
            try {
                const data = await getAlerts({ status: 'OPEN', page: 1, size: 1 })
                setOpenAlertCount(data.totalElements || 0)
            } catch {
                // silently ignore
            }
        }
        fetchCount()
        const interval = setInterval(fetchCount, 30000)
        window.addEventListener('alertsChanged', fetchCount)
        return () => {
            clearInterval(interval)
            window.removeEventListener('alertsChanged', fetchCount)
        }
    }, [])

    return (
        <aside className={`sidebar ${isOpen ? 'sidebar-open' : ''}`}>
            {/* Logo */}
            <div className="sidebar-logo">
                <div className="sidebar-logo-icon">🌱</div>
                <div>
                    <div className="sidebar-logo-text">AgriSense</div>
                    <div className="sidebar-logo-sub">IoT Platform</div>
                </div>
            </div>

            {/* System status */}
            <div style={{ padding: '12px 12px 4px' }}>
                <div className="sidebar-status">
                    <span className="sidebar-status-dot" />
                    <span className="sidebar-status-text">System Online</span>
                </div>
            </div>

            {/* Navigation */}
            <nav className="sidebar-nav">
                {navItems.map((group) => (
                    <div key={group.section}>
                        <div className="sidebar-section">{group.section}</div>
                        {group.links.map((link) => (
                            <NavLink
                                key={link.to}
                                to={link.to}
                                className={({ isActive }) => isActive ? 'active' : ''}
                                onClick={onClose}
                            >
                                <span className="sidebar-nav-icon">{link.icon}</span>
                                {link.label}
                                {link.badge && openAlertCount > 0 && (
                                    <span style={{
                                        marginLeft: 'auto',
                                        background: 'rgba(239, 68, 68, 0.15)',
                                        color: '#ef4444',
                                        fontSize: '0.7rem',
                                        fontWeight: 600,
                                        borderRadius: '6px',
                                        padding: '2px 8px',
                                        minWidth: 20,
                                        textAlign: 'center',
                                    }}>
                                        {openAlertCount}
                                    </span>
                                )}
                            </NavLink>
                        ))}
                    </div>
                ))}
            </nav>

            {/* Footer */}
            <div className="sidebar-footer">
                <span>AgriSense Platform</span>
                <span className="sidebar-version">v1.0.0</span>
            </div>
        </aside>
    )
}
