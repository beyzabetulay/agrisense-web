import { useState, useEffect } from 'react'
import { useLocation } from 'react-router-dom'

const pageMap = {
    '/dashboard': { title: 'Dashboard', icon: '▦' },
    '/sensors': { title: 'Sensors', icon: '⬡' },
    '/measurements': { title: 'Measurements', icon: '∿' },
    '/alerts/open': { title: 'Open Alerts', icon: '⚠' },
    '/alerts': { title: 'Alerts', icon: '◈' },
    '/settings': { title: 'Settings', icon: '⚙' },
    '/profile': { title: 'Profile', icon: '○' },
}

function useClock() {
    const [time, setTime] = useState('')
    useEffect(() => {
        const fmt = () => {
            const now = new Date()
            return now.toLocaleTimeString('en-GB', { hour: '2-digit', minute: '2-digit', second: '2-digit' })
        }
        setTime(fmt())
        const id = setInterval(() => setTime(fmt()), 1000)
        return () => clearInterval(id)
    }, [])
    return time
}

export default function Topbar({ onMenuClick }) {
    const location = useLocation()
    const clock = useClock()

    let page = { title: 'AgriSense', icon: '🌱' }
    for (const [path, info] of Object.entries(pageMap)) {
        if (location.pathname.startsWith(path)) page = info
    }
    if (location.pathname.startsWith('/sensors/') && location.pathname.length > 9) {
        page = { title: 'Sensor Detail', icon: '⬡' }
    }

    return (
        <header className="topbar">
            <button className="hamburger-btn" onClick={onMenuClick} aria-label="Toggle menu">
                ☰
            </button>

            <div className="topbar-breadcrumb">
                <span className="topbar-page-icon" aria-hidden="true">{page.icon}</span>
                <h1 className="topbar-title">{page.title}</h1>
            </div>

            <div className="topbar-actions">
                <span className="topbar-time">{clock}</span>
                <div className="live-indicator">
                    <span className="live-dot" />
                    Live
                </div>
            </div>
        </header>
    )
}
