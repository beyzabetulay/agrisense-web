import client from './client'

export const getSensors = () =>
    client.get('/api/sensors').then(r => r.data)

export const getSensorById = (id) =>
    client.get(`/api/sensors/${id}`).then(r => r.data)

export const createSensor = (data) =>
    client.post('/api/sensors', data).then(r => r.data)

export const updateSensor = (id, data) =>
    client.put(`/api/sensors/${id}`, data).then(r => r.data)

export const deleteSensor = (id) =>
    client.delete(`/api/sensors/${id}`).then(r => r.data)
