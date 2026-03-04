import { createContext, useContext, useState, useCallback, useRef, useMemo } from 'react'

const ToastContext = createContext(null)

let toastIdCounter = 0

export function ToastProvider({ children }) {
    const [toasts, setToasts] = useState([])
    const timersRef = useRef({})

    const removeToast = useCallback((id) => {
        setToasts(prev => prev.filter(t => t.id !== id))
        if (timersRef.current[id]) {
            clearTimeout(timersRef.current[id])
            delete timersRef.current[id]
        }
    }, [])

    const addToast = useCallback((message, type = 'info', duration = 4000) => {
        const id = ++toastIdCounter
        setToasts(prev => [...prev, { id, message, type }])
        timersRef.current[id] = setTimeout(() => removeToast(id), duration)
        return id
    }, [removeToast])

    const toast = useMemo(() => ({
        success: (msg) => addToast(msg, 'success'),
        error: (msg) => addToast(msg, 'error', 6000),
        info: (msg) => addToast(msg, 'info'),
        warning: (msg) => addToast(msg, 'warning', 5000),
    }), [addToast])

    return (
        <ToastContext.Provider value={toast}>
            {children}
            <div className="toast-container">
                {toasts.map(t => (
                    <div key={t.id} className={`toast toast-${t.type}`} onClick={() => removeToast(t.id)}>
                        <span className="toast-icon">
                            {t.type === 'success' && '✅'}
                            {t.type === 'error' && '❌'}
                            {t.type === 'warning' && '⚠️'}
                            {t.type === 'info' && 'ℹ️'}
                        </span>
                        <span className="toast-message">{t.message}</span>
                        <button className="toast-close" onClick={(e) => { e.stopPropagation(); removeToast(t.id) }}>×</button>
                    </div>
                ))}
            </div>
        </ToastContext.Provider>
    )
}

export function useToast() {
    const ctx = useContext(ToastContext)
    if (!ctx) throw new Error('useToast must be used within ToastProvider')
    return ctx
}
