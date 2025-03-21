import React from 'react';
import { 
  Container, 
  Box, 
  TextField, 
  Button, 
  Typography, 
  Snackbar, 
  Alert 
} from '@mui/material';
import { useAuth } from '../hooks/useAuth';
import Header from '../components/Header';
import Footer from '../components/Footer';

function PageLogin() {
    // フックから状態と関数を取得
    const { 
        email, setEmail, 
        password, setPassword, 
        handleLogin, 
        alertOpen, setAlertOpen, 
        alertMessage, alertSeverity 
    } = useAuth();

    return (
        <>
            <Header />
            <Container maxWidth="xs" sx={{ mt: 8 }}>
                <Box display="flex" flexDirection="column" alignItems="center">
                    <Typography variant="h5" component="h1" gutterBottom>
                        ログイン
                    </Typography>

                    <TextField
                        label="メールアドレス"
                        variant="outlined"
                        margin="normal"
                        fullWidth
                        value={email}
                        onChange={(e) => setEmail(e.target.value)}
                    />

                    <TextField
                        label="パスワード"
                        variant="outlined"
                        type="password"
                        margin="normal"
                        fullWidth
                        value={password}
                        onChange={(e) => setPassword(e.target.value)}
                    />

                    <Button 
                        variant="contained" 
                        color="primary" 
                        onClick={handleLogin}
                        sx={{ mt: 2 }}
                        fullWidth
                    >
                        ログイン
                    </Button>
                </Box>

                <Snackbar 
                    open={alertOpen} 
                    autoHideDuration={2000} 
                    onClose={() => setAlertOpen(false)}
                    anchorOrigin={{ vertical: 'top', horizontal: 'center' }}
                >
                    <Alert onClose={() => setAlertOpen(false)} severity={alertSeverity}>
                        {alertMessage}
                    </Alert>
                </Snackbar>
            </Container>
            <Footer />
        </>
    );
}

export default PageLogin;
