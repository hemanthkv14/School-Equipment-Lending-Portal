import apiClient from "./apiClient";

export const getAllEquipment = async () => {
    const response = await apiClient.get("/api/equipment");
    return response.data;
};

export const addEquipment = async (equipment) => {
  const response = await apiClient.post("/api/equipment/add", equipment);
  return response.data;
}

export const getAllCategories = async () => {
    const response = await apiClient.get("/api/categories");
    return response.data;
};

export const getAllItemDetails = async (equipmentId) => {
    const response = await apiClient.get(`/api/equipment/${equipmentId}/items`);
    return response.data;
  };

export const createBorrowRequest = async (requestData) => {
    const response = await apiClient.post("/api/borrowRequests/request", requestData);
    return response.data;
  };

export const getAllBorrowRequests = async () => {
  const response = await apiClient.get("/api/borrowRequests");
  return response.data;
};
