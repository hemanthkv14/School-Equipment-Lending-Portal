import React from "react";
import { Navigate } from "react-router-dom";

const ProtectedRoute = ({ element: Component, allowedRoles }) => {
    const token = localStorage.getItem("token");
    const userRole = localStorage.getItem("userRole");

    // Not logged in → redirect to login
    if (!token || !userRole) {
        return <Navigate to="/" replace />;
    }

    // Logged in but not authorized → redirect to login
    if (!allowedRoles.includes(userRole.toLowerCase())) {
        return <Navigate to="/" replace />;
    }

    // Authorized → render component
    return <Component />;
};

export default ProtectedRoute;
