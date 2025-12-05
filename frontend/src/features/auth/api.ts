import axios from "axios";
const client = axios.create({ baseURL: "http://localhost:8081", withCredentials: true });
export async function getProfile(id: string) {
  const res = await client.get(`/api/auth/profile/${id}`);
  return res.data;
}
export async function updateProfile(id: string, data: any) {
  const res = await client.put(`/api/auth/profile/${id}`, data);
  return res.data;
}

