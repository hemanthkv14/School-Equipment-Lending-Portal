import { BrowserRouter as Router, Routes, Route } from "react-router-dom";
import Login from "./components/Login";
import AdminDashboard from "./components/AdminDashboard";
import StaffDashboard from "./components/StaffDashboard";
import StudentDashboard from "./components/StudentDashboard";
import ProtectedRoute from "./components/ProtectedRoute";
import RegisterUser from "./components/RegisterUser";

function App() {
    return (
        <Router>
            <Routes>
                {/* Public Routes */}
                <Route path="/" element={<Login />} />
                <Route path="/register-student" element={<RegisterUser roleRestriction="student" />} />

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
        </Router>
    );
}

export default App;
