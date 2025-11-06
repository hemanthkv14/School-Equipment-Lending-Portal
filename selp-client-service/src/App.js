import { BrowserRouter as Router, Routes, Route, Navigate, useLocation } from "react-router-dom";
import Login from "./components/Login";
import AdminDashboard from "./components/AdminDashboard";
import StaffDashboard from "./components/StaffDashboard";
import StudentDashboard from "./components/StudentDashboard";
import ProtectedRoute from "./components/ProtectedRoute";
import RegisterUser from "./components/RegisterUser";
import EquipmentList from "./components/EquipmentList";
import RequestForm from "./components/RequestForm";
import MyRequests from "./components/MyRequests";
import Navbar from "./components/Navbar";

function AppContent() {
    const location = useLocation();
    const hideNavbar = location.pathname === "/" || location.pathname.startsWith("/register");

    return (
        <>
            {!hideNavbar && <Navbar />}
            <Routes>
                {/* Public Routes */}
                <Route path="/" element={<Login />} />
                <Route path="/register-student" element={<RegisterUser roleRestriction="student" />} />
                <Route path="/equipment-list" element={<EquipmentList />} />
                <Route path="/my-requests" element={<MyRequests />} />
                <Route path="/request/:itemId" element={<RequestForm />} />
                <Route path="*" element={<Navigate to="/equipment-list" />} />

                {/* Admin-only registration */}
                <Route
                    path="/register"
                    element={
                        <ProtectedRoute
                            element={RegisterUser}
                            allowedRoles={["admin"]}
                        />
                    }
                />

                {/* Protected Dashboards */}
                <Route
                    path="/student-dashboard"
                    element={
                        <ProtectedRoute
                            element={StudentDashboard}
                            allowedRoles={["student"]}
                        />
                    }
                />
                <Route
                    path="/admin-dashboard"
                    element={
                        <ProtectedRoute
                            element={AdminDashboard}
                            allowedRoles={["admin"]}
                        />
                    }
                />
                <Route
                    path="/staff-dashboard"
                    element={
                        <ProtectedRoute
                            element={StaffDashboard}
                            allowedRoles={["staff"]}
                        />
                    }
                />
            </Routes>
        </>
    );
}

function App() {
    return (
        <Router>
            <AppContent />
        </Router>
    );
}

export default App;