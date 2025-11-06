import React, { useState, useEffect } from "react";
import { motion } from "framer-motion";
import { getAllEquipment, getAllItemDetails, updateEquipmentItem } from "../api/equipmentApi";

export default function AdminEquipmentList() {
  const [equipment, setEquipment] = useState([]);
  const [selectedEquipment, setSelectedEquipment] = useState(null);
  const [items, setItems] = useState([]);
  const [selectedItem, setSelectedItem] = useState(null);
  const [showItemModal, setShowItemModal] = useState(false);
  const [showEditModal, setShowEditModal] = useState(false);
  const [successMessage, setSuccessMessage] = useState("");

  useEffect(() => {
    loadEquipment();
  }, []);

  const loadEquipment = async () => {
    try {
      const data = await getAllEquipment();
      setEquipment(Array.isArray(data) ? data : []);
    } catch {
      console.log("Error loading equipment");
    }
  };

  const openItems = async (equipmentId) => {
    try {
      setSelectedEquipment(equipmentId);
      const data = await getAllItemDetails(equipmentId);
      setItems(Array.isArray(data) ? data : []);
      setShowItemModal(true);
    } catch {
      console.log("Error loading items");
    }
  };

  const openEditModal = (item) => {
    setSelectedItem({ ...item });
    setShowEditModal(true);
  };

  const handleSave = async () => {
    try {
      await updateEquipmentItem(selectedItem);
      await openItems(selectedEquipment);
      setShowEditModal(false);
      setSuccessMessage("Item updated successfully!");
      setTimeout(() => setSuccessMessage(""), 3000);
    } catch (err) {
      console.error("Error updating item:", err);
    }
  };

  return (
    <div className="w-full min-h-screen bg-gray-50 py-10 px-4 text-gray-800">
      {successMessage && (
        <motion.div
          initial={{ opacity: 0, y: -20 }}
          animate={{ opacity: 1, y: 0 }}
          exit={{ opacity: 0, y: -20 }}
          className="fixed top-4 left-1/2 transform -translate-x-1/2 -translate-y-1/2 px-6 py-3 bg-green-100 border border-green-400 text-green-700 rounded-lg shadow-xl z-[100] max-w-lg w-full text-center font-medium">
          ✅ {successMessage}
        </motion.div>
      )}

      <h1 className="text-4xl font-bold text-center mb-10 text-slate-800">Admin Equipment Management</h1>

      <div className="max-w-5xl mx-auto grid gap-8 grid-cols-1">
        {equipment.map((item) => (
          <motion.div
            key={item.equipmentId}
            className="bg-white rounded-2xl shadow-md hover:shadow-xl transition-all duration-300 p-8 border border-gray-100"
            whileHover={{ scale: 1.02 }}
          >
            <div className="flex justify-between items-center">
              <h2 className="text-2xl font-bold">{item.name}</h2>
              <button
                onClick={() => openItems(item.equipmentId)}
                className="px-4 py-2 bg-indigo-600 text-white rounded-lg hover:bg-indigo-700 transition"
              >
                View Items
              </button>
            </div>
          </motion.div>
        ))}
      </div>

      {showItemModal && (
        <div className="fixed inset-0 bg-black bg-opacity-40 z-50">
          <div
            className="bg-white rounded-2xl shadow-lg p-8 w-full max-w-4xl max-h-[80vh] overflow-y-auto
            absolute top-1/2 left-1/2 transform -translate-x-1/2 -translate-y-1/2"
          >
            <button
              onClick={() => {
                setShowItemModal(false);
                setItems([]);
              }}
              className="absolute top-4 right-4 text-gray-600 hover:text-gray-800 text-2xl"
            >
              ✖
            </button>

            <h2 className="text-2xl font-semibold mb-6 text-slate-700">
              Items for this Equipment
            </h2>

            {items.length > 0 ? (
              <div className="grid gap-6 grid-cols-1">
                {items.map((it) => (
                  <motion.div
                    key={it.itemId}
                    className="bg-gray-50 rounded-xl shadow p-6 border border-gray-100 flex justify-between items-center"
                    whileHover={{ scale: 1.01 }}
                  >
                    <div>
                      <p className="text-lg font-semibold text-gray-800">{it.equipmentName}</p>
                      <p className="text-sm text-gray-500">Serial: {it.serialNumber}</p>
                      <p className="text-sm text-gray-500">Condition: {it.condition}</p>
                      <p className={`text-sm font-semibold ${it.isAvailable ? "text-green-600" : "text-red-600"}`}>
                        {it.isAvailable ? "Available" : "Unavailable"}
                      </p>
                    </div>

                    <button
                      onClick={() => openEditModal(it)}
                      className="px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition"
                    >
                      Edit
                    </button>
                  </motion.div>
                ))}
              </div>
            ) : (
              <p className="text-center text-gray-500">No items found.</p>
            )}
          </div>
        </div>
      )}


      {showEditModal && selectedItem && (
        <div className="fixed inset-0 bg-black bg-opacity-40 z-[60]">
          <div
            className="bg-white rounded-2xl shadow-lg p-8 w-full max-w-md
            absolute top-1/2 left-1/2 transform -translate-x-1/2 -translate-y-1/2"
          >
            <div className="flex justify-between items-center mb-4">
              <h2 className="text-2xl font-semibold">Edit Item</h2>
              <button onClick={() => setShowEditModal(false)} className="text-gray-500 hover:text-gray-700">
                ✖
              </button>
            </div>

            <div className="space-y-4">
              <div>
                <label className="block text-gray-700 mb-1">Equipment Name</label>
                <input
                  type="text"
                  value={selectedItem.equipmentName}
                  onChange={(e) => setSelectedItem({ ...selectedItem, equipmentName: e.target.value })}
                  className="w-full border rounded-md p-2"
                />
              </div>

              <div>
                <label className="block text-gray-700 mb-1">Serial Number</label>
                <input
                  type="text"
                  value={selectedItem.serialNumber}
                  onChange={(e) => setSelectedItem({ ...selectedItem, serialNumber: e.target.value })}
                  className="w-full border rounded-md p-2"
                />
              </div>

              <div>
                <label className="block text-gray-700 mb-1">Condition</label>
                <input
                  type="text"
                  value={selectedItem.condition}
                  onChange={(e) => setSelectedItem({ ...selectedItem, condition: e.target.value })}
                  className="w-full border rounded-md p-2"
                />
              </div>

              <div>
                <label className="block text-gray-700 mb-1">Availability</label>
                <select
                  value={selectedItem.isAvailable}
                  onChange={(e) =>
                    setSelectedItem({ ...selectedItem, isAvailable: e.target.value === "true" })
                  }
                  className="w-full border rounded-md p-2"
                >
                  <option value="true">Available</option>
                  <option value="false">Unavailable</option>
                </select>
              </div>
            </div>

            <div className="flex justify-end mt-6 gap-3">
              <button
                onClick={() => setShowEditModal(false)}
                className="px-4 py-2 bg-gray-300 rounded-lg hover:bg-gray-400"
              >
                Cancel
              </button>
              <button
                onClick={handleSave}
                className="px-4 py-2 bg-green-600 text-white rounded-lg hover:bg-green-700"
              >
                Save Changes
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}
