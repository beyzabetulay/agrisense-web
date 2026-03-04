import { useState, useEffect, useCallback } from 'react'
import { getAlerts, closeAlert } from '../api/alerts'
import { extractList, extractPagination } from '../utils/apiShape'
import { useToast } from '../components/common/Toast'
import AlertList from '../components/alerts/AlertList'
import Loader from '../components/common/Loader'
import ErrorState from '../components/common/ErrorState'
import EmptyState from '../components/common/EmptyState'
import Pagination from '../components/common/Pagination'

const STATUS_OPTIONS = ['', 'OPEN', 'RESOLVED', 'CLOSED', 'ACTIVE']

export default function Alerts({ forcedStatus }) {
    const toast = useToast()
    const [alerts, setAlerts] = useState([])
    const [loading, setLoading] = useState(true)
    const [error, setError] = useState(null)
    const [status, setStatus] = useState(forcedStatus || '')
    const [page, setPage] = useState(1)
    const [totalPages, setTotalPages] = useState(null)
    const [size] = useState(20)

    const fetchAlerts = useCallback(async () => {
        setLoading(true); setError(null)
        try {
            const params = { page, size }
            if (status) params.status = status
            const data = await getAlerts(params)
            setAlerts(extractList(data))
            const pag = extractPagination(data)
            setTotalPages(pag.totalPages || null)
        } catch (e) {
            setError(e.message)
        } finally {
            setLoading(false)
        }
    }, [status, page, size])

    useEffect(() => { fetchAlerts() }, [fetchAlerts])

    const handleClose = async (alertId) => {
        try {
            await closeAlert(alertId)
            toast.success('Alert resolved successfully!')
            window.dispatchEvent(new Event('alertsChanged'))
            fetchAlerts()
        } catch (e) {
            toast.error(e.message)
            setError(e.message)
        }
    }

    const isOpenOnly = !!forcedStatus

    return (
        <div>
            <div className="page-header">
                <div>
                    <h2 className="page-title">{isOpenOnly ? 'Open Alerts' : 'Alerts'}</h2>
                    <p className="page-subtitle">
                        {isOpenOnly ? 'Showing only OPEN alerts' : 'All system alerts with filtering'}
                    </p>
                </div>
            </div>

            {/* Filters */}
            {!isOpenOnly && (
                <div className="filters-bar">
                    <div className="form-group" style={{ minWidth: 180 }}>
                        <label className="form-label">Filter by Status</label>
                        <select
                            className="form-select"
                            value={status}
                            onChange={e => { setStatus(e.target.value); setPage(1) }}
                        >
                            {STATUS_OPTIONS.map(s => (
                                <option key={s} value={s}>{s || 'All statuses'}</option>
                            ))}
                        </select>
                    </div>
                    <div className="form-group" style={{ minWidth: 100 }}>
                        <label className="form-label">Page Size</label>
                        <select
                            className="form-select"
                            value={size}
                            onChange={() => setPage(1)}
                        >
                            <option value={10}>10</option>
                            <option value={20}>20</option>
                            <option value={50}>50</option>
                        </select>
                    </div>
                </div>
            )}

            <div className="card">
                {loading && <Loader text="Fetching alerts..." />}
                {error && <ErrorState message={error} onRetry={fetchAlerts} />}
                {!loading && !error && alerts.length === 0 && (
                    <EmptyState icon="🔔" title="No alerts found" description="No alerts match the current filter." />
                )}
                {!loading && !error && alerts.length > 0 && (
                    <>
                        <AlertList alerts={alerts} onClose={handleClose} />
                        <Pagination
                            page={page}
                            totalPages={totalPages}
                            onPrev={() => setPage(p => Math.max(1, p - 1))}
                            onNext={() => setPage(p => p + 1)}
                        />
                    </>
                )}
            </div>
        </div>
    )
}
