import { NavLink, useNavigate } from "react-router-dom";
import { useState, useRef, useEffect } from "react";
import { FaUserCircle } from "react-icons/fa";

export default function Navbar() {
  const navigate = useNavigate();
  const role = localStorage.getItem("userRole");
  const username = localStorage.getItem("username");

  const [open, setOpen] = useState(false);
  const dropdownRef = useRef(null);

  const handleLogout = () => {
    localStorage.clear();
    navigate("/");
  };

  useEffect(() => {
    const handleClickOutside = (event) => {
      if (dropdownRef.current && !dropdownRef.current.contains(event.target)) {
        setOpen(false);
      }
    };
    document.addEventListener("mousedown", handleClickOutside);
    return () => document.removeEventListener("mousedown", handleClickOutside);
  }, []);

  return (
    <nav className="bg-blue-600 text-white shadow-md sticky top-0 z-50">
      <div className="max-w-7xl mx-auto px-4 py-3 flex justify-between items-center">
        <h1 className="text-2xl font-bold">Equipment Portal</h1>

        <div className="space-x-6 flex items-center">
          {role === "student" && (
            <>
              <NavLink
                to="/equipment-list"
                className={({ isActive }) =>
                  `text-lg ${isActive ? "font-semibold underline" : "hover:underline"}`
                }
              >
                All Equipment
              </NavLink>
              <NavLink
                to="/my-requests"
                className={({ isActive }) =>
                  `text-lg ${isActive ? "font-semibold underline" : "hover:underline"}`
                }
              >
                My Requests
              </NavLink>
            </>
          )}

          {role === "admin" && (
            <>
              <NavLink
                to="/admin-equipment-list"
                className={({ isActive }) =>
                  `text-lg ${isActive ? "font-semibold underline" : "hover:underline"}`
                }
              >
                Manage Equipment
              </NavLink>
              <NavLink
                to="/admin-dashboard"
                className={({ isActive }) =>
                  `text-lg ${isActive ? "font-semibold underline" : "hover:underline"}`
                }
              >
                Dashboard
              </NavLink>
            </>
          )}

          {role === "staff" && (
            <NavLink
              to="/staff-dashboard"
              className={({ isActive }) =>
                `text-lg ${isActive ? "font-semibold underline" : "hover:underline"}`
              }
            >
              Review Requests
            </NavLink>
          )}

          {username && (
            <div className="relative" ref={dropdownRef}>
              <button
                onClick={() => setOpen(!open)}
                className="flex items-center gap-2 focus:outline-none"
              >
                <FaUserCircle size={28} className="text-white hover:opacity-90" />
              </button>

              {open && (
                <div className="absolute right-0 mt-2 w-48 bg-white text-gray-800 rounded-lg shadow-lg border">
                  <div className="px-4 py-3 border-b">
                    <p className="font-semibold text-gray-900">Hi, {username}</p>
                    <p className="text-xs text-gray-500 capitalize">{role}</p>
                  </div>
                  <button
                    onClick={handleLogout}
                    className="w-full text-left px-4 py-2 text-red-600 hover:bg-gray-100 rounded-b-lg"
                  >
                    Logout
                  </button>
                </div>
              )}
            </div>
          )}
        </div>
      </div>
    </nav>
  );
}
