import React from "react";
import { Alert, Snackbar } from "@mui/material";

/**
 * 通知コンポーネント
 * @param {Object} props - コンポーネントのプロパティ
 * @param {boolean} props.open - 通知を表示するかどうか
 * @param {string} props.message - 通知メッセージ
 * @param {string} props.severity - 通知の種類 (success, error, warning, info)
 * @param {Function} props.onClose - 閉じるときのコールバック
 * @param {number} props.autoHideDuration - 自動で閉じるまでの時間（ミリ秒）
 */
const Notification = ({
  open,
  message,
  severity = "info",
  onClose,
  autoHideDuration = 6000,
}) => {
  return (
    <Snackbar
      open={open}
      autoHideDuration={autoHideDuration}
      onClose={onClose}
      anchorOrigin={{ vertical: "top", horizontal: "center" }}
    >
      <Alert onClose={onClose} severity={severity} sx={{ width: "100%" }}>
        {message}
      </Alert>
    </Snackbar>
  );
};

export default Notification;
