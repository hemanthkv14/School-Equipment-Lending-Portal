import { NavLink, useNavigate } from "react-router-dom";
import { useState, useEffect, useRef } from "react";
import { FaUserCircle, FaBell } from "react-icons/fa";
import { notificationApi } from "../api/equipmentApi";

export default function Navbar() {
  const navigate = useNavigate();
  const role = localStorage.getItem("userRole");
  const username = localStorage.getItem("username");
  const userId = localStorage.getItem("userId");

  const [openProfile, setOpenProfile] = useState(false);
  const [openNotif, setOpenNotif] = useState(false);
  const [notifications, setNotifications] = useState([]);

  const profileRef = useRef(null);
  const notifRef = useRef(null);

  const handleLogout = () => {
    localStorage.clear();
    navigate("/");
  };

  useEffect(() => {
    if (role === "student" && userId) {
      loadNotifications();
    }
  }, [role, userId]);

  const loadNotifications = async () => {
    try {
      console.log("userID",userId);
      const res = await notificationApi(userId);
      console.log("res",res);
      if (res?.dueSoonLendings) {
        const today = new Date();
        const formatted = res.dueSoonLendings.map((lend) => {
          const due = new Date(lend.due_date);
          const diffDays = Math.ceil((due - today) / (1000 * 60 * 60 * 24));

          let message = "";
          if (diffDays <= 2) message = `⚠️ Due within ${diffDays} day(s)!`;
          else if (diffDays <= 7) message = `📅 Due within a week`;
          else message = `Due on ${due.toLocaleDateString()}`;

          return { ...lend, message, daysLeft: diffDays };
        });
        setNotifications(formatted);
      }
    } catch (err) {
      console.error("Failed to fetch notifications:", err);
    }
  };

  useEffect(() => {
    const handleClickOutside = (event) => {
      if (
        profileRef.current &&
        !profileRef.current.contains(event.target) &&
        notifRef.current &&
        !notifRef.current.contains(event.target)
      ) {
        setOpenProfile(false);
        setOpenNotif(false);
      }
    };
    document.addEventListener("mousedown", handleClickOutside);
    return () => document.removeEventListener("mousedown", handleClickOutside);
  }, []);

  return (
    <nav className="bg-blue-600 text-white shadow-md sticky top-0 z-50">
      <div className="max-w-7xl mx-auto px-4 py-3 flex justify-between items-center">
        <h1 className="text-2xl font-bold">Equipment Portal</h1>

        <div className="flex items-center space-x-6">
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

              <div className="relative" ref={notifRef}>
                <button
                  onClick={() => setOpenNotif(!openNotif)}
                  className="relative focus:outline-none"
                >
                  <FaBell size={22} />
                  {notifications.length > 0 && (
                    <span className="absolute -top-1 -right-1 bg-red-500 text-white text-xs px-1.5 rounded-full">
                      {notifications.length}
                    </span>
                  )}
                </button>

                {openNotif && (
                  <div className="absolute right-0 mt-2 w-72 bg-white text-gray-800 rounded-lg shadow-lg border z-50">
                    <div className="flex justify-between items-center px-4 py-2 border-b">
                      <p className="font-semibold text-gray-900">Notifications</p>
                      <button
                        onClick={() => setNotifications([])}
                        className="text-sm text-red-500 hover:text-red-600"
                      >
                        Clear
                      </button>
                    </div>

                    <div className="max-h-60 overflow-y-auto">
                      {notifications.length > 0 ? (
                        notifications.map((n) => (
                          <div
                            key={n.lending_id}
                            className="px-4 py-3 border-b last:border-none hover:bg-gray-100"
                          >
                            <p className="text-sm">{n.message}</p>
                            <p className="text-xs text-gray-500">
                              Due Date: {new Date(n.due_date).toLocaleDateString()}
                            </p>
                          </div>
                        ))
                      ) : (
                        <p className="text-sm text-gray-500 px-4 py-3">No notifications</p>
                      )}
                    </div>
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

          {/* 👤 Profile Menu */}
          {username && (
            <div className="relative" ref={profileRef}>
              <button
                onClick={() => setOpenProfile(!openProfile)}
                className="flex items-center gap-2 focus:outline-none"
              >
                <FaUserCircle size={28} className="text-white hover:opacity-90" />
              </button>

              {openProfile && (
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
