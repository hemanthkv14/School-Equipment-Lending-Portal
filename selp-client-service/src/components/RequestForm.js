import React, { useState, useEffect } from "react";
import { useParams, useNavigate } from "react-router-dom";
import { getAllEquipment } from "../api/equipmentApi";
import { createBorrowRequest } from "../api/borrowApi";

export default function RequestForm() {
    const { equipmentId } = useParams();
    const [quantity, setQuantity] = useState(1);
    const [equipment, setEquipment] = useState(null);
    const [error, setError] = useState("");
    const navigate = useNavigate();

    useEffect(() => {
        const loadEquipment = async () => {
            if (!equipmentId) return;

            try {
                const allEquipment = await getAllEquipment();
                const eq = allEquipment.find((e) => e.equipmentId === Number(equipmentId));
                setEquipment(eq);
            } catch (err) {
                console.error("Error loading equipment:", err);
                setError("Could not load equipment details.");
            }
        };
        loadEquipment();
    }, [equipmentId]);

    const handleQuantityChange = (e) => {
        const val = Number(e.target.value);
        setQuantity(val);
        if (equipment && val > equipment.quantityAvailable) {
            setError(`Maximum available quantity is ${equipment.quantityAvailable}`);
        } else {
            setError("");
        }
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        if (!equipment) return;

        const borrowRequestDTO = {
            equipmentId: equipment.equipmentId,
            quantity,
            requestedBy: 1, //TODO: Replace with user ID
        };
        console.log("borrowRequestDTO", borrowRequestDTO);

        try {
            await createBorrowRequest(borrowRequestDTO);
            alert("Request created successfully!");
            navigate("/");
        } catch (err) {
            console.error(err);
            alert("Failed to create request: " + (err.response?.data || err.message));
        }
    };

    if (error && error.includes("Could not load")) return <p className="text-center mt-10 text-red-600">Error: {error}</p>;
    if (!equipment) return <p className="text-center mt-10">Loading...</p>;

    return (
        <div className="max-w-xl mx-auto mt-10 p-6 bg-white rounded-xl shadow-md">
            <h1 className="text-2xl font-bold mb-4">New Equipment Request</h1>
            <form onSubmit={handleSubmit} className="space-y-4">
                <div>
                    <label className="block font-semibold mb-1">Equipment Name</label>
                    <input
                        type="text"
                        value={equipment.equipmentName}
                        disabled
                        className="w-full border p-2 rounded-md bg-gray-100"
                    />
                </div>
                <div>
                    <label className="block font-semibold mb-1">
                        Quantity (Available: {equipment.quantityAvailable})
                    </label>
                    <input
                        type="number"
                        value={quantity}
                        onChange={handleQuantityChange}
                        className="w-full border p-2 rounded-md"
                        min="1"
                        max={equipment.quantityAvailable}
                    />
                    {error && <p className="text-red-600 text-sm mt-1">{error}</p>}
                </div>
                <button
                    type="submit"
                    disabled={quantity > equipment.quantityAvailable || quantity < 1}
                    className={`px-4 py-2 rounded-md text-white ${
                        quantity > equipment.quantityAvailable || quantity < 1
                            ? "bg-gray-400 cursor-not-allowed"
                            : "bg-indigo-600 hover:bg-indigo-700"
                    }`}
                >
                    Submit Request
                </button>
            </form>
        </div>
    );
}