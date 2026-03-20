import { Routes, Route, Navigate } from 'react-router-dom'
import Dashboard from '../pages/Dashboard'
import Sensors from '../pages/Sensors'
import SensorDetail from '../pages/SensorDetail'
import Measurements from '../pages/Measurements'
import Alerts from '../pages/Alerts'
import OpenAlerts from '../pages/OpenAlerts'
import Settings from '../pages/Settings'
import Profile from '../pages/Profile'
import NotFound from '../pages/NotFound'

export default function AppRouter() {
    return (
        <Routes>
            <Route path="/" element={<Navigate to="/dashboard" replace />} />
            <Route path="/dashboard" element={<Dashboard />} />
            <Route path="/sensors" element={<Sensors />} />
            <Route path="/sensors/:id" element={<SensorDetail />} />
            <Route path="/measurements" element={<Measurements />} />
            <Route path="/alerts" element={<Alerts />} />
            <Route path="/alerts/open" element={<OpenAlerts />} />
            <Route path="/settings" element={<Settings />} />
            <Route path="/profile" element={<Profile />} />
            <Route path="*" element={<NotFound />} />
        </Routes>
    )
}
