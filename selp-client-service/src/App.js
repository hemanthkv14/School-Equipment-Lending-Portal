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
import RoleRedirect from "./components/RoleRedirect";
import AdminEquipmentList from "./components/AdminEquipmentList";
import AdminRequests from "./components/AdminRequests"; 

function AppContent() {
  const location = useLocation();
  const hideNavbar = location.pathname === "/" || location.pathname.startsWith("/register");

  return (
    <>
      {!hideNavbar && <Navbar />}
      <Routes>
        <Route path="/" element={<Login />} />
        <Route path="/register-student" element={<RegisterUser roleRestriction="student" />} />
        <Route path="*" element={<Navigate to="/equipment-list" />} />

        <Route
          path="/equipment-list"
          element={
            <ProtectedRoute
              element={EquipmentList}
              allowedRoles={["student"]}
            />
          }
        />
        <Route
          path="/my-requests"
          element={
            <ProtectedRoute
              element={MyRequests}
              allowedRoles={["student"]}
            />
          }
        />
        <Route
          path="/request/:itemId"
          element={
            <ProtectedRoute
              element={RequestForm}
              allowedRoles={["student"]}
            />
          }
        />
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
          path="/admin-equipment-list"
          element={
            <ProtectedRoute
              element={AdminEquipmentList}
              allowedRoles={["admin"]}
            />
          }
        />
        <Route
          path="/register"
          element={
            <ProtectedRoute
              element={RegisterUser}
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
        <Route
          path="/admin-requests"
          element={
            <ProtectedRoute
              element={AdminRequests}
              allowedRoles={["admin", "staff"]}
            />
          }
        />
        <Route path="*" element={<RoleRedirect />} />
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