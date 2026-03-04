import { BrowserRouter } from 'react-router-dom'
import AppRouter from './routes/AppRouter'
import AppLayout from './components/layout/AppLayout'
import { ToastProvider } from './components/common/Toast'

function App() {
    return (
        <BrowserRouter>
            <ToastProvider>
                <AppLayout>
                    <AppRouter />
                </AppLayout>
            </ToastProvider>
        </BrowserRouter>
    )
}

export default App
