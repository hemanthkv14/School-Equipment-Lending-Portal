import React, { useEffect, useState } from "react";
import { getAllBorrowRequests } from "../api/equipmentApi";

export default function MyRequests() {
  const [requests, setRequests] = useState([]);

  useEffect(() => {
    const loadRequests = async () => {
      try {
        const data = await getAllBorrowRequests();
        setRequests(data);
      } catch (err) {
        console.error("Error fetching requests:", err);
      }
    };
    loadRequests();
  }, []);

  return (
    <div className="max-w-5xl mx-auto mt-10 p-6 bg-white rounded-xl shadow-md">
      <h1 className="text-2xl font-bold mb-4">My Requests</h1>
      {requests.length === 0 ? (
        <p>No requests yet.</p>
      ) : (
        <table className="min-w-full border border-gray-300 text-left">
          <thead className="bg-gray-100">
            <tr>
              <th className="py-2 px-4 border-b">Item</th>
              <th className="py-2 px-4 border-b">Borrower</th>
              <th className="py-2 px-4 border-b">Status</th>
              <th className="py-2 px-4 border-b">Action</th>
            </tr>
          </thead>
          <tbody>
            {requests.map((req) => (
              <tr key={req.lendingId}>
                <td className="py-2 px-4 border-b">{req.equipmentName}</td>
                <td className="py-2 px-4 border-b">{req.borrowerUsername}</td>
                <td className="py-2 px-4 border-b">{req.approvalStatus}</td>
                <td className="py-2 px-4 border-b">
                  {req.approvalStatus === "APPROVED" ? (
                    <button
                      onClick={() => alert("Returning item " + req.equipmentName)}
                      className="bg-green-600 text-white px-3 py-1 rounded-md hover:bg-green-700"
                    >
                      Return
                    </button>
                  ) : (
                    <span className="text-gray-500">—</span>
                  )}
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      )}
    </div>
  );
}
