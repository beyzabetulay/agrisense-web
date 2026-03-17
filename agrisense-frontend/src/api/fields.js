import client from './client'

export const getFields = () =>
    client.get('/api/fields').then(r => r.data)

export const createField = (data) =>
    client.post('/api/fields', data).then(r => r.data)
