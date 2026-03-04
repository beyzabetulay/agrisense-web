import client from './client'

export const getRules = (sensorId) =>
    client.get(`/sensors/${sensorId}/rules`).then(r => r.data)

export const createRule = (sensorId, data) =>
    client.post(`/sensors/${sensorId}/rules`, data).then(r => r.data)

export const updateRule = (sensorId, ruleId, data) =>
    client.put(`/sensors/${sensorId}/rules/${ruleId}`, data).then(r => r.data)

export const deleteRule = (sensorId, ruleId) =>
    client.delete(`/sensors/${sensorId}/rules/${ruleId}`).then(r => r.data)
