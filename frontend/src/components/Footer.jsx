import React from "react";
import { Typography, Box, Container, Link, Grid } from "@mui/material";
import { useNavigate } from "react-router-dom";

/**
 * フッターコンポーネント
 * アプリケーションの下部に表示される共通フッター
 */
const Footer = () => {
  const navigate = useNavigate();
  const currentYear = new Date().getFullYear();

  // ナビゲーション処理
  const handleNavigation = (path) => {
    navigate(path);
  };

  return (
    <Box 
      component="footer"
      sx={{
        mt: 10,
        backgroundColor: "#1d2b59",
        color: "white", 
        py: 4,
        position: "relative",
        bottom: 0,
        width: "100%"
      }}
    >
      <Container maxWidth="lg">
        <Grid container spacing={4} justifyContent="space-between">
          {/* 左側：サイト情報 */}
          <Grid item xs={12} sm={4}>
            <Typography variant="h6" gutterBottom>
              Auction LIVE!
            </Typography>
            <Typography variant="body2">
              リアルタイムオークションプラットフォーム
            </Typography>
          </Grid>

          {/* 中央：リンク */}
          <Grid item xs={12} sm={4}>
            <Typography variant="h6" gutterBottom>
              リンク
            </Typography>
            <Link 
              component="button" 
              variant="body2" 
              onClick={() => handleNavigation("/")}
              color="inherit"
              sx={{ display: "block", mb: 1 }}
            >
              ホーム
            </Link>
            <Link 
              component="button" 
              variant="body2" 
              onClick={() => handleNavigation("/auctions")}
              color="inherit"
              sx={{ display: "block", mb: 1 }}
            >
              オークション
            </Link>
          </Grid>

          {/* 右側：コピーライト */}
          <Grid item xs={12} sm={4}>
            <Typography variant="body2" align="right">
              &copy; {currentYear} Auction LIVE! All rights reserved.
            </Typography>
          </Grid>
        </Grid>
      </Container>
    </Box>
  );
};

export default Footer;
