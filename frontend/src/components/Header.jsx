import React, { useState } from "react";
import { 
  AppBar, 
  Toolbar, 
  Typography, 
  Button, 
  Box, 
  Dialog, 
  DialogTitle, 
  DialogContent, 
  DialogActions,
  useMediaQuery,
  IconButton,
  Menu,
  MenuItem,
  useTheme
} from "@mui/material";
import { useNavigate } from "react-router-dom";
import MenuIcon from '@mui/icons-material/Menu';

/**
 * ヘッダーコンポーネント
 * アプリケーションの上部に表示されるナビゲーションバー
 */
const Header = () => {
  // ナビゲーションと状態管理
  const navigate = useNavigate();
  const theme = useTheme();
  const isMobile = useMediaQuery(theme.breakpoints.down('sm'));
  const [logoutDialogOpen, setLogoutDialogOpen] = useState(false);
  const [mobileMenuAnchor, setMobileMenuAnchor] = useState(null);

  // モバイルメニューの状態
  const isMobileMenuOpen = Boolean(mobileMenuAnchor);
  
  // モバイルメニューを開く
  const handleMobileMenuOpen = (event) => {
    setMobileMenuAnchor(event.currentTarget);
  };

  // モバイルメニューを閉じる
  const handleMobileMenuClose = () => {
    setMobileMenuAnchor(null);
  };

  // ログアウト処理
  const handleLogout = () => {
    localStorage.removeItem("token"); // JWT削除
    setLogoutDialogOpen(true); // ダイアログを開く
    handleMobileMenuClose(); // モバイルメニューを閉じる
  };

  // ログアウトダイアログを閉じる
  const handleCloseDialog = () => {
    setLogoutDialogOpen(false);
    navigate("/login"); // ログインページへ遷移
  };

  // ナビゲーション処理
  const handleNavigation = (path) => {
    navigate(path);
    handleMobileMenuClose(); // モバイルメニューを閉じる
  };

  return (
    <>
      <AppBar position="static" sx={{ backgroundColor: "white", color: "#666" }}>
        <Toolbar>
          {/* 左側にロゴを配置 */}
          <Typography variant="h6" sx={{ flexGrow: 1 }}>
            <Box
              component="img"
              src="/img/auction-live-logo.png"
              alt="AuctionLIVE!"
              sx={{
                height: 40,
                cursor: "pointer"
              }}
              onClick={() => handleNavigation("/")}
            />
          </Typography>

          {/* デスクトップ用ナビゲーションボタン */}
          {!isMobile ? (
            <Box>
              <Button color="inherit" onClick={() => handleNavigation("/")}>ホーム</Button>
              <Button color="inherit" onClick={() => handleNavigation("/users")}>ユーザー管理</Button>
              <Button color="inherit" onClick={() => handleNavigation("/auctions")}>オークション</Button>
              <Button color="inherit" onClick={handleLogout}>ログアウト</Button>
            </Box>
          ) : (
            // モバイル用メニューボタン
            <IconButton
              edge="end"
              color="inherit"
              aria-label="menu"
              onClick={handleMobileMenuOpen}
            >
              <MenuIcon />
            </IconButton>
          )}
        </Toolbar>
      </AppBar>

      {/* モバイル用メニュー */}
      <Menu
        anchorEl={mobileMenuAnchor}
        open={isMobileMenuOpen}
        onClose={handleMobileMenuClose}
      >
        <MenuItem onClick={() => handleNavigation("/")}>ホーム</MenuItem>
        <MenuItem onClick={() => handleNavigation("/users")}>ユーザー管理</MenuItem>
        <MenuItem onClick={() => handleNavigation("/auctions")}>オークション</MenuItem>
        <MenuItem onClick={handleLogout}>ログアウト</MenuItem>
      </Menu>

      {/* ログアウトダイアログ */}
      <Dialog open={logoutDialogOpen} onClose={handleCloseDialog}>
        <DialogTitle>ログアウト</DialogTitle>
        <DialogContent>
          <Typography>ログアウトしました。</Typography>
        </DialogContent>
        <DialogActions>
          <Button onClick={handleCloseDialog} color="primary" variant="contained">
            OK
          </Button>
        </DialogActions>
      </Dialog>
    </>
  );
};

export default Header;
