import * as React from 'react';
import { useUsers } from '../hooks/useUsers';
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
    Snackbar,
    Alert,
    FormControl,
    FormLabel,
    RadioGroup,
    FormControlLabel,
    Radio,
    Checkbox,
    Box
} from "@mui/material";
import { useNavigate } from 'react-router-dom';
import Header from '../components/Header';
import Footer from '../components/Footer';
import { CheckBox } from '@mui/icons-material';

function PageUsers() {
    const navigate = useNavigate();
    const { users, saveUser, deleteUser } = useUsers(navigate);
    const [open, setOpen] = React.useState(false);
    const [isEditing, setIsEditing] = React.useState(false);
    const [currentUser, setCurrentUser] = React.useState({
        email: "", nickname: "", password: "", confirmPassword: "", role: "NORMAL" , verified: false
    });
    const [alertOpen, setAlertOpen] = React.useState(false);
    const [alertMessage, setAlertMessage] = React.useState("");

    // モーダルを開く（追加 or 編集）
    const handleOpen = (user = null) => {
        if (user) {
            setIsEditing(true);
            setCurrentUser({ ...user, password: "", confirmPassword: "", role: user.role || "NORMAL", verified: user.verified || false  });
        } else {
            setIsEditing(false);
            setCurrentUser({ email: "", nickname: "", password: "", confirmPassword: "", role: "NORMAL", verified: false  });
        }
        setOpen(true);
    };

    const handleClose = () => {
        setOpen(false);
        setIsEditing(false);
        setCurrentUser({ email: "", nickname: "", password: "", confirmPassword: "", role: "NORMAL" });
    };

    const handleChange = (e) => {
        setCurrentUser({ ...currentUser, [e.target.name]: e.target.value });
    };

    const handleCheckboxChange = (e) => {
        setCurrentUser({ ...currentUser, verified: e.target.checked });
    };
    
    const handleDelete = (email) => {
        if (window.confirm(`ユーザー ${email} を削除してもよろしいですか？`)) {
            deleteUser(email);
        }
    };

    const handleSubmit = () => {
        if (!currentUser.role) {
            setCurrentUser({ ...currentUser, role: "NORMAL" });
        }

        if (isEditing) {
            if (currentUser.password && currentUser.password !== currentUser.confirmPassword) {
                setAlertMessage("パスワードが一致しません");
                setAlertOpen(true);
                return;
            }
            const updatedUser = { ...currentUser };
            if (!updatedUser.password) {
                delete updatedUser.password;
                delete updatedUser.confirmPassword;
            }
            saveUser(updatedUser, handleClose);
        } else {
            if (!currentUser.password) {
                setAlertMessage("パスワードは必須です");
                setAlertOpen(true);
                return;
            }
            if (currentUser.password !== currentUser.confirmPassword) {
                setAlertMessage("パスワードが一致しません");
                setAlertOpen(true);
                return;
            }
            saveUser(currentUser, handleClose);
        }
    };

    return (
        <>
            <Header />
            <Box sx={{ maxWidth: 900, margin: "auto", padding: 3 }}>
                <Typography variant="h4" sx={{ textAlign: "center", mb: 2 }}>ユーザー一覧</Typography>
                
                <Box sx={{ display: "flex", justifyContent: "flex-end", mb: 2 }}>
                    <Button variant="contained" color="primary" onClick={() => handleOpen()} sx={{ fontWeight: "bold" }}>
                        ➕ 新規追加
                    </Button>
                </Box>

                <TableContainer component={Paper} sx={{ borderRadius: 2, boxShadow: 3 }}>
                    <Table>
                        <TableHead>
                            <TableRow sx={{ backgroundColor: "#1976d2" }}>
                                <TableCell sx={{ color: "white", fontWeight: "bold" }}>メールアドレス</TableCell>
                                <TableCell sx={{ color: "white", fontWeight: "bold" }}>ニックネーム</TableCell>
                                <TableCell sx={{ color: "white", fontWeight: "bold" }}>権限</TableCell>
                                <TableCell sx={{ color: "white", fontWeight: "bold" }}>検証済み</TableCell>
                                <TableCell sx={{ color: "white", fontWeight: "bold", textAlign: "center" }}>操作</TableCell>
                            </TableRow>
                        </TableHead>
                        <TableBody>
                            {users!=null && users.length > 0 ? (
                                users.map((user, index) => (
                                    <TableRow key={index}>
                                        <TableCell>{user.email}</TableCell>
                                        <TableCell>{user.nickname}</TableCell>
                                        <TableCell>{user.role || "NORMAL"}</TableCell>
                                        <TableCell>{user.verified ? "✅" : "❌"}</TableCell>
                                        <TableCell sx={{ textAlign: "center" }}>
                                            <Button variant="contained" color="warning" size="small" onClick={() => handleOpen(user)} sx={{ mr: 1 }}>
                                                編集
                                            </Button>
                                            <Button variant="contained" color="error" size="small" onClick={() => handleDelete(user.email)}>
                                                削除
                                            </Button>
                                        </TableCell>
                                    </TableRow>
                                ))
                            ) : (
                                <TableRow>
                                    <TableCell colSpan={4} align="center">
                                        <Typography>データがありません</Typography>
                                    </TableCell>
                                </TableRow>
                            )}
                        </TableBody>
                    </Table>
                </TableContainer>
            </Box>

            <Dialog open={open} onClose={handleClose}>
                <DialogTitle sx={{ fontWeight: "bold" }}>{isEditing ? "✏️ ユーザー編集" : "🆕 ユーザー追加"}</DialogTitle>
                <DialogContent>
                    <Box sx={{ display: "flex", flexDirection: "column", gap: 2, width: 400 }}>
                        <TextField label="メールアドレス" name="email" fullWidth variant="outlined" value={currentUser.email} onChange={handleChange} disabled={isEditing} />
                        <TextField label="ニックネーム" name="nickname" fullWidth variant="outlined" value={currentUser.nickname} onChange={handleChange} />
                        <TextField label="パスワード" name="password" type="password" fullWidth variant="outlined" value={currentUser.password} onChange={handleChange} />
                        <TextField label="パスワード確認" name="confirmPassword" type="password" fullWidth variant="outlined" value={currentUser.confirmPassword} onChange={handleChange} />

                        {/* 権限選択 */}
                        <FormControl sx={{ mt: 2 }}>
                            <FormLabel>権限</FormLabel>
                            <RadioGroup
                                row
                                name="role"
                                value={currentUser.role || "NORMAL"}
                                onChange={(e) => setCurrentUser({ ...currentUser, role: e.target.value })}
                            >
                                <FormControlLabel value="NORMAL" control={<Radio />} label="NORMAL" />
                                <FormControlLabel value="ADMIN" control={<Radio />} label="ADMIN" />
                            </RadioGroup>
                        </FormControl>
                        <FormControlLabel control={<Checkbox checked={currentUser.verified} onChange={handleCheckboxChange} />} label="検証済み" />
                    </Box>
                </DialogContent>
                <DialogActions>
                    <Button onClick={handleClose} color="secondary">キャンセル</Button>
                    <Button onClick={handleSubmit} color="primary" variant="contained">{isEditing ? "更新" : "追加"}</Button>
                </DialogActions>
            </Dialog>

            <Snackbar open={alertOpen} autoHideDuration={3000} onClose={() => setAlertOpen(false)}>
                <Alert severity="error" onClose={() => setAlertOpen(false)}>
                    {alertMessage}
                </Alert>
            </Snackbar>
            <Footer/>
        </>
    );
}

export default PageUsers;