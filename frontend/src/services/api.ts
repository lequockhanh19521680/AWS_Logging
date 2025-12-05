import axios from "axios";

const client = axios.create({
  baseURL: "http://localhost:8081",
  withCredentials: true,
});

export async function getQuestions() {
  const res = await client.get("/api/questions");
  return res.data;
}
