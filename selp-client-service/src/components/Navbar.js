import { NavLink, useNavigate } from "react-router-dom";
import { useState, useEffect } from "react";
import { FaUserCircle, FaBell } from "react-icons/fa";
import { notificationApi } from "../api/equipmentApi";

export default function Navbar() {
  const navigate = useNavigate();
  const role = localStorage.getItem("userRole");
  const username = localStorage.getItem("username");
  const userId = localStorage.getItem("userId");

  const [profileOpen, setProfileOpen] = useState(false);
  const [notifOpen, setNotifOpen] = useState(false);
  const [notifications, setNotifications] = useState([]);

  useEffect(() => {
    if (role === "student" && userId) {
      loadNotifications();
    }
  }, [role, userId]);

  const loadNotifications = async () => {
    try {
      const data = await notificationApi(userId);
      if (data?.dueSoonLendings) {
        const today = new Date();
        const list = data.dueSoonLendings.map((item) => {
          const due = new Date(item.due_date);
          const diffDays = Math.ceil((due - today) / (1000 * 60 * 60 * 24));
          let message = "";
          if (diffDays < 0) {
            message = `🔴 Overdue by ${Math.abs(diffDays)} day${Math.abs(diffDays) > 1 ? "s" : ""}`;
          } else if (diffDays <= 2) {
            message = `⏰ Due in ${diffDays} day${diffDays > 1 ? "s" : ""}`;
          } else {
            message = `Due in ${diffDays} days`;
          }

          return {
            id: item.lending_id,
            dueDate: due.toLocaleDateString(),
            message,
            isOverdue: diffDays < 0,
          };
        });
        setNotifications(list);
      }
    } catch (err) {
      console.error("Error fetching notifications:", err);
    }
  };

  const handleLogout = () => {
    localStorage.clear();
    navigate("/");
  };

  const clearNotifications = () => setNotifications([]);

  return (
    <nav className="bg-blue-600 text-white shadow-md sticky top-0 z-50">
      <div className="max-w-7xl mx-auto px-4 py-3 flex justify-between items-center">
        <h1 className="text-2xl font-bold">Equipment Portal</h1>

        <div className="flex items-center gap-6">
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

              <div className="relative">
                <button
                  onClick={() => {
                    setNotifOpen(!notifOpen);
                    setProfileOpen(false);
                  }}
                  className="relative"
                >
                  <FaBell size={22} className="text-white hover:opacity-90" />
                  {notifications.length > 0 && (
                    <span className="absolute -top-2 -right-2 bg-red-600 text-white text-xs font-bold px-1.5 py-0.5 rounded-full">
                      {notifications.length}
                    </span>
                  )}
                </button>

                {notifOpen && (
                  <div className="absolute right-0 mt-2 w-72 bg-white text-gray-800 rounded-lg shadow-lg border z-50">
                    <div className="px-4 py-2 border-b flex justify-between items-center">
                      <p className="font-semibold">Notifications</p>
                      <button
                        onClick={clearNotifications}
                        className="text-sm text-blue-600 hover:underline"
                      >
                        Clear
                      </button>
                    </div>

                    {notifications.length > 0 ? (
                      notifications.map((n) => (
                        <div
                          key={n.id}
                          className={`px-4 py-2 border-b text-sm ${
                            n.isOverdue ? "text-red-600 font-medium" : "text-gray-700"
                          }`}
                        >
                          {n.message} (Due: {n.dueDate})
                        </div>
                      ))
                    ) : (
                      <p className="px-4 py-3 text-sm text-gray-500">No notifications</p>
                    )}
                  </div>
                )}
              </div>
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
                to="/admin-requests"
                className={({ isActive }) =>
                  `text-lg ${isActive ? "font-semibold underline" : "hover:underline"}`
                }
              >
                All Requests
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
            <>
              <NavLink
                to="/admin-requests"
                className={({ isActive }) =>
                  `text-lg ${isActive ? "font-semibold underline" : "hover:underline"}`
                }
              >
                All Requests
              </NavLink>
              <NavLink
                to="/staff-dashboard"
                className={({ isActive }) =>
                  `text-lg ${isActive ? "font-semibold underline" : "hover:underline"}`
                }
              >
                Dashboard
              </NavLink>
            </>
          )}

          {username && (
            <div className="relative">
              <button
                onClick={() => {
                  setProfileOpen(!profileOpen);
                  setNotifOpen(false);
                }}
                className="flex items-center gap-2 focus:outline-none"
              >
                <FaUserCircle size={28} className="text-white hover:opacity-90" />
              </button>

              {profileOpen && (
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
