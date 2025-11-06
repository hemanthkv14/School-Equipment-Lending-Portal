import React, { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";

const AdminDashboard = () => {
    const [username, setUsername] = useState("");
    const navigate = useNavigate();

    useEffect(() => {
        const storedUser = localStorage.getItem("username");
        if (storedUser) {
            setUsername(storedUser);
        }
    }, []);

    const handleLogout = () => {
        localStorage.clear();
        navigate("/");
    };

    const goToRegister = () => {
        navigate("/register");
    };

    const viewEquipments = () => {
         navigate("/admin-equipment-list");
    };

    return (
        <div className="full-screen-center">
            <div className="login-panel">
                <h2 className="dashboard-heading">Admin Dashboard</h2>
                <p className="dashboard-content">
                    Welcome back, <strong>{username || "Admin"}</strong> 👋
                </p>
                <p className="dashboard-content">
                    You can manage equipment, users, and view system reports here.
                </p>
                <button onClick={viewEquipments} className="btn-primary logout-btn">
                    Go to equipment dashboard
                </button>
                <button onClick={goToRegister} className="btn-primary" style={{ marginTop: "15px" }}>
                    Register New User
                </button>

                <button onClick={handleLogout} className="btn-primary logout-btn">
                    Logout
                </button>
            </div>
        </div>
    );
};

export default AdminDashboard;
