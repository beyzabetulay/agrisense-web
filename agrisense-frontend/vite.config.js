import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

export default defineConfig({
    plugins: [react()],
    server: {
        port: 3000,
        proxy: {
            '/api': {
                target: 'http://localhost:8081',
                changeOrigin: true,
            },
            // Only proxy /sensors/*/rules API calls, not SPA routes
            '^/sensors/\\d+/rules': {
                target: 'http://localhost:8081',
                changeOrigin: true,
            },
        },
    },
})
