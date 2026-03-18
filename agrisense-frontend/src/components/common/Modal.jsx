export default function Modal({ isOpen, onClose, title, children, footer }) {
    if (!isOpen) return null

    return (
        <div className="modal-overlay" onClick={(e) => e.target === e.currentTarget && onClose()}>
            <div className="modal" role="dialog" aria-modal="true">
                <div className="modal-header">
                    <span className="modal-title">{title}</span>
                    <button className="btn btn-ghost btn-icon" onClick={onClose} aria-label="Close">✕</button>
                </div>
                <div className="modal-body">{children}</div>
                {footer && <div className="modal-footer">{footer}</div>}
            </div>
        </div>
    )
}
