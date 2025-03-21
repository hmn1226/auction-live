import React, { useState, useEffect } from "react";
import { useEmailVerification } from "../hooks/useEmailVerification";
import { Container, Typography, Button, Box , Snackbar, Alert} from "@mui/material";
import { default as OtpInput } from "react-otp-input";
import { useParams } from "react-router-dom";
import Header from "../components/Header";
import Footer from "../components/Footer";

const PageEmailVerification = () => {
    const [error, setError] = useState("");
    const {
        verificationToken,
        setVerificationToken,
        verificationCode,
        setVerificationCode,
        verifyCode,
        alertOpen,
        setAlertOpen,
        alertMessage,
        alertSeverity,
      } = useEmailVerification();
    const { token } = useParams(); // URLからトークンを取得

    // URLのtokenをセット
    useEffect(() => {
        if (token) {
            setVerificationToken(token);
        } else {
            setError("認証トークンが見つかりません。URLを確認してください。");
        }
    }, [token, setVerificationToken]);

    // 認証コード変更時の処理
    const handleChange = (otp) => {
        setVerificationCode(otp);
        if (otp.length === 6) {
            setError(""); // 6桁入力時にエラーをクリア
        }
    };

    // 認証リクエストの送信
    const handleSubmit = () => {
        if (!verificationToken) {
            setError("認証トークンが設定されていません。");
            return;
        }

        if (verificationCode.length !== 6) {
            setError("6桁の認証コードを入力してください");
            return;
        }

        verifyCode();
    };

    return (
        <>
            <Header />
            <Container maxWidth="xs" sx={{ mt: 8, textAlign: "center" }}>
                <Typography variant="h5" gutterBottom>
                    認証コード入力
                </Typography>
                <Typography variant="body2" sx={{ mb: 2 }}>
                    メールに送信された6桁の認証コードを入力してください
                </Typography>

                {/* 認証コード入力 */}
                <Box sx={{ display: "flex", justifyContent: "center", mb: 2 }}>
                <OtpInput
                    value={verificationCode}
                    onChange={handleChange}
                    numInputs={6} // 6マス
                    renderInput={(props) => <input {...props} type="tel" />} // 数字入力用
                    shouldAutoFocus // 自動フォーカス
                    inputStyle={{
                        width: "3rem",
                        height: "3rem",
                        margin: "0 0.5rem",
                        fontSize: "1.5rem",
                        textAlign: "center",
                        border: "1px solid #ccc",
                        borderRadius: "5px",
                        outline: "none"
                    }}
                    focusStyle={{
                        border: "2px solid #1976d2"
                    }}
                />
                </Box>

                {error && <Typography color="error">{error}</Typography>}

                {/* 認証ボタン */}
                <Button
                    variant="contained"
                    color="primary"
                    fullWidth
                    onClick={handleSubmit}
                    sx={{ mt: 2 }}
                >
                    認証する
                </Button>
            </Container>
             {/* Snackbar */}
            <Snackbar
                open={alertOpen}
                autoHideDuration={6000}
                onClose={() => setAlertOpen(false)}
                anchorOrigin={{ vertical: "bottom", horizontal: "left" }} // 左下に表示
            >
                <Alert
                onClose={() => setAlertOpen(false)}
                severity={alertSeverity}
                sx={{ width: "100%" }}
                >
                {alertMessage}
                </Alert>
            </Snackbar>

            <Footer />
        </>
    );
};

export default PageEmailVerification;