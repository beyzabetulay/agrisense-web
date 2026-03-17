/**
 * Tolerant API response shape parser
 * Handles: array, { content: [...] }, { _embedded: { ... } }, plain objects
 */
export function extractList(data) {
    if (!data) return []
    if (Array.isArray(data)) return data
    if (Array.isArray(data.content)) return data.content
    if (data._embedded) {
        const keys = Object.keys(data._embedded)
        if (keys.length > 0) {
            const first = data._embedded[keys[0]]
            if (Array.isArray(first)) return first
        }
    }
    if (data.data && Array.isArray(data.data)) return data.data
    // Fallback: single object wrapped
    return [data]
}

/**
 * Extract pagination metadata from response
 */
export function extractPagination(data) {
    if (!data || Array.isArray(data)) return {}
    return {
        page: data.page ?? data.currentPage ?? 1,
        size: data.size ?? data.pageSize ?? 50,
        totalPages: data.totalPages ?? null,
        totalElements: data.totalElements ?? null,
    }
}
