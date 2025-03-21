import React, { useState } from "react";
import { Button, TextField } from "@mui/material";

const BidButton = ({ monitor }) => {
  const [bidUserId, setBidUserId] = useState("hmn1226");
  const API_BASE_URL = "http://localhost:8080/api/live-bid";

  const onBidButtonClick = async () => {
    try {
      const token = localStorage.getItem("token");
      const response = await fetch(
        `${API_BASE_URL}/${monitor.auctionRoomId}/${monitor.auctionLaneId}/${monitor.auctionEntryId}`,
        {
          method: "POST",
          headers: {
            Authorization: `Bearer ${token}`,
            "Content-Type": "application/json",
          },
          body: JSON.stringify({ bidUserId }),
        }
      );

      if (!response.ok) {
        if (response.status === 401 || response.status === 403) {
          throw new Error("認証エラー");
        }
        throw new Error(`エラー: ${response.status}`);
      }

      const json = await response.json();
      console.log("入札成功", json);
    } catch (error) {
      console.error("入札エラー:", error.message);
    }
  };

  return (
    <span>
      <TextField
        value={bidUserId}
        onChange={(e) => setBidUserId(e.target.value)}
        style={{ width: "140px", fontWeight: "bold" }}
        variant="outlined"
        size="small"
      />
      <Button variant="outlined" color="secondary" onClick={onBidButtonClick}>
        入札する
      </Button>
    </span>
  );
};

export default BidButton;