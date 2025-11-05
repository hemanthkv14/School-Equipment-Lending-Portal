import {useParams, useNavigate} from "react-router-dom";
import {useState} from "react";
import {motion, AnimatePresence} from "framer-motion";
import {createBorrowRequest} from "../api/equipmentApi";

export default function RequestForm() {
    const {itemId} = useParams();
    const navigate = useNavigate();
    const [isSuccess, setIsSuccess] = useState(false);
    const borrowerId = localStorage.getItem("userId");

    const handleSubmit = async (e) => {
        e.preventDefault();
        try {
            await createBorrowRequest({itemId: Number(itemId), borrowerId});
            setIsSuccess(true);
        } catch (err) {
            console.error("Error requesting item:", err);
        }
    };

    return (
        <div className="min-h-screen flex items-center justify-center bg-gray-50 p-4">
            <motion.div
                className="bg-white p-8 rounded-2xl shadow-lg w-full max-w-md"
                initial={{scale: 0.9, opacity: 0}}
                animate={{scale: 1, opacity: 1}}
            >
                <h1 className="text-2xl font-bold mb-6 text-center text-gray-800">
                    Confirm Request
                </h1>
                <p className="text-gray-600 mb-6 text-center">
                    Are you sure you want to request this item ?
                </p>
                <div className="flex justify-center gap-4">
                    <button
                        onClick={() => navigate(-1)}
                        className="bg-gray-400 hover:bg-gray-500 text-white px-4 py-2 rounded-lg"
                    >
                        Cancel
                    </button>
                    <button
                        onClick={handleSubmit}
                        className="bg-indigo-600 hover:bg-indigo-700 text-white px-4 py-2 rounded-lg"
                    >
                        Request
                    </button>
                </div>
            </motion.div>
            <AnimatePresence>
                {isSuccess && (
                    <motion.div
                        className="fixed inset-0 flex items-center justify-center bg-black bg-opacity-50"
                        initial={{opacity: 0}}
                        animate={{opacity: 1}}
                        exit={{opacity: 0}}
                    >
                        <motion.div
                            className="bg-white p-8 rounded-2xl shadow-lg text-center w-full max-w-sm"
                            initial={{scale: 0.8}}
                            animate={{scale: 1}}
                            exit={{scale: 0.8}}
                        >
                            <h2 className="text-xl font-bold mb-4 text-green-700">
                                ✅ Request Successful!
                            </h2>
                            <p className="text-gray-600 mb-6">
                                Your borrow request has been submitted.
                            </p>
                            <button
                                onClick={() => navigate("/equipment-list")}
                                className="bg-indigo-600 hover:bg-indigo-700 text-white px-4 py-2 rounded-lg"
                            >
                                Go Back
                            </button>
                        </motion.div>
                    </motion.div>
                )}
            </AnimatePresence>
        </div>
    );
}
