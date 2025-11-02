import React, { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import "../css/Login.css";

const StudentDashboard = () => {
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

    const viewEquipments = () => {
         navigate("/equipment-list");
    };

    return (
        <div className="full-screen-center">
            <div className="login-panel">
                <h2 className="dashboard-heading">Student Dashboard</h2>
                <p className="dashboard-content">
                    Welcome, <strong>{username || "Student"}</strong> 🎓
                </p>
                <p className="dashboard-content">
                    You can view available equipment and make borrowing requests here.
                </p>
                <button onClick={viewEquipments} className="btn-primary logout-btn">
                    Go to equipment dashboard
                </button>
                <button onClick={handleLogout} className="btn-primary logout-btn">
                    Logout
                </button>
            </div>
        </div>
    );
};

export default StudentDashboard;
