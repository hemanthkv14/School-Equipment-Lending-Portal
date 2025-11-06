import React, { useEffect, useState } from "react";
import { motion } from "framer-motion";
import { getAllBorrowRequests } from "../api/equipmentApi";

export default function MyRequests() {
  const [requests, setRequests] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  useEffect(() => {
    const fetchRequests = async () => {
      try {
        const data = await getAllBorrowRequests();
        setRequests(data || []);
      } catch (err) {
        console.error("Error loading requests:", err);
        setError("Failed to load your borrow requests.");
      } finally {
        setLoading(false);
      }
    };
    fetchRequests();
  }, []);

  const normalizeStatus = (s) => (s ? String(s).toUpperCase() : "");

  const statusStyles = {
    BORROW_PENDING: "bg-yellow-100 text-yellow-800 border-yellow-300",
    APPROVED: "bg-green-100 text-green-800 border-green-300",
    REJECTED: "bg-red-100 text-red-800 border-red-300",
    OVERDUE: "bg-orange-100 text-orange-800 border-orange-300",
    RETURN_PENDING: "bg-blue-100 text-blue-800 border-blue-300",
    RETURNED: "bg-gray-100 text-gray-600 border-gray-300",
  };

  if (loading) return <p className="text-center mt-10">Loading your requests...</p>;
  if (error) return <p className="text-center mt-10 text-red-600">{error}</p>;

  return (
    <div className="w-full min-h-screen py-10 px-4 text-gray-800 bg-gray-50">
      <header className="text-center mb-10">
        <h1 className="text-4xl font-bold text-slate-800">My Requests</h1>
        <p className="text-gray-600 mt-2">
          View and manage your borrow requests
        </p>
      </header>

      <div className="max-w-5xl mx-auto grid gap-8 grid-cols-1">
        {requests.length === 0 ? (
          <p className="text-center text-gray-600 py-10">No requests found.</p>
        ) : (
          requests.map((req, i) => {
            const status = normalizeStatus(req.approvalStatus);

            return (
              <motion.div
                key={req.lendingId}
                className="bg-white rounded-2xl shadow-md hover:shadow-xl transition-all duration-300 p-8 flex flex-col justify-between border border-gray-100"
                initial={{ opacity: 0, y: 10 }}
                animate={{ opacity: 1, y: 0 }}
                transition={{ duration: 0.3, delay: i * 0.06 }}
              >
                <div className="flex items-center justify-between flex-wrap gap-4">
                  <h2 className="text-2xl font-bold text-gray-800 text-left flex-1 min-w-[30%]">
                    {req.equipmentName}
                  </h2>
                  <span
                    className={`inline-block px-4 py-1 text-sm font-medium rounded-full border text-center ${statusStyles[status] || "bg-gray-100 text-gray-600 border-gray-300"} min-w-[150px]`}
                  >
                    {(req.approvalStatus || "").replace("_", " ")}
                  </span>

                  <div className="flex justify-end min-w-[150px]">
                    {status === "BORROW_PENDING" && (
                      <button className="px-4 py-2 bg-yellow-500 text-white rounded-lg hover:bg-yellow-600 transition">
                        Cancel Request
                      </button>
                    )}
                    {status === "APPROVED" && (
                      <button className="px-4 py-2 bg-green-600 text-white rounded-lg hover:bg-green-700 transition">
                        Return
                      </button>
                    )}
                    {status === "REJECTED" && (
                      <button className="px-4 py-2 bg-indigo-600 text-white rounded-lg hover:bg-indigo-700 transition">
                        Retry
                      </button>
                    )}
                    {status === "OVERDUE" && (
                      <button className="px-4 py-2 bg-red-600 text-white rounded-lg hover:bg-red-700 transition">
                        Return Immediately
                      </button>
                    )}
                    {status === "RETURN_PENDING" && (
                      <div className="px-4 py-2 bg-gray-200 text-gray-700 rounded-lg">
                        Please wait...
                      </div>
                    )}
                    {status === "RETURNED" && (
                        <div className="px-4 py-2 text-gray-400 rounded-lg">
                            Completed
                        </div>
                    )}
                  </div>
                </div>
              </motion.div>
            );
          })
        )}
      </div>
    </div>
  );
}