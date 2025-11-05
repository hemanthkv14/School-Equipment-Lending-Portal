import React, { useEffect, useState } from "react";
import { getAllBorrowRequests } from "../api/equipmentApi";

export default function MyRequests() {
  const [requests, setRequests] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const loadRequests = async () => {
      try {
        const data = await getAllBorrowRequests();
        setRequests(data);
      } catch (err) {
        console.error("Error fetching requests:", err);
      } finally {
        setLoading(false);
      }
    };
    loadRequests();
  }, []);

  if (loading) return <p className="text-center mt-10">Loading...</p>;

  if (requests.length === 0)
    return <p className="text-center mt-10 text-gray-600">No requests found.</p>;

  return (
    <div className="w-full min-h-screen py-10 px-4 text-gray-800">
      <header className="text-center mb-10">
        <h1 className="text-4xl font-bold text-slate-800">My Requests</h1>
        <p className="text-gray-600 mt-2">View your borrow requests and their status</p>
      </header>

      <div className="max-w-5xl mx-auto grid gap-8 grid-cols-1">
        {requests.map((req) => (
          <div
            key={req.lendingId}
            className="bg-white rounded-2xl shadow-md hover:shadow-xl transition-all duration-300 p-8 flex flex-col justify-between border border-gray-100"
          >
            <div className="md:flex md:justify-between md:items-center">
              <div className="mb-4 md:mb-0">
                <h2 className="text-2xl font-bold text-gray-800 mb-1">
                  {req.equipmentName}
                </h2>
              </div>

              <div className="flex flex-wrap gap-x-6 gap-y-3 items-center">
                <span
                  className={`px-3 py-1 rounded-full text-sm font-semibold ${
                    req.approvalStatus === "APPROVED"
                      ? "bg-green-100 text-green-700"
                      : req.approvalStatus === "BORROW_PENDING" ||
                        req.approvalStatus === "PENDING"
                      ? "bg-yellow-100 text-yellow-700"
                      : req.approvalStatus === "REJECTED"
                      ? "bg-red-100 text-red-700"
                      : "bg-gray-100 text-gray-600"
                  }`}
                >
                  {req.approvalStatus}
                </span>

                {req.approvalStatus === "BORROW_PENDING" ||
                req.approvalStatus === "PENDING" ? (
                  <button className="bg-red-500 text-white px-4 py-2 rounded-lg hover:bg-red-600">
                    Cancel Request
                  </button>
                ) : req.approvalStatus === "APPROVED" ? (
                  <button className="bg-green-500 text-white px-4 py-2 rounded-lg hover:bg-green-600">
                    Return
                  </button>
                ) : req.approvalStatus === "REJECTED" ? (
                  <button className="bg-indigo-500 text-white px-4 py-2 rounded-lg hover:bg-indigo-600">
                    Retry
                  </button>
                ) : null}
              </div>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
}
