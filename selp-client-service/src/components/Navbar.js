import { NavLink } from "react-router-dom";

export default function Navbar() {
  return (
    <nav className="bg-blue-600 text-white shadow-md">
      <div className="max-w-7xl mx-auto px-4 py-3 flex justify-between items-center">
        <h1 className="text-2xl font-bold">Equipment Portal</h1>
        <div className="space-x-6">
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
        </div>
      </div>
    </nav>
  );
}
