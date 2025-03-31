import React, { useState } from "react";
import {
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  TextField,
  Button,
  Typography,
  Box,
} from "@mui/material";
import { API_ENDPOINTS } from "../constants/api";

/**
 * ログインモーダルコンポーネント
 * @param {Object} props - コンポーネントのプロパティ
 * @param {boolean} props.open - モーダルを表示するかどうか
 * @param {Function} props.onClose - 閉じるときのコールバック
 * @param {Function} props.onLoginSuccess - ログイン成功時のコールバック
 */
const LoginModal = ({ open, onClose, onLoginSuccess }) => {
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);

  const handleLogin = async () => {
    // 入力検証
    if (!email || !password) {
      setError("メールアドレスとパスワードを入力してください");
      return;
    }

    setLoading(true);
    setError("");

    try {
      // API通信
      const response = await fetch(`${API_ENDPOINTS.AUTH}/login`, {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify({ email, password }),
      });

      if (!response.ok) {
        const errorData = await response.json();
        throw new Error(errorData.message || "ログインに失敗しました");
      }

      const data = await response.json();
      
      // 成功時の処理
      localStorage.setItem("token", data.token);
      
      // ログイン成功コールバックを呼び出し
      if (onLoginSuccess) {
        onLoginSuccess();
      }
      
      // モーダルを閉じる
      onClose();
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };

  const handleKeyPress = (e) => {
    if (e.key === "Enter") {
      handleLogin();
    }
  };

  return (
    <Dialog open={open} onClose={onClose} maxWidth="sm" fullWidth>
      <DialogTitle>ログイン</DialogTitle>
      <DialogContent>
        <Box sx={{ mt: 2 }}>
          {error && (
            <Typography color="error" variant="body2" sx={{ mb: 2 }}>
              {error}
            </Typography>
          )}
          <TextField
            label="メールアドレス"
            type="email"
            fullWidth
            margin="normal"
            value={email}
            onChange={(e) => setEmail(e.target.value)}
            onKeyPress={handleKeyPress}
            disabled={loading}
          />
          <TextField
            label="パスワード"
            type="password"
            fullWidth
            margin="normal"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            onKeyPress={handleKeyPress}
            disabled={loading}
          />
        </Box>
      </DialogContent>
      <DialogActions>
        <Button onClick={onClose} disabled={loading}>
          キャンセル
        </Button>
        <Button
          onClick={handleLogin}
          variant="contained"
          color="primary"
          disabled={loading}
        >
          {loading ? "ログイン中..." : "ログイン"}
        </Button>
      </DialogActions>
    </Dialog>
  );
};

export default LoginModal;
