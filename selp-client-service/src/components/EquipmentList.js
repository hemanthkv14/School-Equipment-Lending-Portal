import { useEffect, useState } from "react";
import { motion, AnimatePresence } from "framer-motion";
import { useNavigate } from "react-router-dom";
import { getAllEquipment, getAllCategories, getAllItemDetails } from "../api/equipmentApi";

export default function EquipmentList() {
  const [equipment, setEquipment] = useState([]);
  const [categories, setCategories] = useState({});
  const [searchTerm, setSearchTerm] = useState("");
  const [categoryFilter, setCategoryFilter] = useState("All");
  const [availabilityFilter, setAvailabilityFilter] = useState("All");
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [itemDetails, setItemDetails] = useState([]);
  const navigate = useNavigate();

  useEffect(() => {
    loadEquipment();
  }, []);

  const loadEquipment = async () => {
    try {
      const [equipmentData, categoryData] = await Promise.all([
        getAllEquipment(),
        getAllCategories(),
      ]);

      const categoryMap = {};
      categoryData.forEach((cat) => (categoryMap[cat.categoryId] = cat.name));

      const equipmentWithCategory = equipmentData.map((item) => ({
        ...item,
        categoryName: categoryMap[item.categoryId] || "Unknown",
      }));

      setEquipment(equipmentWithCategory);
      setCategories(categoryMap);
    } catch (error) {
      console.error("Error fetching equipment:", error);
    }
  };

  const viewItemDetails = async (equipmentId) => {
    try {
      const details = await getAllItemDetails(equipmentId);
      setItemDetails(details);
      setIsModalOpen(true);
    } catch (error) {
      console.error("Error fetching item details:", error);
    }
  };

  const filteredEquipment = equipment.filter((item) => {
    const matchesSearch = item.name.toLowerCase().includes(searchTerm.toLowerCase());
    const matchesCategory =
      categoryFilter === "All" || item.categoryName === categoryFilter;
    const matchesAvailability =
      availabilityFilter === "All" ||
      (availabilityFilter === "Available" && item.quantityAvailable > 0) ||
      (availabilityFilter === "Out of Stock" && item.quantityAvailable === 0);
    return matchesSearch && matchesCategory && matchesAvailability;
  });

  const uniqueCategories = ["All", ...new Set(equipment.map((item) => item.categoryName))];

  return (
    <div className="w-full min-h-screen py-10 px-4 text-gray-800 bg-gray-50">
      <header className="text-center mb-10">
        <h1 className="text-4xl font-bold text-slate-800">Equipment List</h1>
      </header>

      <div className="max-w-4xl mx-auto mb-8 flex flex-wrap gap-4 justify-center">
        <input
          type="text"
          placeholder="🔍 Search by Equipment Name"
          value={searchTerm}
          onChange={(e) => setSearchTerm(e.target.value)}
          className="w-full md:w-64 p-3 rounded-xl border border-gray-300 focus:ring-2 focus:ring-indigo-500"
        />
        <select
          value={categoryFilter}
          onChange={(e) => setCategoryFilter(e.target.value)}
          className="w-full md:w-48 p-3 rounded-xl border border-gray-300 focus:ring-2 focus:ring-indigo-500"
        >
          {uniqueCategories.map((cat) => (
            <option key={cat} value={cat}>{cat}</option>
          ))}
        </select>
        <select
          value={availabilityFilter}
          onChange={(e) => setAvailabilityFilter(e.target.value)}
          className="w-full md:w-48 p-3 rounded-xl border border-gray-300 focus:ring-2 focus:ring-indigo-500"
        >
          <option value="All">All</option>
          <option value="Available">Available</option>
          <option value="Out of Stock">Out of Stock</option>
        </select>
      </div>

      <div className="max-w-5xl mx-auto grid gap-8 grid-cols-1">
        {filteredEquipment.map((item) => (
          <motion.div
            key={item.equipmentId}
            className="bg-white rounded-2xl shadow-md hover:shadow-xl p-8 border"
            initial={{ opacity: 0, y: 10 }}
            animate={{ opacity: 1, y: 0 }}
          >
            <div className="flex flex-col md:flex-row md:justify-between">
              <div>
                <h2 className="text-2xl font-bold">{item.name}</h2>
                <p className="text-sm text-indigo-600">{item.categoryName}</p>
              </div>
              <div className="flex items-center gap-6 text-sm">
                <p>Total: {item.totalQuantity}</p>
                <p>
                  Available:{" "}
                  <span className={item.quantityAvailable > 0 ? "text-green-600" : "text-red-600"}>
                    {item.quantityAvailable}
                  </span>
                </p>
                {item.quantityAvailable > 0 && (
                  <button
                    onClick={() => viewItemDetails(item.equipmentId)}
                    className="bg-green-600 hover:bg-green-700 text-white px-4 py-2 rounded-lg"
                  >
                    View Details
                  </button>
                )}
              </div>
            </div>
          </motion.div>
        ))}
      </div>

      <AnimatePresence>
        {isModalOpen && (
          <motion.div
            className="fixed inset-0 flex items-center justify-center bg-black bg-opacity-50 z-50"
            initial={{ opacity: 0 }}
            animate={{ opacity: 1 }}
            exit={{ opacity: 0 }}
          >
            <motion.div
              className="bg-white rounded-2xl shadow-lg p-6 w-full max-w-2xl max-h-[80vh] overflow-y-auto relative"
              initial={{ scale: 0.8, opacity: 0 }}
              animate={{ scale: 1, opacity: 1 }}
              exit={{ scale: 0.8, opacity: 0 }}
            >
              <button
                onClick={() => setIsModalOpen(false)}
                className="absolute top-4 right-4 text-gray-500 hover:text-gray-800"
              >
                ✕
              </button>

              <h2 className="text-2xl font-semibold mb-4">Item Details</h2>

              {itemDetails.map((item) => (
                <div
                  key={item.itemId}
                  className="flex justify-between items-center border p-3 rounded-lg mb-2"
                >
                  <div>
                    <p className="font-semibold">{item.serialNumber}</p>
                    <p className="text-sm text-gray-600">Condition: {item.condition}</p>
                  </div>
                  {item.isAvailable ? (
                    <button
                      className="bg-indigo-600 hover:bg-indigo-700 text-white px-4 py-1 rounded-lg"
                      onClick={() => navigate(`/request/${item.itemId}`)}
                    >
                      Request
                    </button>
                  ) : (
                    <span className="text-red-500">Not Available</span>
                  )}
                </div>
              ))}
            </motion.div>
          </motion.div>
        )}
      </AnimatePresence>
    </div>
  );
}
