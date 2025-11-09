import React, { useEffect, useState } from "react";
import {
  getAllLendings,
  approveLoan,
  acceptReturnItem,
  rejectItem,
} from "../api/equipmentApi";
import { motion } from "framer-motion";
import {
  FaCheckCircle,
  FaTimesCircle,
  FaClock,
  FaUndo,
  FaExclamationTriangle,
  FaBoxOpen,
} from "react-icons/fa";

function DatePicker({ value, onChange }) {
  return (
    <input
      type="datetime-local"
      value={value}
      onChange={(e) => onChange(e.target.value)}
      className="border border-gray-300 px-3 py-2 rounded-lg w-full focus:ring-2 focus:ring-blue-400 focus:outline-none bg-white/70 backdrop-blur-sm"
    />
  );
}

const ITEM_CONDITIONS = ["NEW", "GOOD", "FAIR", "POOR", "BROKEN"];

export default function AdminRequests() {
  const [requests, setRequests] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const [actionMessage, setActionMessage] = useState({ type: "", text: "" });
  const [selectedDueDate, setSelectedDueDate] = useState({});
  const [returnModal, setReturnModal] = useState({ open: false, lendingId: null });
  const [returnCondition, setReturnCondition] = useState("GOOD");
  const userId = localStorage.getItem("userId");

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
    setActionMessage({ type: "", text: "" });
    try {
      if (!selectedDueDate[lendingId]) {
        setActionMessage({ type: "error", text: "Please select a due date first." });
        return;
      }
      await approveLoan(userId, lendingId, selectedDueDate[lendingId]);
      setActionMessage({ type: "success", text: `Loan ID ${lendingId} approved.` });
      fetchRequests();
    } catch (err) {
      setActionMessage({
        type: "error",
        text: err.response?.data || err.message || "Error approving loan.",
      });
    }
  };

  const handleReject = async (lendingId) => {
    setActionMessage({ type: "", text: "" });
    try {
      await rejectItem(userId,lendingId);
      setActionMessage({ type: "success", text: `Loan ID ${lendingId} rejected.` });
      fetchRequests();
    } catch (err) {
      setActionMessage({
        type: "error",
        text: err.response?.data || err.message || "Error rejecting loan.",
      });
    }
  };

  const openReturnModal = (lendingId) => {
    setReturnModal({ open: true, lendingId });
    setReturnCondition("GOOD");
    setActionMessage({ type: "", text: "" });
  };

  const closeReturnModal = () => setReturnModal({ open: false, lendingId: null });

  const handleReturnConfirm = async () => {
    setActionMessage({ type: "", text: "" });
    try {
      await acceptReturnItem(returnModal.lendingId, returnCondition);
      setActionMessage({
        type: "success",
        text: `Return accepted for Loan ID ${returnModal.lendingId}`,
      });
      closeReturnModal();
      fetchRequests();
    } catch (err) {
      setActionMessage({
        type: "error",
        text: err.response?.data || err.message || "Error accepting return.",
      });
    }
  };

  if (loading)
    return <p className="text-center mt-10 text-gray-600 text-lg animate-pulse">Loading requests...</p>;
  if (error)
    return <p className="text-center mt-10 text-red-600 font-semibold">{error}</p>;

  const getStatusBadge = (status) => {
    const colors = {
      BORROW_PENDING: "bg-yellow-100 text-yellow-700 border-yellow-300",
      APPROVED: "bg-green-100 text-green-700 border-green-300",
      REJECTED: "bg-red-100 text-red-700 border-red-300",
      OVERDUE: "bg-orange-100 text-orange-700 border-orange-300",
      RETURN_PENDING: "bg-blue-100 text-blue-700 border-blue-300",
      RETURNED: "bg-gray-100 text-gray-700 border-gray-300",
    };
    return (
      <span
        className={`inline-flex items-center gap-2 px-3 py-1 rounded-full border text-sm font-medium ${colors[status]}`}
      >
        {status === "APPROVED" && <FaCheckCircle />}
        {status === "BORROW_PENDING" && <FaClock />}
        {status === "REJECTED" && <FaTimesCircle />}
        {status === "RETURN_PENDING" && <FaUndo />}
        {status === "OVERDUE" && <FaExclamationTriangle />}
        {status === "RETURNED" && <FaBoxOpen />}
        {status.replace("_", " ")}
      </span>
    );
  };

  return (
    <div className="max-w-6xl mx-auto py-10 px-4">
      <h1 className="text-3xl font-bold mb-6 text-center text-blue-700">All Borrow Requests</h1>

      {actionMessage.text && (
        <div
          className={`p-3 mb-4 rounded-lg text-center ${
            actionMessage.type === "error"
              ? "bg-red-100 text-red-700"
              : "bg-green-100 text-green-700"
          }`}
        >
          {actionMessage.text}
        </div>
      )}

      <div className="grid gap-6">
        {requests.length === 0 && <p>No requests found.</p>}

        {requests.map((req) => (
          <motion.div
            key={req.lendingId}
            className="bg-white/80 backdrop-blur-md p-6 rounded-2xl shadow-lg hover:shadow-2xl transition-all duration-300 border border-gray-200"
            initial={{ opacity: 0, y: 15 }}
            animate={{ opacity: 1, y: 0 }}
          >
            <div className="flex flex-col md:flex-row md:items-center md:justify-between gap-4">
              <div>
                <h2 className="text-xl font-semibold text-gray-900">
                  {req.equipmentName}
                </h2>
                <p className="text-gray-700 mt-1">
                  Status: {getStatusBadge(req.approvalStatus)}
                </p>
              </div>

              {/* Actions */}
              <div className="flex flex-col gap-3 min-w-[300px]">
                {req.approvalStatus === "BORROW_PENDING" && (
                  <>
                    <div>
                      <label className="block font-semibold mb-1 text-gray-700">
                        Select Due Date
                      </label>
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
                        className="flex items-center gap-2 px-4 py-2 bg-green-600 text-white rounded-lg hover:bg-green-700 transition"
                      >
                        <FaCheckCircle /> Approve
                      </button>
                      <button
                        onClick={() => handleReject(req.lendingId)}
                        className="flex items-center gap-2 px-4 py-2 bg-red-600 text-white rounded-lg hover:bg-red-700 transition"
                      >
                        <FaTimesCircle /> Reject
                      </button>
                    </div>
                  </>
                )}

                {req.approvalStatus === "RETURN_PENDING" && (
                  <button
                    onClick={() => openReturnModal(req.lendingId)}
                    className="flex items-center justify-center gap-2 px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition"
                  >
                    <FaUndo /> Mark as Returned
                  </button>
                )}
              </div>
            </div>
          </motion.div>
        ))}
      </div>

      {returnModal.open && (
        <div className="fixed inset-0 bg-black bg-opacity-40 flex items-center justify-center p-4 z-50">
          <div className="bg-white rounded-xl p-6 max-w-md w-full shadow-xl">
            <h2 className="text-xl font-bold mb-4 text-gray-800">Confirm Return</h2>

            <label className="block mb-2 font-semibold text-gray-700">
              Item Condition
            </label>
            <select
              value={returnCondition}
              onChange={(e) => setReturnCondition(e.target.value)}
              className="w-full border rounded-lg px-3 py-2 mb-4 focus:ring-2 focus:ring-blue-400"
            >
              {ITEM_CONDITIONS.map((cond) => (
                <option key={cond} value={cond}>
                  {cond}
                </option>
              ))}
            </select>

            <div className="flex justify-end gap-3">
              <button
                onClick={closeReturnModal}
                className="px-4 py-2 bg-gray-300 text-gray-700 rounded-lg hover:bg-gray-400"
              >
                Cancel
              </button>
              <button
                onClick={handleReturnConfirm}
                className="flex items-center gap-2 px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700"
              >
                <FaCheckCircle /> Confirm
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}