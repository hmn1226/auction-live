// API URLの定数
const BASE_URL = "http://localhost:8080";

export const API_ENDPOINTS = {
  AUTH: `${BASE_URL}/api/auth`,
  USERS: `${BASE_URL}/api/users`,
  AUCTIONS: `${BASE_URL}/api/auctions`,
  LIVE_BID: `${BASE_URL}/api/live-bid`,
  HEALTH_CHECK: `${BASE_URL}/api/health-check`,
  LANDING: `${BASE_URL}/api`,
};

export default API_ENDPOINTS;
