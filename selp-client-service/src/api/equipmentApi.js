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

export const updateEquipmentItem = async (updateData) => {
    const response = await apiClient.put("/api/equipment/items/update", updateData);
    return response.data;
  };

export const  updateEquipment = async (updateData) => {
  const response = await apiClient.put("/api/equipment/update", updateData);
  return response.data;
};

export const deleteIndividualItem = async (itemId) => {
  const response = await apiClient.delete(`/api/equipment/items/${itemId}`);
  return response.data;
};

export const deleteAllItems = async (itemIds) => {
  const response = await apiClient.delete(`/api/equipment/deleteAllItems`, {
    data: JSON.stringify(itemIds),
  });
  return response.data;
};

export const deleteEquipment = async (equipmentId) => {
  const response = await apiClient.delete(`/api/equipment/delete/${equipmentId}`);
  return response.data;
};

export const notificationApi = async (userId) => {
  const response = await apiClient.get(`/api/notifications/dueDetails/${userId}`);
  return response.data;
};

export const addNewCategory = async (data) => {
  const response = await apiClient.post("/api/categories/add",data);
  return response.data;
};

export const addNewEquipment = async (data) => {
  const response = await apiClient.post("/api/equipment/add",data);
  return response.data;
};

export const cancelRequest = async (lendingRequestId) => {
  const response = await apiClient.post(`/api/borrowRequests/revoke/${ lendingRequestId}`);
  return response.data;
}

export const returnItem = async (data) => {
    const { lendingId, borrowerId } = data;
    const response = await apiClient.post(`/api/borrowRequests/return/${lendingId}/${borrowerId}`);
    return response.data;
}

export const getAllLendings = async () => {
  const response = await apiClient.get("/api/borrowRequests");
  return response.data;
};

export const getAllLendingsOfUser = async (userId) => {
  const response = await apiClient.get(`/api/borrowRequests/${userId}`);
  return response.data;
};

export const approveLoan = async (adminId, lendingId, dueDate) => {
  const response = await apiClient.post(`/api/borrowRequests/approve/${lendingId}/user/${adminId}`, null, {
    params: { dueDate },
  });
  return response.data;
};

export const acceptReturnItem = async (lendingId, condition) => {
  const response = await apiClient.post(`/api/borrowRequests/acceptReturn/${lendingId}`, null, {
    params: { condition },
  });
  return response.data;
};

export const rejectItem = async (adminId, lendingId) => {
  const response = await apiClient.post(`/api/borrowRequests/reject/${lendingId}/user/${adminId}`);
  return response.data;
};