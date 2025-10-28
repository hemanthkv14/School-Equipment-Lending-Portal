import React, { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import "../css/Login.css";

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
