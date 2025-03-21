import React from "react";
import { Button } from "@mui/material";

const StopButton = ({ monitor }) => {
  const onStopButtonClick = async () => {
    try {
      const token = localStorage.getItem("token");

      const response = await fetch(
        `http://localhost:8080/api/auction-lane/${monitor.auctionRoomId}/${monitor.auctionLaneId}/status`,
        {
          method: "POST",
          headers: {
            Authorization: `Bearer ${token}`,
            "Content-Type": "application/json",
          },
          body: JSON.stringify({ auctionLaneStatus: "STOP" }),
        }
      );

      if (!response.ok) {
        if (response.status === 401 || response.status === 403) {
          throw new Error("認証エラー");
        }
        throw new Error(`エラー: ${response.status}`);
      }

      const json = await response.json();
      console.log("成功:", json);
    } catch (error) {
      console.error("エラー:", error.message);
    }
  };

  return (
    <Button variant="outlined" color="secondary" onClick={onStopButtonClick}>
      ストップ
    </Button>
  );
};

export default StopButton;