import axios from "axios";

const API_URL = "/api/auth/";

export async function loginUser(username, password) {
    const response = await axios.post(`${API_URL}login`, { username, password });
    return response.data;
}
