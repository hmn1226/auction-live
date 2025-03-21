import * as React from "react";
import { 
    Container, 
    TextField, 
    Button, 
    Typography, 
    Box, 
    Snackbar, 
    Alert,
    CircularProgress,
    Link
} from "@mui/material";
import { useSignup } from "../hooks/useSignup";
import { Link as RouterLink } from "react-router-dom";
import Header from "../components/Header";
import Footer from "../components/Footer";

function PageSignup() {
    // フックから状態と関数を取得
    const {
        email, setEmail,
        password, setPassword,
        confirmPassword, setConfirmPassword,
        nickname, setNickname,
        handleSignup,
        alertOpen, setAlertOpen,
        alertMessage, alertSeverity,
        isLoading
    } = useSignup();

    return (
        <>
            <Header />
            <Container maxWidth="xs" sx={{ mt: 8 }}>
                {/* フォームタイトル */}
                <Box display="flex" flexDirection="column" alignItems="center">
                    <Typography variant="h5" gutterBottom>
                        新規登録
                    </Typography>

                    {/* 入力フォーム */}
                    <TextField
                        label="メールアドレス"
                        variant="outlined"
                        margin="normal"
                        fullWidth
                        value={email}
                        onChange={(e) => setEmail(e.target.value)}
                        required
                        type="email"
                    />
                    <TextField
                        label="ニックネーム"
                        variant="outlined"
                        margin="normal"
                        fullWidth
                        value={nickname}
                        onChange={(e) => setNickname(e.target.value)}
                        required
                    />
                    <TextField
                        label="パスワード"
                        variant="outlined"
                        type="password"
                        margin="normal"
                        fullWidth
                        value={password}
                        onChange={(e) => setPassword(e.target.value)}
                        required
                    />
                    <TextField
                        label="パスワード確認"
                        variant="outlined"
                        type="password"
                        margin="normal"
                        fullWidth
                        value={confirmPassword}
                        onChange={(e) => setConfirmPassword(e.target.value)}
                        required
                        error={password !== confirmPassword && confirmPassword !== ""}
                        helperText={
                            password !== confirmPassword && confirmPassword !== "" 
                                ? "パスワードが一致しません" 
                                : ""
                        }
                    />

                    {/* 登録ボタン */}
                    <Button 
                        variant="contained" 
                        color="primary" 
                        onClick={handleSignup}
                        sx={{ mt: 2 }}
                        fullWidth
                        disabled={isLoading}
                    >
                        {isLoading ? <CircularProgress size={24} /> : "登録"}
                    </Button>

                    {/* ログインページへのリンク */}
                    <Box sx={{ mt: 2, textAlign: 'center' }}>
                        <Typography variant="body2">
                            すでにアカウントをお持ちの方は
                            <Link component={RouterLink} to="/login">
                                こちら
                            </Link>
                        </Typography>
                    </Box>
                </Box>

                {/* アラート表示 */}
                <Snackbar 
                    open={alertOpen} 
                    autoHideDuration={3000} 
                    onClose={() => setAlertOpen(false)}
                    anchorOrigin={{ vertical: 'top', horizontal: 'center' }}
                >
                    <Alert severity={alertSeverity} onClose={() => setAlertOpen(false)}>
                        {alertMessage}
                    </Alert>
                </Snackbar>
            </Container>
            <Footer/>
        </>
    );
}

export default PageSignup;
