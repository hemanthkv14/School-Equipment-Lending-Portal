import React from "react";
import { Navigate } from "react-router-dom";

export default function RoleRedirect() {
  const user = JSON.parse(localStorage.getItem("username"));

  if (!user) return <Navigate to="/" />;

  switch (user.role) {
    case "admin":
      return <Navigate to="/admin-equipment-list" />;
    case "student":
      return <Navigate to="/equipment-list" />;
    case "staff":
      return <Navigate to="/staff-dashboard" />;
    default:
      return <Navigate to="/" />;
  }
}
