import React, { useState } from "react";
import { Button, TextField } from "@mui/material";

const CurrentPriceButton = ({ monitor }) => {
  const [currentPrice, setCurrentPrice] = useState(1000);

  const onCurrentPriceButtonClick = async () => {
    try {
      const token = localStorage.getItem("token");

      const response = await fetch(
        `http://localhost:8080/api/auction-lane/${monitor.auctionRoomId}/${monitor.auctionLaneId}/current-price`,
        {
          method: "POST",
          headers: {
            Authorization: `Bearer ${token}`,
            "Content-Type": "application/json",
          },
          body: JSON.stringify({ currentPrice: currentPrice }),
        }
      );

      if (!response.ok) {
        if (response.status === 401 || response.status === 403) {
          throw new Error("認証エラー");
        }
        throw new Error(`エラー: ${response.status}`);
      }

      console.log("現在価格が更新されました:", currentPrice);
    } catch (error) {
      console.error("エラー:", error.message);
    }
  };

  return (
    <span>
      <TextField
        type="number"
        value={currentPrice}
        onChange={(e) => setCurrentPrice(Number(e.target.value))}
        sx={{
          width: "120px",
          "& input": { textAlign: "right", fontWeight: "bold" },
        }}
        variant="outlined"
        size="small"
      />
      <Button variant="outlined" onClick={onCurrentPriceButtonClick}>
        現在価格変更
      </Button>
    </span>
  );
};

export default CurrentPriceButton;