import React from "react";
import { TextField, Button } from "@mui/material";
import { formatNumberWithCommas } from "../utils/formatters";

// オークションエントリーアイテムコンポーネント
const AuctionEntryItem = ({
  entry,
  bidPrice,
  onBidPriceChange,
  onBidButtonClick,
}) => {
  return (
    <li
      style={{
        border: "1px solid #ddd",
        borderRadius: "8px",
        padding: "15px",
        marginBottom: "15px",
        boxShadow: "0 2px 4px rgba(0,0,0,0.1)",
      }}
    >
      <div style={{ marginBottom: "10px" }}>
        <h3 style={{ margin: "0 0 10px 0" }}>{entry.entryName}</h3>
        <div style={{ display: "flex", flexWrap: "wrap", gap: "10px" }}>
          <InfoItem label="スタート価格" value={entry.startPrice} />
          <InfoItem label="数量" value={entry.quantity} />
          <InfoItem label="販売方法" value={entry.bulkSaleMode} />
        </div>
        {entry.entryDescription && (
          <p style={{ margin: "10px 0" }}>
            <strong>説明:</strong> {entry.entryDescription}
          </p>
        )}
      </div>

      <div
        style={{
          display: "flex",
          alignItems: "center",
          gap: "10px",
          marginTop: "15px",
          padding: "10px",
          backgroundColor: "#f5f5f5",
          borderRadius: "4px",
        }}
      >
        <span style={{ fontWeight: "bold" }}>現在価格:</span>
        <TextField
          value={formatNumberWithCommas(bidPrice)}
          onChange={(e) => {
            // 入力値からカンマを除去して状態に保存
            const rawValue = e.target.value.replace(/,/g, "");
            onBidPriceChange(entry.entryId, rawValue);
          }}
          // 入力値を右寄せ
          inputProps={{ style: { textAlign: "right" } }}
          style={{ width: "140px" }}
          variant="outlined"
          size="small"
          // type を text に変更（カンマ区切りを表示するため）
          type="text"
        />
        <Button
          variant="contained"
          color="primary"
          onClick={() => onBidButtonClick(entry)}
        >
          入札する
        </Button>
      </div>

      {entry.images && entry.images.length > 0 && (
        <div style={{ marginTop: "15px" }}>
          <div
            style={{
              display: "flex",
              flexWrap: "wrap",
              gap: "10px",
            }}
          >
            {entry.images.map((image, imgIndex) => (
              <img
                key={imgIndex}
                src={image.data}
                alt={`${entry.entryName} 画像 ${imgIndex + 1}`}
                style={{
                  width: "100px",
                  height: "100px",
                  objectFit: "cover",
                  borderRadius: "4px",
                }}
              />
            ))}
          </div>
        </div>
      )}
    </li>
  );
};

// 情報項目を表示するヘルパーコンポーネント
const InfoItem = ({ label, value }) => (
  <span style={{ marginRight: "15px" }}>
    <strong>{label}:</strong> {value}
  </span>
);

export default AuctionEntryItem;
