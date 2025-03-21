import { useState, useEffect } from "react";
import { useNavigate, useParams } from "react-router-dom";
import { API_ENDPOINTS } from "../constants/api";

export function useListener() {
  const navigate = useNavigate();
  const { auctionRoomId } = useParams();
  const [auction, setAuction] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    async function fetchAuction() {
      setLoading(true);
      setError(null);
      
      const token = localStorage.getItem("token");
      if (!auctionRoomId || !token) {
        setLoading(false);
        return;
      }

      try {
        const response = await fetch(`${API_ENDPOINTS.AUCTIONS}/${auctionRoomId}`, {
          method: "GET",
          headers: {
            "Content-Type": "application/json",
            "Authorization": `Bearer ${token}`
          }
        });

        // 認証エラーの場合はログインページへリダイレクト
        if (response.status === 401 || response.status === 403) {
          navigate('/login');
          return;
        }
        
        if (!response.ok) {
          throw new Error(`HTTP error! Status: ${response.status}`);
        }
        
        const data = await response.json();
        setAuction(data);
      } catch (error) {
        console.error("Failed to fetch auction:", error);
        setError(error.message || "オークション情報の取得に失敗しました");
      } finally {
        setLoading(false);
      }
    }

    fetchAuction();
  }, [auctionRoomId, navigate]);

  return { auction, loading, error };
}
