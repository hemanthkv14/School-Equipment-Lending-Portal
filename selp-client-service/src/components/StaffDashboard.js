import React, { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import "../css/Login.css";

const StaffDashboard = () => {
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

    return (
        <div className="full-screen-center">
            <div className="login-panel">
                <h2 className="dashboard-heading">Staff Dashboard</h2>
                <p className="dashboard-content">
                    Hello, <strong>{username || "Staff Member"}</strong> 👋
                </p>
                <p className="dashboard-content">
                    You can approve requests, and monitor allocations here.
                </p>
                <button onClick={handleLogout} className="btn-primary logout-btn">
                    Logout
                </button>
            </div>
        </div>
    );
};

export default StaffDashboard;
