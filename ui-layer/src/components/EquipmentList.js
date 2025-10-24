import React, { useEffect, useState } from "react";
import { getAllEquipment } from "../api/equipmentApi";

export default function EquipmentList() {
    const [equipment, setEquipment] = useState([]);

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

    return (
        <div className="bg-gray-50 py-10 min-h-screen">
            <div className="w-full max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
                <header className="text-center mb-10">
                    <h1 className="text-4xl font-bold text-gray-800">Equipment List</h1>
                    <p className="text-gray-500 mt-2">
                        View all available equipment from the school inventory
                    </p>
                </header>
            </div>

            <div className="w-full px-4 sm:px-6 lg:px-8 max-w-7xl mx-auto">
                <div className="grid gap-8 grid-cols-1">
                    {equipment.length === 0 ? (
                        <p className="text-center text-gray-600 col-span-full py-10">
                            No equipment found.
                        </p>
                    ) : (
                        equipment.map((item) => (
                            <div
                                key={item.equipmentId}
                                className="bg-white rounded-2xl shadow-xl hover:shadow-2xl transition-all duration-300 p-8 flex flex-col justify-between border border-gray-100"
                            >
                                <div className="md:flex md:justify-between md:items-center">
                                    <div className=" mb-4 md:mb-0">
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
                                            <span className="ml-1">{item.condition}</span>
                                        </p>
                                        <p>
                                            <span className="font-semibold text-gray-700">Total:</span>{" "}
                                            <span className="ml-1 text-gray-800 font-bold">{item.quantityTotal}</span>
                                        </p>

                                        <p>
                                            <span className="font-semibold text-gray-700">Available:</span>{" "}
                                            <span className="ml-1 text-green-600 font-bold">{item.quantityAvailable}</span>
                                        </p>
                                    </div>
                                </div>
                            </div>
                        ))
                    )}
                </div>
            </div>
        </div>
    );
}