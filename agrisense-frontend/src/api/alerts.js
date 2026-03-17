import client from './client'

export const getAlerts = (params = {}) =>
    client.get('/api/alerts', { params }).then(r => r.data)

export const closeAlert = (id) =>
    client.put(`/api/alerts/${id}/close`).then(r => r.data)
