import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import apiClient from "../api/apiClient";
import "../css/Login.css";

const Login = () => {
    const [username, setUsername] = useState("");
    const [password, setPassword] = useState("");
    const [error, setError] = useState(null);
    const navigate = useNavigate();

    const handleSubmit = async (e) => {
        e.preventDefault();
        try {
            const response = await apiClient.post("/api/auth/login", {
                username,
                password,
            });
            const { token, user } = response.data;
            localStorage.setItem("token", token);
            localStorage.setItem("userRole", user.role);
            localStorage.setItem("username", user.username);

            if (user.role === "student") navigate("/student-dashboard");
            else if (user.role === "admin") navigate("/admin-dashboard");
            else if (user.role === "staff") navigate("/staff-dashboard");
        } catch (error) {
            setError("Invalid username or password");
        }
    };

    return (
        <div className="full-screen-center">
            <h1 className="page-heading">School Equipment Management System</h1>
            <div className="login-panel">
                <h2 className="panel-heading">Login</h2>
                <form onSubmit={handleSubmit}>
                    <input
                        type="text"
                        value={username}
                        onChange={(e) => setUsername(e.target.value)}
                        placeholder="Username"
                        className="input-field"
                    />
                    <input
                        type="password"
                        value={password}
                        onChange={(e) => setPassword(e.target.value)}
                        placeholder="Password"
                        className="input-field"
                    />
                    <button type="submit" className="btn-primary">
                        Login
                    </button>
                    {error && <p className="error-msg">{error}</p>}
                </form>

                {/* Student Registration Link */}
                <p style={{ textAlign: "center", marginTop: "10px" }}>
                    Don’t have an account?{" "}
                    <span
                        style={{
                            color: "#4682B4",
                            cursor: "pointer",
                            fontWeight: "bold",
                        }}
                        onClick={() => navigate("/register-student")}
                    >
                        Register as Student
                    </span>
                </p>
            </div>
        </div>
    );
};

export default Login;
