import * as React from 'react';
import { useNavigate } from 'react-router-dom';
import { 
    Button, 
    Typography, 
    Table, 
    TableBody, 
    TableCell, 
    TableContainer, 
    TableHead, 
    TableRow, 
    TextField,
    Paper, 
    Dialog, 
    DialogTitle, 
    DialogContent, 
    DialogActions,
    Box,
    Container,
    CircularProgress,
    Alert,
    Snackbar
} from "@mui/material";
import { useAuctions } from '../hooks/useAuctions';
import Header from '../components/Header';
import Footer from '../components/Footer';

function PageAuctions() {
    // ナビゲーションと状態管理
    const navigate = useNavigate();
    const { 
        auctions, 
        deleteAuction, 
        addAuction, 
        isLoading, 
        error, 
        setError 
    } = useAuctions(navigate);
    
    // ローカル状態
    const [open, setOpen] = React.useState(false);
    const [newAuction, setNewAuction] = React.useState({ 
        auctionRoomId: "", 
        liverUlid: "" 
    });

    // イベントハンドラ
    const handleOpen = () => setOpen(true);
    const handleClose = () => setOpen(false);
    const handleChange = (e) => {
        setNewAuction({ ...newAuction, [e.target.name]: e.target.value });
    };

    return (
        <>
            <Header />
            <Container maxWidth="md" sx={{ mt: 5 }}>
                {/* ページタイトル */}
                <Typography variant="h4" align="center" gutterBottom>
                    AUCTION一覧
                </Typography>
                
                {/* 新規追加ボタン */}
                <Box sx={{ textAlign: "center", mb: 2 }}>
                    <Button 
                        variant="contained" 
                        color="primary" 
                        onClick={handleOpen} 
                        sx={{ width: 200 }}
                        disabled={isLoading}
                    >
                        ➕ 新規追加
                    </Button>
                </Box>

                {/* ローディング表示 */}
                {isLoading && (
                    <Box sx={{ display: 'flex', justifyContent: 'center', my: 3 }}>
                        <CircularProgress />
                    </Box>
                )}

                {/* エラー表示 */}
                {error && (
                    <Alert severity="error" sx={{ mb: 2 }} onClose={() => setError(null)}>
                        {error}
                    </Alert>
                )}

                {/* オークション一覧テーブル */}
                <TableContainer component={Paper} sx={{ maxWidth: "100%", overflowX: "auto" }}>
                    <Table>
                        <TableHead>
                            <TableRow sx={{ backgroundColor: "#1976d2" }}>
                                <TableCell sx={{ color: "white", fontWeight: "bold" }}>オークションルームID</TableCell>
                                <TableCell sx={{ color: "white", fontWeight: "bold" }}>主催者ULID</TableCell>
                                <TableCell sx={{ color: "white", fontWeight: "bold", textAlign: "center" }}>操作</TableCell>
                            </TableRow>
                        </TableHead>

                        <TableBody>
                            {!isLoading && auctions.length > 0 ? (
                                auctions.map((auction, index) => (
                                    <TableRow key={index}>
                                        <TableCell>
                                            {auction.auctionRoomId}
                                            <Box component="span" sx={{ ml: 2 }}>
                                                <a 
                                                    href={`/listener/${auction.auctionRoomId}`} 
                                                    target="_blank" 
                                                    rel="noopener noreferrer"
                                                >
                                                    ライブ画面
                                                </a>
                                            </Box>
                                        </TableCell>
                                        <TableCell>{auction.liverUlid}</TableCell>
                                        <TableCell sx={{ textAlign: "center" }}>
                                            <Button
                                                variant="contained"
                                                color="error"
                                                size="small"
                                                onClick={() => deleteAuction(auction.auctionRoomId)}
                                            >
                                                削除
                                            </Button>
                                        </TableCell>
                                    </TableRow>
                                ))
                            ) : (
                                !isLoading && (
                                    <TableRow>
                                        <TableCell colSpan={3} align="center">
                                            <Typography>データがありません</Typography>
                                        </TableCell>
                                    </TableRow>
                                )
                            )}
                        </TableBody>
                    </Table>
                </TableContainer>

                {/* 戻るボタン */}
                <Box sx={{ textAlign: "center", mt: 3 }}>
                    <Button 
                        variant="outlined" 
                        color="secondary" 
                        onClick={() => navigate('/')} 
                        sx={{ width: 200 }}
                    >
                        TOPへ戻る
                    </Button>
                </Box>
            </Container>

            {/* 新規追加のモーダル */}
            <Dialog open={open} onClose={handleClose}>
                <DialogTitle>🆕 オークション追加</DialogTitle>
                <DialogContent>
                    <TextField
                        margin="dense"
                        label="Auction Room ID"
                        name="auctionRoomId"
                        fullWidth
                        variant="outlined"
                        onChange={handleChange}
                        required
                    />
                    <TextField
                        margin="dense"
                        label="Liver ULID"
                        name="liverUlid"
                        fullWidth
                        variant="outlined"
                        onChange={handleChange}
                        required
                    />
                </DialogContent>
                <DialogActions>
                    <Button onClick={handleClose} color="secondary">
                        キャンセル
                    </Button>
                    <Button 
                        onClick={() => addAuction(newAuction, handleClose)} 
                        color="primary" 
                        variant="contained"
                    >
                        追加
                    </Button>
                </DialogActions>
            </Dialog>

            <Footer />
        </>
    );
}

export default PageAuctions;
