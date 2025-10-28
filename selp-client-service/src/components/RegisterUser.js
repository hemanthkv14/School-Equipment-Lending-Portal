import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import apiClient from "../api/apiClient";
import "../css/Login.css";

const RegisterUser = ({ roleRestriction = null }) => {
    const [username, setUsername] = useState("");
    const [email, setEmail] = useState("");
    const [password, setPassword] = useState("");
    const [role, setRole] = useState(roleRestriction || "student");
    const [message, setMessage] = useState("");
    const navigate = useNavigate();

    const handleRegister = async (e) => {
        e.preventDefault();
        try {
            const response = await apiClient.post("/api/auth/register", {
                username,
                email,
                password,
                role,
            });
            if (response.status === 201 || response.status === 200) {
                setMessage("User registered successfully!");
                setTimeout(() => navigate("/"), 1500);
            }
        } catch (error) {
            setMessage("Failed to register user. Please try again.");
        }
    };

    return (
        <div className="full-screen-center">
            <div className="login-panel">
                <h2 className="panel-heading">Register User</h2>
                <form onSubmit={handleRegister}>
                    <input
                        type="text"
                        placeholder="Username"
                        value={username}
                        onChange={(e) => setUsername(e.target.value)}
                        className="input-field"
                        required
                    />
                    <input
                        type="email"
                        placeholder="Email"
                        value={email}
                        onChange={(e) => setEmail(e.target.value)}
                        className="input-field"
                        required
                    />
                    <input
                        type="password"
                        placeholder="Password"
                        value={password}
                        onChange={(e) => setPassword(e.target.value)}
                        className="input-field"
                        required
                    />

                    {/* Role Dropdown */}
                    {!roleRestriction && (
                        <select
                            value={role}
                            onChange={(e) => setRole(e.target.value)}
                            className="input-field"
                        >
                            <option value="admin">Admin</option>
                            <option value="staff">Staff</option>
                            <option value="student">Student</option>
                        </select>
                    )}

                    {/* For login page restricted registration */}
                    {roleRestriction && (
                        <input
                            type="hidden"
                            value={roleRestriction}
                            onChange={() => {}}
                        />
                    )}

                    <button type="submit" className="btn-primary">
                        Register
                    </button>
                </form>

                {message && (
                    <p
                        className="dashboard-content"
                        style={{
                            color: message.includes("success") ? "green" : "red",
                            marginTop: "10px",
                        }}
                    >
                        {message}
                    </p>
                )}
            </div>
        </div>
    );
};

export default RegisterUser;
