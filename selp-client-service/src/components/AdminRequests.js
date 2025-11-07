import React, { useEffect, useState } from "react";
import { getAllLendings, approveLoan, acceptReturnItem } from "../api/equipmentApi";
import { motion } from "framer-motion";

function DatePicker({ value, onChange }) {
  return (
    <input
      type="datetime-local"
      value={value}
      onChange={(e) => onChange(e.target.value)}
      className="border px-2 py-1 rounded"
    />
  );
}

const ITEM_CONDITIONS = ["EXCELLENT", "GOOD", "DAMAGED", "LOST"];

export default function AdminRequests() {
  const [requests, setRequests] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const [actionError, setActionError] = useState("");
  const [actionSuccess, setActionSuccess] = useState("");
  const [selectedDueDate, setSelectedDueDate] = useState({});
  const [returnModal, setReturnModal] = useState({ open: false, lendingId: null });
  const [returnCondition, setReturnCondition] = useState("GOOD");

  const fetchRequests = async () => {
    setLoading(true);
    setError("");
    try {
      const data = await getAllLendings();
      setRequests(data || []);
    } catch (err) {
      setError("Failed to load requests.");
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchRequests();
  }, []);

  const handleApprove = async (lendingId) => {
    setActionError("");
    setActionSuccess("");
    try {
      if (!selectedDueDate[lendingId]) {
        setActionError("Please select a due date first.");
        return;
      }
      await approveLoan(lendingId, selectedDueDate[lendingId]);
      setActionSuccess(`Loan ID ${lendingId} approved.`);
      fetchRequests();
    } catch (err) {
      setActionError(err.response?.data || err.message || "Error approving loan.");
    }
  };

  const handleReject = async (lendingId) => {
    alert("Reject API not implemented yet.");
  };

  const openReturnModal = (lendingId) => {
    setReturnModal({ open: true, lendingId });
    setReturnCondition("GOOD");
    setActionError("");
    setActionSuccess("");
  };

  const closeReturnModal = () => setReturnModal({ open: false, lendingId: null });

  const handleReturnConfirm = async () => {
    setActionError("");
    setActionSuccess("");
    try {
      await acceptReturnItem(returnModal.lendingId, returnCondition);
      setActionSuccess(`Return accepted for Loan ID ${returnModal.lendingId}`);
      closeReturnModal();
      fetchRequests();
    } catch (err) {
      setActionError(err.response?.data || err.message || "Error accepting return.");
    }
  };

  if (loading) return <p className="text-center mt-10">Loading requests...</p>;
  if (error) return <p className="text-center mt-10 text-red-600">{error}</p>;

  return (
    <div className="max-w-6xl mx-auto py-10 px-4">
      <h1 className="text-3xl font-bold mb-6 text-center">All Borrow Requests</h1>

      {actionError && (
        <div className="bg-red-100 text-red-700 p-3 rounded mb-4">{actionError}</div>
      )}
      {actionSuccess && (
        <div className="bg-green-100 text-green-700 p-3 rounded mb-4">{actionSuccess}</div>
      )}

      <div className="grid gap-6">
        {requests.length === 0 && <p>No requests found.</p>}

        {requests.map((req) => (
          <motion.div
            key={req.lendingId}
            className="bg-white p-6 rounded-xl shadow hover:shadow-lg transition flex flex-col md:flex-row md:items-center md:justify-between gap-4"
            initial={{ opacity: 0, y: 10 }}
            animate={{ opacity: 1, y: 0 }}
          >
            <div className="flex-1">
              <h2 className="text-xl font-semibold">{req.equipmentName}</h2>
              <p>
                Requested by: {req.borrowerName || "N/A"} <br />
                Status: <strong>{req.approvalStatus}</strong>
              </p>
            </div>

            <div className="flex flex-col gap-3 min-w-[300px]">
              <div>
                <label className="block font-semibold mb-1">Due Date (for Approve/Reject)</label>
                <DatePicker
                  value={selectedDueDate[req.lendingId] || ""}
                  onChange={(val) =>
                    setSelectedDueDate((prev) => ({ ...prev, [req.lendingId]: val }))
                  }
                />
              </div>

              <div className="flex gap-3 flex-wrap">
                <button
                  onClick={() => handleApprove(req.lendingId)}
                  className="px-4 py-2 bg-green-600 text-white rounded hover:bg-green-700 transition"
                >
                  Approve
                </button>

                <button
                  onClick={() => handleReject(req.lendingId)}
                  className="px-4 py-2 bg-red-600 text-white rounded hover:bg-red-700 transition"
                >
                  Reject
                </button>

                <button
                  onClick={() => openReturnModal(req.lendingId)}
                  className="px-4 py-2 bg-blue-600 text-white rounded hover:bg-blue-700 transition"
                >
                  Mark as Returned
                </button>
              </div>
            </div>
          </motion.div>
        ))}
      </div>

      {/* Return Confirmation Modal */}
      {returnModal.open && (
        <div className="fixed inset-0 bg-black bg-opacity-40 flex items-center justify-center p-4 z-50">
          <div className="bg-white rounded-lg p-6 max-w-md w-full">
            <h2 className="text-xl font-bold mb-4">Confirm Return</h2>

            <label className="block mb-2 font-semibold">Item Condition</label>
            <select
              value={returnCondition}
              onChange={(e) => setReturnCondition(e.target.value)}
              className="w-full border rounded px-3 py-2 mb-4"
            >
              {ITEM_CONDITIONS.map((cond) => (
                <option key={cond} value={cond}>
                  {cond}
                </option>
              ))}
            </select>

            <div className="flex justify-end gap-4">
              <button
                onClick={closeReturnModal}
                className="px-4 py-2 bg-gray-300 rounded hover:bg-gray-400"
              >
                Cancel
              </button>
              <button
                onClick={handleReturnConfirm}
                className="px-4 py-2 bg-blue-600 text-white rounded hover:bg-blue-700"
              >
                Confirm
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}