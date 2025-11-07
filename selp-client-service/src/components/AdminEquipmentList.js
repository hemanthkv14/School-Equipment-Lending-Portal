import React, { useState, useEffect } from "react";
import { motion } from "framer-motion";
import {
  getAllEquipment,
  getAllItemDetails,
  updateEquipmentItem,
  addNewCategory,
  addNewEquipment,
  getAllCategories,
  updateEquipment,
} from "../api/equipmentApi";

export default function AdminEquipmentList() {
  const [equipment, setEquipment] = useState([]);
  const [categories, setCategories] = useState([]);
  const [selectedEquipment, setSelectedEquipment] = useState(null);
  const [selectedEquipmentToEdit, setSelectedEquipmentToEdit] = useState(null); // New state for editing equipment
  const [items, setItems] = useState([]);
  const [selectedItem, setSelectedItem] = useState(null);
  const [showItemModal, setShowItemModal] = useState(false);
  const [showEditItemModal, setShowEditItemModal] = useState(false); // Renamed for clarity
  const [showCategoryModal, setShowCategoryModal] = useState(false);
  const [showEquipmentModal, setShowEquipmentModal] = useState(false);
  const [showEditEquipmentModal, setShowEditEquipmentModal] = useState(false); // New state for Update Equipment Modal
  const [newCategoryName, setNewCategoryName] = useState("");
  const [newEquipmentData, setNewEquipmentData] = useState({
    name: "",
    categoryId: "",
    totalQuantity: 0,
    quantityAvailable: 0,
  });
  const [successMessage, setSuccessMessage] = useState("");

  useEffect(() => {
    loadEquipment();
    loadCategories();
  }, []);

  // --- Load Data Functions ---
  const loadEquipment = async () => {
    try {
      const data = await getAllEquipment();
      setEquipment(Array.isArray(data) ? data : []);
    } catch {
      console.log("Error loading equipment");
    }
  };

  const loadCategories = async () => {
    try {
      const data = await getAllCategories();
      setCategories(Array.isArray(data) ? data : []);
    } catch {
      console.log("Error loading categories");
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

  // --- Item Edit Functions ---
  const openEditItemModal = (item) => {
    setSelectedItem({ ...item });
    setShowEditItemModal(true);
  };

  const handleSaveItem = async () => {
    try {
      await updateEquipmentItem(selectedItem);
      await openItems(selectedEquipment);
      setShowEditItemModal(false);
      setSuccessMessage("Item updated successfully!");
      setTimeout(() => setSuccessMessage(""), 3000);
    } catch (err) {
      console.error("Error updating item:", err);
    }
  };

  // --- Equipment Update Functions ---
  const openEditEquipmentModal = (equipment) => {
    // Note: categoryId might be nested or named differently in the response,
    // assuming it's directly on the equipment object for now.
    setSelectedEquipmentToEdit({
        equipmentId: equipment.equipmentId,
        name: equipment.name,
        categoryId: equipment.categoryId,
        totalQuantity: equipment.totalQuantity,
        quantityAvailable: equipment.quantityAvailable
    });
    setShowEditEquipmentModal(true);
  };

  const handleUpdateEquipment = async () => {
      if (!selectedEquipmentToEdit.name.trim() || !selectedEquipmentToEdit.categoryId || selectedEquipmentToEdit.totalQuantity < 0) {
          alert("Please fill all fields.");
          return;
      }

      try {
          const dataToSend = {
              ...selectedEquipmentToEdit,
              categoryId: parseInt(selectedEquipmentToEdit.categoryId),
              totalQuantity: parseInt(selectedEquipmentToEdit.totalQuantity),
              quantityAvailable: parseInt(selectedEquipmentToEdit.quantityAvailable), // Ensure these are ints
          };

          await updateEquipment(dataToSend); // API call to update equipment
          await loadEquipment(); // Refresh the main list
          setShowEditEquipmentModal(false);
          setSuccessMessage(`Equipment "${dataToSend.name}" updated successfully!`);
          setTimeout(() => setSuccessMessage(""), 3000);
      } catch (err) {
          console.error("Error updating equipment:", err);
          alert("Failed to update equipment.");
      }
  };

  // --- Add Category Functions ---
  const handleAddCategory = async () => {
    if (!newCategoryName.trim()) {
      alert("Category name cannot be empty.");
      return;
    }
    try {
      await addNewCategory({ name: newCategoryName });
      await loadCategories();
      setShowCategoryModal(false);
      setNewCategoryName("");
      setSuccessMessage("Category added successfully!");
      setTimeout(() => setSuccessMessage(""), 3000);
    } catch (err) {
      console.error("Error adding category:", err);
      alert("Failed to add category.");
    }
  };

  // --- Add Equipment Functions ---
  const handleAddEquipment = async () => {
    if (!newEquipmentData.name.trim() || !newEquipmentData.categoryId || newEquipmentData.totalQuantity <= 0) {
      alert("Please fill all fields and ensure Total Quantity is greater than 0.");
      return;
    }

    try {
      const dataToSend = {
        ...newEquipmentData,
        categoryId: parseInt(newEquipmentData.categoryId),
        totalQuantity: parseInt(newEquipmentData.totalQuantity),
        quantityAvailable: parseInt(newEquipmentData.totalQuantity),
      };

      await addNewEquipment(dataToSend);
      await loadEquipment();
      setShowEquipmentModal(false);
      setNewEquipmentData({
        name: "",
        categoryId: "",
        totalQuantity: 0,
        quantityAvailable: 0,
      });
      setSuccessMessage("Equipment added successfully!");
      setTimeout(() => setSuccessMessage(""), 3000);
    } catch (err) {
      console.error("Error adding equipment:", err);
      alert("Failed to add equipment.");
    }
  };

  // --- Render ---
  return (
    <div className="w-full min-h-screen bg-gray-50 py-10 px-4 text-gray-800">
      {successMessage && (
        <motion.div
          initial={{ opacity: 0, y: -20 }}
          animate={{ opacity: 1, y: 0 }}
          exit={{ opacity: 0, y: -20 }}
          className="fixed top-4 left-1/2 transform -translate-x-1/2 px-6 py-3 bg-green-100 border border-green-400 text-green-700 rounded-lg shadow-xl z-[100] max-w-lg w-full text-center font-medium">
          ✅ {successMessage}
        </motion.div>
      )}

      <h1 className="text-4xl font-bold text-center mb-10 text-slate-800">Admin Equipment Management</h1>

      <div className="max-w-5xl mx-auto mb-8 flex justify-end space-x-4">
        <button
          onClick={() => setShowCategoryModal(true)}
          className="px-4 py-2 bg-orange-500 text-white rounded-lg hover:bg-orange-600 transition font-medium shadow-md"
        >
          Add New Category
        </button>
        <button
          onClick={() => {
            if (categories.length === 0) {
              alert("Please add a category first.");
              return;
            }
            setShowEquipmentModal(true);
          }}
          className="px-4 py-2 bg-cyan-600 text-white rounded-lg hover:bg-cyan-700 transition font-medium shadow-md"
        >
          Add New Equipment
        </button>
      </div>

      <div className="max-w-5xl mx-auto grid gap-8 grid-cols-1">
        {equipment.map((item) => (
          <motion.div
            key={item.equipmentId}
            className="bg-white rounded-2xl shadow-md hover:shadow-xl transition-all duration-300 p-8 border border-gray-100"
            whileHover={{ scale: 1.02 }}
          >
            <div className="flex justify-between items-center">
              <h2 className="text-2xl font-bold">{item.name}</h2>
              <div className="space-x-3 flex items-center">
                <button
                    onClick={() => openEditEquipmentModal(item)}
                    className="px-4 py-2 bg-amber-500 text-white rounded-lg hover:bg-amber-600 transition"
                >
                    Update
                </button>
                <button
                    onClick={() => openItems(item.equipmentId)}
                    className="px-4 py-2 bg-indigo-600 text-white rounded-lg hover:bg-indigo-700 transition"
                >
                    View Items
                </button>
              </div>
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
                      onClick={() => openEditItemModal(it)}
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

      {showEditItemModal && selectedItem && (
        <div className="fixed inset-0 bg-black bg-opacity-40 z-[60]">
          <div
            className="bg-white rounded-2xl shadow-lg p-8 w-full max-w-md
            absolute top-1/2 left-1/2 transform -translate-x-1/2 -translate-y-1/2"
          >
            <div className="flex justify-between items-center mb-4">
              <h2 className="text-2xl font-semibold">Edit Item</h2>
              <button onClick={() => setShowEditItemModal(false)} className="text-gray-500 hover:text-gray-700">
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
                onClick={() => setShowEditItemModal(false)}
                className="px-4 py-2 bg-gray-300 rounded-lg hover:bg-gray-400"
              >
                Cancel
              </button>
              <button
                onClick={handleSaveItem}
                className="px-4 py-2 bg-green-600 text-white rounded-lg hover:bg-green-700"
              >
                Save Changes
              </button>
            </div>
          </div>
        </div>
      )}

      {showCategoryModal && (
        <div className="fixed inset-0 bg-black bg-opacity-40 z-[70]">
          <div
            className="bg-white rounded-2xl shadow-lg p-8 w-full max-w-md
            absolute top-1/2 left-1/2 transform -translate-x-1/2 -translate-y-1/2"
          >
            <div className="flex justify-between items-center mb-4">
              <h2 className="text-2xl font-semibold">Add New Category</h2>
              <button onClick={() => setShowCategoryModal(false)} className="text-gray-500 hover:text-gray-700">
                ✖
              </button>
            </div>

            <div className="space-y-4">
              <div>
                <label htmlFor="categoryName" className="block text-gray-700 mb-1">Category Name</label>
                <input
                  id="categoryName"
                  type="text"
                  value={newCategoryName}
                  onChange={(e) => setNewCategoryName(e.target.value)}
                  className="w-full border rounded-md p-2"
                  placeholder="e.g., Physics Equipment"
                />
              </div>
            </div>

            <div className="flex justify-end mt-6 gap-3">
              <button
                onClick={() => setShowCategoryModal(false)}
                className="px-4 py-2 bg-gray-300 rounded-lg hover:bg-gray-400"
              >
                Cancel
              </button>
              <button
                onClick={handleAddCategory}
                className="px-4 py-2 bg-orange-500 text-white rounded-lg hover:bg-orange-600"
              >
                Add Category
              </button>
            </div>
          </div>
        </div>
      )}

      {showEquipmentModal && (
        <div className="fixed inset-0 bg-black bg-opacity-40 z-[70]">
          <div
            className="bg-white rounded-2xl shadow-lg p-8 w-full max-w-md
            absolute top-1/2 left-1/2 transform -translate-x-1/2 -translate-y-1/2"
          >
            <div className="flex justify-between items-center mb-4">
              <h2 className="text-2xl font-semibold">Add New Equipment</h2>
              <button onClick={() => setShowEquipmentModal(false)} className="text-gray-500 hover:text-gray-700">
                ✖
              </button>
            </div>

            <div className="space-y-4">
              <div>
                <label htmlFor="equipmentName" className="block text-gray-700 mb-1">Equipment Name</label>
                <input
                  id="equipmentName"
                  type="text"
                  value={newEquipmentData.name}
                  onChange={(e) => setNewEquipmentData({ ...newEquipmentData, name: e.target.value })}
                  className="w-full border rounded-md p-2"
                  placeholder="e.g., Cricket Bat"
                />
              </div>

              <div>
                <label htmlFor="category" className="block text-gray-700 mb-1">Category</label>
                <select
                  id="category"
                  value={newEquipmentData.categoryId}
                  onChange={(e) => setNewEquipmentData({ ...newEquipmentData, categoryId: e.target.value })}
                  className="w-full border rounded-md p-2"
                >
                  <option value="">Select a Category</option>
                  {categories.map((cat) => (
                    <option key={cat.categoryId} value={cat.categoryId}>
                      {cat.name}
                    </option>
                  ))}
                </select>
              </div>

              <div>
                <label htmlFor="totalQuantity" className="block text-gray-700 mb-1">Total Quantity</label>
                <input
                  id="totalQuantity"
                  type="number"
                  min="1"
                  value={newEquipmentData.totalQuantity}
                  onChange={(e) => setNewEquipmentData({ ...newEquipmentData, totalQuantity: e.target.value, quantityAvailable: e.target.value })}
                  className="w-full border rounded-md p-2"
                  placeholder="e.g., 15"
                />
                <p className="text-xs text-gray-500 mt-1">Quantity Available will be set to Total Quantity initially.</p>
              </div>
            </div>

            <div className="flex justify-end mt-6 gap-3">
              <button
                onClick={() => setShowEquipmentModal(false)}
                className="px-4 py-2 bg-gray-300 rounded-lg hover:bg-gray-400"
              >
                Cancel
              </button>
              <button
                onClick={handleAddEquipment}
                className="px-4 py-2 bg-cyan-600 text-white rounded-lg hover:bg-cyan-700"
              >
                Add Equipment
              </button>
            </div>
          </div>
        </div>
      )}
      {showEditEquipmentModal && selectedEquipmentToEdit && (
        <div className="fixed inset-0 bg-black bg-opacity-40 z-[70]">
          <div
            className="bg-white rounded-2xl shadow-lg p-8 w-full max-w-md
            absolute top-1/2 left-1/2 transform -translate-x-1/2 -translate-y-1/2"
          >
            <div className="flex justify-between items-center mb-4">
              <h2 className="text-2xl font-semibold">Update Equipment</h2>
              <button onClick={() => setShowEditEquipmentModal(false)} className="text-gray-500 hover:text-gray-700">
                ✖
              </button>
            </div>

            <div className="space-y-4">
              <div>
                <label className="block text-gray-700 mb-1">Equipment Name</label>
                <input
                  type="text"
                  value={selectedEquipmentToEdit.name}
                  onChange={(e) => setSelectedEquipmentToEdit({ ...selectedEquipmentToEdit, name: e.target.value })}
                  className="w-full border rounded-md p-2"
                />
              </div>

              <div>
                <label className="block text-gray-700 mb-1">Category</label>
                <select
                  value={selectedEquipmentToEdit.categoryId}
                  onChange={(e) => setSelectedEquipmentToEdit({ ...selectedEquipmentToEdit, categoryId: e.target.value })}
                  className="w-full border rounded-md p-2"
                >
                  <option value="">Select a Category</option>
                  {categories.map((cat) => (
                    <option key={cat.categoryId} value={cat.categoryId}>
                      {cat.name}
                    </option>
                  ))}
                </select>
              </div>

              <div>
                <label className="block text-gray-700 mb-1">Total Quantity</label>
                <input
                  type="number"
                  min="0"
                  value={selectedEquipmentToEdit.totalQuantity}
                  onChange={(e) => setSelectedEquipmentToEdit({ ...selectedEquipmentToEdit, totalQuantity: e.target.value })}
                  className="w-full border rounded-md p-2"
                />
              </div>

              <div>
                <label className="block text-gray-700 mb-1">Quantity Available</label>
                <input
                  type="number"
                  min="0"
                  value={selectedEquipmentToEdit.quantityAvailable}
                  onChange={(e) => setSelectedEquipmentToEdit({ ...selectedEquipmentToEdit, quantityAvailable: e.target.value })}
                  className="w-full border rounded-md p-2"
                />
              </div>
            </div>

            <div className="flex justify-end mt-6 gap-3">
              <button
                onClick={() => setShowEditEquipmentModal(false)}
                className="px-4 py-2 bg-gray-300 rounded-lg hover:bg-gray-400"
              >
                Cancel
              </button>
              <button
                onClick={handleUpdateEquipment}
                className="px-4 py-2 bg-green-600 text-white rounded-lg hover:bg-green-700"
              >
                Save Updates
              </button>
            </div>
          </div>
        </div>
      )}

    </div>
  );
}