import axios from 'axios'

const BASE_URL = import.meta.env.VITE_API_BASE_URL || ''

const client = axios.create({
    baseURL: BASE_URL,
    timeout: 10000,
    headers: {
        'Content-Type': 'application/json',
        'Accept': 'application/json',
    },
})

// Parse backend error into user-friendly message
function parseBackendError(data) {
    if (!data) return null
    // Direct message / error fields
    const raw = data.message || data.error || data.detail || data.title || null
    if (!raw || typeof raw !== 'string') return null

    // SQL constraint violations → friendly text
    if (raw.includes('Referential integrity constraint violation') || raw.includes('FOREIGN KEY')) {
        if (raw.includes('FIELD_ID') || raw.includes('field_id'))
            return 'The selected field does not exist. Please create it first.'
        if (raw.includes('SENSOR_ID') || raw.includes('sensor_id'))
            return 'The selected sensor does not exist.'
        return 'A referenced record does not exist. Please check your selections.'
    }
    if (raw.includes('Unique index or primary key violation') || raw.includes('PRIMARY KEY')) {
        return 'This record already exists. Please try a different value.'
    }
    if (raw.includes('Duplicate') || raw.includes('unique constraint')) {
        return 'A record with this value already exists.'
    }
    if (raw.includes('could not execute statement')) {
        return 'Database error. Please check your input and try again.'
    }
    if (raw.includes('Detached entity')) {
        return 'Record update failed. Please refresh and try again.'
    }
    // Validation errors list
    if (data.violations && Array.isArray(data.violations)) {
        return data.violations.map(v => v.message || v.field).join(', ')
    }
    // If the raw message is too long (SQL dump), truncate
    if (raw.length > 200) {
        return 'An error occurred while processing your request. Please check your input.'
    }
    return raw
}

// Response interceptor — normalize errors
client.interceptors.response.use(
    (response) => response,
    (error) => {
        const status = error.response?.status
        const parsed = parseBackendError(error.response?.data)
        let message = 'An unexpected error occurred.'

        if (error.code === 'ECONNABORTED') {
            message = 'Request timed out. Please check your backend is running.'
        } else if (!error.response) {
            message = 'Cannot connect to backend. Is the server running?'
        } else if (status === 400) {
            message = parsed || 'Bad request. Please check the form fields.'
        } else if (status === 404) {
            message = parsed || 'Resource not found.'
        } else if (status === 409) {
            message = parsed || 'Conflict. The resource may already exist.'
        } else if (status >= 500) {
            message = parsed || 'Server error. Please try again later.'
        } else {
            message = parsed || `Error ${status}`
        }

        const normalized = new Error(message)
        normalized.status = status
        normalized.original = error
        return Promise.reject(normalized)
    }
)

export default client
