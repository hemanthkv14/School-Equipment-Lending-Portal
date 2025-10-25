import React, { useEffect, useState } from "react";
import { getAllEquipment } from "../api/equipmentApi";
import { useNavigate } from "react-router-dom";

export default function EquipmentList() {
    const [equipment, setEquipment] = useState([]);
    const [searchTerm, setSearchTerm] = useState("");
    const [categoryFilter, setCategoryFilter] = useState("All");
    const [availabilityFilter, setAvailabilityFilter] = useState("All");
    const navigate = useNavigate();

    useEffect(() => {
        loadEquipment();
    }, []);

    const loadEquipment = async () => {
        try {
            const data = await getAllEquipment();
            setEquipment(data);
        } catch (error) {
            console.error("Error fetching equipment:", error);
        }
    };

    const filteredEquipment = equipment.filter((item) => {
        const matchesSearch = item.equipmentName.toLowerCase().includes(searchTerm.toLowerCase());
        const matchesCategory = categoryFilter === "All" || item.category === categoryFilter;
        const matchesAvailability =
            availabilityFilter === "All" ||
            (availabilityFilter === "Available" && item.quantityAvailable > 0) ||
            (availabilityFilter === "Out of Stock" && item.quantityAvailable === 0);

        return matchesSearch && matchesCategory && matchesAvailability;
    });

    const uniqueCategories = ["All", ...new Set(equipment.map((item) => item.category))];

    return (
        <div className="w-full min-h-screen py-10 px-4 text-gray-800">
            <header className="text-center mb-10">
                <h1 className="text-4xl font-bold text-slate-800">Equipment List</h1>
                <p className="text-gray-600 mt-2">
                    View all available equipment from the school inventory
                </p>
            </header>

            <div className="max-w-4xl mx-auto mb-8 flex flex-wrap gap-4 justify-center">
                <input
                    type="text"
                    placeholder="🔍 Search by Equipment Name"
                    value={searchTerm}
                    onChange={(e) => setSearchTerm(e.target.value)}
                    className="w-full md:w-64 p-3 rounded-xl border border-gray-300 shadow-sm focus:outline-none focus:ring-2 focus:ring-indigo-500 text-gray-700"
                />

                <select
                    value={categoryFilter}
                    onChange={(e) => setCategoryFilter(e.target.value)}
                    className="w-full md:w-48 p-3 rounded-xl border border-gray-300  focus:outline-none focus:ring-2 focus:ring-indigo-500 text-gray-700"
                >
                    {uniqueCategories.map((cat) => (
                        <option key={cat} value={cat}>
                            {cat}
                        </option>
                    ))}
                </select>

                <select
                    value={availabilityFilter}
                    onChange={(e) => setAvailabilityFilter(e.target.value)}
                    className="w-full md:w-48 p-3 rounded-xl border border-gray-300 focus:outline-none focus:ring-2 focus:ring-indigo-500 text-gray-700"
                >
                    <option value="All">All</option>
                    <option value="Available">Available</option>
                    <option value="Out of Stock">Out of Stock</option>
                </select>
            </div>

            {/* Equipment Cards */}
            <div className="max-w-5xl mx-auto grid gap-8 grid-cols-1">
                {filteredEquipment.length === 0 ? (
                    <p className="text-center text-gray-600 py-10">No equipment found.</p>
                ) : (
                    filteredEquipment.map((item) => (
                        <div
                            key={item.equipmentId}
                            className="bg-white rounded-2xl shadow-md hover:shadow-xl transition-all duration-300 p-8 flex flex-col justify-between border border-gray-100"
                        >
                            <div className="md:flex md:justify-between md:items-center">
                                <div className="mb-4 md:mb-0">
                                    <h2 className="text-2xl font-bold text-gray-800 mb-1">
                                        {item.equipmentName}
                                    </h2>
                                    <p className="text-sm text-indigo-600 font-medium">
                                        {item.category}
                                    </p>
                                </div>
                                <div className="flex flex-wrap gap-x-10 gap-y-1 text-sm text-gray-600">
                                    <p>
                                        <span className="font-semibold text-gray-700">Condition:</span>{" "}
                                        {item.condition}
                                    </p>
                                    <p>
                                        <span className="font-semibold text-gray-700">Total:</span>{" "}
                                        {item.quantityTotal}
                                    </p>
                                    <p>
                                        <span className="font-semibold text-gray-700">Available:</span>{" "}
                                        <span
                                            className={`font-bold ${
                                                item.quantityAvailable > 0
                                                    ? "text-green-600"
                                                    : "text-red-600"
                                            }`}
                                        >
                                            {item.quantityAvailable}
                                        </span>
                                    </p>
                                    <div>
                                    {item.quantityAvailable > 0 && (
                                        <button
                                            onClick={() => navigate(`/new-request/${item.equipmentId}`)}
                                            className="ml-4 bg-green-600 text-white px-4 py-1 rounded"
                                        >
                                            Request
                                        </button>
                                    )}
                                    </div>
                                </div>
                            </div>
                        </div>
                    ))
                )}
            </div>
        </div>
    );
}
