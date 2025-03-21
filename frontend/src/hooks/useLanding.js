import { useState } from "react";

const API_BASE_URL = "http://localhost:8080/api";

export function useLanding() {
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const [showLoginModal, setShowLoginModal] = useState(false);
  const token = localStorage.getItem('token');

  // auctionRoomIdを生成するエンドポイントに問い合わせる
  const generateAuctionRoomId = async () => {
    try {
      const token = localStorage.getItem('token');
      const response = await fetch(`${API_BASE_URL}/auctions/auction-room-id/generate`, {
        method: "GET",
        headers: {
          "Authorization": `Bearer ${token}`,
        },
      });

      if (response.status === 200) {
        const json = await response.json();
        return json.auctionRoomId;
      } else if (response.status === 401 || response.status === 403) {
        // 認証エラーの場合
        throw new Error("認証エラー");
      } else {
        throw new Error(`auctionRoomId生成失敗: ${response.status}`);
      }
    } catch (err) {
      throw err;
    }
  };

  // generateしてからPUTする一連の処理
  const generateAndStartAuction = async (auctionData) => {
    setLoading(true);
    setError(null);
    setShowLoginModal(false); // 一旦リセット

    const token = localStorage.getItem('token');

    console.log(token);

    try {
      const auctionRoomId = await generateAuctionRoomId();
      const response = await fetch(`${API_BASE_URL}/auctions/${auctionRoomId}`, {
        method: "PUT",
        headers: {
          "Content-Type": "application/json",
          "Authorization": `Bearer ${token}`,
        },
        body: JSON.stringify(auctionData),
      });

      if (!response.ok) {
        throw new Error(`オークション開始に失敗しました: ${response.status}`);
      }

      return await response.json();
    } catch (err) {
      if (err.message === "認証エラー") {
        // 403または401の場合はログインモーダルを開く
        setShowLoginModal(true);
      }
      setError(err.message);
      return null;
    } finally {
      setLoading(false);
    }
  };

  return {generateAndStartAuction, loading, error, showLoginModal, setShowLoginModal };
}