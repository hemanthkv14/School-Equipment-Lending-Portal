import React, { useEffect, useState } from "react";
import { getAllEquipment } from "../api/equipmentApi";

export default function EquipmentList() {
    const [equipment, setEquipment] = useState([]);
    const [searchTerm, setSearchTerm] = useState("");

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

    const filteredEquipment = equipment.filter((item) =>
        item.equipmentName.toLowerCase().includes(searchTerm.toLowerCase())
    );

    return (
        <div className="w-full min-h-screen py-10 px-4 sm:px-6 lg:px-8 text-gray-800">
            <header className="text-center mb-10">
                <h1 className="text-4xl font-bold text-slate-800">Equipment List</h1>
                <p className="text-white mt-2">
                    View all available equipment from the school inventory
                </p>
            </header>

            <div className="max-w-4xl mx-auto mb-8">
                <input
                    type="text"
                    placeholder="🔍 Search by Equipment Name"
                    value={searchTerm}
                    onChange={(e) => setSearchTerm(e.target.value)}
                    className="w-full p-3 rounded-xl border border-gray-300 shadow-sm focus:outline-none focus:ring-2 focus:ring-indigo-500 text-gray-700"
                />
            </div>

            <div className="max-w-5xl mx-auto grid gap-8 grid-cols-1">
                {filteredEquipment.length === 0 ? (
                    <p className="text-center text-gray-600 py-10">
                        No equipment found.
                    </p>
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
                                        <span className="text-green-600 font-bold">{item.quantityAvailable}</span>
                                    </p>
                                </div>
                            </div>
                        </div>
                    ))
                )}
            </div>
        </div>
    );
}
