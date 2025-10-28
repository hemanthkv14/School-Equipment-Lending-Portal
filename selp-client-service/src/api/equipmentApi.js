import apiClient from "./apiClient";

export const getAllEquipment = async () => {
    const response = await apiClient.get("/api/equipment/getAll");
    return response.data;
};

export const addEquipment = async (equipment) => {
  const response = await apiClient.post("/api/equipment/add", equipment);
  return response.data;
}