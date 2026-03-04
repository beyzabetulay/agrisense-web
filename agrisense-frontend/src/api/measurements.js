import client from './client'

export const getMeasurements = (params = {}) =>
    client.get('/api/measurements', { params }).then(r => r.data)

export const postMeasurement = (data) =>
    client.post('/api/measurements', data).then(r => r.data)
