/**
 * Format a date/time string for display
 */
export function formatDateTime(val) {
    if (!val) return '—'
    try {
        return new Date(val).toLocaleString('en-US', {
            dateStyle: 'medium',
            timeStyle: 'short',
        })
    } catch {
        return String(val)
    }
}

/**
 * Format a numeric value with optional unit
 */
export function formatValue(value, unit) {
    if (value === undefined || value === null) return '—'
    return unit ? `${value} ${unit}` : String(value)
}

/**
 * Truncate a string to maxLen characters
 */
export function truncate(str, maxLen = 50) {
    if (!str) return ''
    return str.length > maxLen ? str.slice(0, maxLen) + '...' : str
}
