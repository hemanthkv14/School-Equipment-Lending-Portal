import apiClient from "./apiClient";

export const createBorrowRequest = async (borrowRequest) => {
    const response = await apiClient.post("/api/borrowRequest/create", borrowRequest);
    return response.data;
};