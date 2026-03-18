export default function Pagination({ page, totalPages, onPrev, onNext, total }) {
    const canPrev = page > 1
    const canNext = totalPages ? page < totalPages : true

    return (
        <div className="pagination">
            <span className="pagination-info">
                Page <strong>{page}</strong>
                {totalPages ? <> of <strong>{totalPages}</strong></> : ''}
                {total != null && <span style={{ marginLeft: 8, opacity: 0.6 }}>({total} total)</span>}
            </span>

            <button
                className="pagination-btn"
                onClick={onPrev}
                disabled={!canPrev}
                aria-label="Previous page"
            >
                ←
            </button>

            {totalPages && totalPages <= 7
                ? Array.from({ length: totalPages }, (_, i) => i + 1).map(p => (
                    <button
                        key={p}
                        className={`pagination-btn ${p === page ? 'active' : ''}`}
                        onClick={() => p !== page && (p < page ? onPrev() : onNext())}
                    >
                        {p}
                    </button>
                ))
                : (
                    <button className="pagination-btn active">{page}</button>
                )
            }

            <button
                className="pagination-btn"
                onClick={onNext}
                disabled={!canNext}
                aria-label="Next page"
            >
                →
            </button>
        </div>
    )
}
