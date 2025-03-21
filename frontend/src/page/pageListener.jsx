import React, { useState, useEffect } from "react";
import { useListener } from "../hooks/useListener";
import { useParams } from "react-router-dom";
import { Container, Typography, Box, CircularProgress, Paper, Grid, Divider } from "@mui/material";
import Header from "../components/Header";
import Footer from "../components/Footer";
import AuctionEntryItem from "../components/AuctionEntryItem";
import Notification from "../components/Notification";
import { API_ENDPOINTS } from "../constants/api";
import { formatDateTime } from "../utils/formatters";

export default function PageListener() {
  const { auction, loading, error } = useListener();
  const { auctionRoomId } = useParams();
  
  // ユーザーIDは後でユーザー設定から取得するように変更すべき
  const [bidUserId, setBidUserId] = useState(() => {
    // ローカルストレージから取得を試みる
    return localStorage.getItem("bidUserId") || "hmn1226";
  });
  
  // 入札価格の状態
  const [bidPrices, setBidPrices] = useState({});
  
  // 通知の状態
  const [notification, setNotification] = useState({
    open: false,
    message: "",
    severity: "info"
  });

  // auction が利用可能になったときに bidPrices を初期化する
  useEffect(() => {
    if (auction) {
      const initialPrices = {};
      auction.publicEntries.forEach((entry) => {
        initialPrices[entry.entryId] = entry.currentPrice;
      });
      setBidPrices(initialPrices);
    }
  }, [auction]);

  // 通知を閉じる
  const handleCloseNotification = () => {
    setNotification(prev => ({ ...prev, open: false }));
  };

  // 通知を表示
  const showNotification = (message, severity = "info") => {
    setNotification({
      open: true,
      message,
      severity
    });
  };

  // TextField の値変更をハンドリング
  const handleBidPriceChange = (entryId, value) => {
    setBidPrices((prev) => ({
      ...prev,
      [entryId]: value,
    }));
  };

  // 入札ボタン押下時に TextField の値を POST する
  const onBidButtonClick = async (entry) => {
    try {
      const token = localStorage.getItem("token");
      if (!token) {
        showNotification("認証情報がありません。再ログインしてください。", "error");
        return;
      }
      
      // 送信前にカンマを除去して数値として扱う
      const bidPrice = bidPrices[entry.entryId].toString().replace(/,/g, "");
      
      const response = await fetch(
        `${API_ENDPOINTS.LIVE_BID}/${entry.auctionRoomId}/${entry.auctionLaneId}/${entry.entryId}`,
        {
          method: "POST",
          headers: {
            Authorization: `Bearer ${token}`,
            "Content-Type": "application/json",
          },
          body: JSON.stringify({ bidUserId, bidPrice }),
        }
      );

      if (!response.ok) {
        if (response.status === 401 || response.status === 403) {
          showNotification("認証エラーが発生しました。再ログインしてください。", "error");
          return;
        }
        throw new Error(`エラー: ${response.status}`);
      }

      const json = await response.json();
      console.log("入札成功", json);
      showNotification(`${entry.entryName}に${bidPrice}円で入札しました`, "success");
    } catch (error) {
      console.error("入札エラー:", error.message);
      showNotification(`入札エラー: ${error.message}`, "error");
    }
  };

  // ローディング中の表示
  if (loading) {
    return (
      <>
        <Header />
        <Container sx={{ textAlign: "center", py: 8 }}>
          <CircularProgress />
          <Typography variant="h6" sx={{ mt: 2 }}>
            オークション情報を読み込み中...
          </Typography>
        </Container>
        <Footer />
      </>
    );
  }

  // エラー時の表示
  if (error) {
    return (
      <>
        <Header />
        <Container sx={{ textAlign: "center", py: 8 }}>
          <Typography variant="h6" color="error">
            エラーが発生しました: {error}
          </Typography>
        </Container>
        <Footer />
      </>
    );
  }

  // auction が取得できていなければローディング表示
  if (!auction) {
    return (
      <>
        <Header />
        <Container sx={{ textAlign: "center", py: 8 }}>
          <Typography variant="h6">
            オークション情報が見つかりません
          </Typography>
        </Container>
        <Footer />
      </>
    );
  }

  return (
    <>
      <Header />
      <Container maxWidth="lg" sx={{ py: 4 }}>
        <Paper elevation={2} sx={{ p: 3, mb: 4 }}>
          <Typography variant="h4" component="h1" gutterBottom>
            オークションルーム: {auctionRoomId}
          </Typography>
          
          <Grid container spacing={2} sx={{ mt: 2 }}>
            <Grid item xs={12} md={6}>
              <Box>
                <Typography variant="body1">
                  <strong>ライバーID:</strong> {auction.liverUlid}
                </Typography>
                <Typography variant="body1">
                  <strong>開始日時:</strong> {formatDateTime(auction.startDatetime)}
                </Typography>
                <Typography variant="body1">
                  <strong>終了日時:</strong> {auction.endDatetime ? formatDateTime(auction.endDatetime) : "未定"}
                </Typography>
              </Box>
            </Grid>
            <Grid item xs={12} md={6}>
              <Box>
                <Typography variant="body1">
                  <strong>オークション方式:</strong> {auction.auctionType}
                </Typography>
                <Typography variant="body1">
                  <strong>自動進行:</strong> {auction.autoPilotMode ? "有効" : "無効"}
                </Typography>
                <Typography variant="body1">
                  <strong>公開範囲:</strong> {auction.publicMode}
                </Typography>
                <Typography variant="body1">
                  <strong>レーン数:</strong> {auction.laneAmount}
                </Typography>
              </Box>
            </Grid>
          </Grid>
        </Paper>

        <Typography variant="h5" component="h2" gutterBottom>
          出品アイテム
        </Typography>
        
        {auction.publicEntries.length > 0 ? (
          <ul style={{ listStyleType: "none", padding: 0 }}>
            {auction.publicEntries.map((entry) => (
              <AuctionEntryItem
                key={entry.entryId}
                entry={entry}
                bidPrice={bidPrices[entry.entryId]}
                onBidPriceChange={handleBidPriceChange}
                onBidButtonClick={onBidButtonClick}
              />
            ))}
          </ul>
        ) : (
          <Paper sx={{ p: 3, textAlign: "center" }}>
            <Typography variant="body1">出品アイテムはありません</Typography>
          </Paper>
        )}
      </Container>
      
      <Notification
        open={notification.open}
        message={notification.message}
        severity={notification.severity}
        onClose={handleCloseNotification}
      />
      
      <Footer />
    </>
  );
}
