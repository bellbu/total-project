import axios from 'axios';

const api = axios.create({
    withCredentials: true, // HttpOnly 쿠키 포함
});

export default api;