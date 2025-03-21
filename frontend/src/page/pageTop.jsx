import * as React from "react";
import { useNavigate } from "react-router-dom";
import { Button, Container, Box, Typography } from "@mui/material";
import Header from "../components/Header";
import Footer from "../components/Footer";

function PageTop() {

  const API_BASE_URL = "http://localhost:8080/api/health-check";

  const navigate = useNavigate();
  const token = localStorage.getItem('token');
  console.log(token);
  
  React.useEffect(() => {
    fetch(API_BASE_URL, {
      method: "POST",
      credentials: "include",
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${token}`
      }
    })
      .then((response) => {
        if (response.status === 403) {
          navigate("/pageLogin");
        }
      })
      .catch((error) => {
        console.error("Health check error: ", error);
      });
  }, [navigate]);

  const changePage = (path) => {
    navigate(path);
  };

  return (
    <>
      <Header />
      <Container maxWidth="md" sx={{ textAlign: "center", mt: 5 }}>
        <Typography variant="h4" component="h1" gutterBottom>
          TOPページ
        </Typography>
        <Box component="ul" sx={{ listStyleType: "none", p: 0, m: 0 }}>
          {[
            { label: "ログイン", path: "/login" },
            { label: "ランディング", path: "/landing" },
            { label: "オークション一覧", path: "/auctions" },
            { label: "ユーザー一覧", path: "/users" },
            { label: "WebSocket", path: "/websocket" },
            { label: "サインアップ", path: "/signup" },
            { label: "VERIFICATION", path: "/emailVerification" },
          ].map((item, index) => (
            <Box component="li" key={index} sx={{ my: 1 }}>
              <Button
                variant="contained"
                color="primary"
                onClick={() => changePage(item.path)}
                sx={{ width: "200px" }}
              >
                {item.label}
              </Button>
            </Box>
          ))}
        </Box>
      </Container>
      <Footer />
    </>
  );
}

export default PageTop;