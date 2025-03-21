import React, { useState } from "react";
import {
  Container,
  Typography,
  TextField,
  Button,
  Select,
  MenuItem,
  FormControl,
  InputLabel,
  Box,
  Grid,
  Paper,
  IconButton,
  Card,
  CardContent,
  Stack,
  Divider,
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  Snackbar,
  Alert,
  InputAdornment,
} from "@mui/material";
import ToggleButton from "@mui/material/ToggleButton";
import CloudUploadIcon from "@mui/icons-material/CloudUpload";
import DeleteIcon from "@mui/icons-material/Delete";
import EditIcon from "@mui/icons-material/Edit";
import ContentCopyIcon from "@mui/icons-material/ContentCopy";
import { useDropzone } from "react-dropzone";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import {
  faCalendarDays,
  faPenToSquare,
  faSquareCheck,
} from "@fortawesome/free-solid-svg-icons";
import Header from "../components/Header";
import Footer from "../components/Footer";
import { useLanding } from "../hooks/useLanding";

// FileをBase64文字列に変換するヘルパー関数
const readFileAsBase64 = (file) => {
  return new Promise((resolve, reject) => {
    const reader = new FileReader();
    reader.onload = () => {
      resolve(reader.result); // 例: "data:image/png;base64,xxxxx..."
    };
    reader.onerror = reject;
    reader.readAsDataURL(file);
  });
};

// images配列の各要素を、{ data: base64文字列 } の形に変換する関数
const convertImagesToBase64 = async (files) => {
  const base64Array = [];
  for (const file of files) {
    if (file instanceof File) {
      const base64Str = await readFileAsBase64(file);
      base64Array.push({ data: base64Str });
    } else {
      // すでにBase64文字列の場合も、オブジェクト形式にする
      base64Array.push({ data: file });
    }
  }
  return base64Array;
};

const PageLanding = () => {
  // Auction state（新仕様）
  const [auction, setAuction] = useState({
    startDatetime: "",
    endDatetime: "",
    entries: [],
    auctionType: "",
  });

  // Entry state（出品商品情報）
  const [newEntry, setNewEntry] = useState({
    entryName: "",
    startPrice: "",
    reservePrice: "",
    quantity: "",
    entryDescription: "",
    bulkSaleMode: "1", // "1":まとめて販売, "2":数量指定して販売
    images: [],
  });
  const [editingIndex, setEditingIndex] = useState(null);

  // Mode settings
  // publicMode: 0 → 非公開, 1 → 公開
  const [publicMode, setPublicMode] = useState(0);
  // autoPilotMode: 0 → 通常, 1 → 自動進行モード
  const [autoPilotMode, setAutoPilotMode] = useState(0);

  // レーン数 → laneAmount
  const [laneAmount, setLaneAmount] = useState("");

  // pendingAuctionData と auctionRoomId（認証エラー時の再試行用）
  const [pendingAuctionData, setPendingAuctionData] = useState(null);
  const [auctionRoomId, setAuctionRoomId] = useState("");

  // モーダル用状態
  const [openModal, setOpenModal] = useState(false); // オークションURL発行モーダル
  const { generateAndStartAuction, showLoginModal, setShowLoginModal } = useLanding();
  const [openSignUpModal, setOpenSignUpModal] = useState(false);

  // ログイン用入力状態
  const [loginEmail, setLoginEmail] = useState("");
  const [loginPassword, setLoginPassword] = useState("");
  const [loginSnackbarOpen, setLoginSnackbarOpen] = useState(false);
  const [loginSnackbarMessage, setLoginSnackbarMessage] = useState("");
  const [loginSnackbarSeverity, setLoginSnackbarSeverity] = useState("success");

  // サインアップ用入力状態
  const [signUpEmail, setSignUpEmail] = useState("");
  const [signUpNickname, setSignUpNickname] = useState("");
  const [signUpPassword, setSignUpPassword] = useState("");
  const [signUpPasswordConfirm, setSignUpPasswordConfirm] = useState("");
  const [signUpMessage, setSignUpMessage] = useState("");

  // Helper: 数値フォーマット
  const formatNumber = (value) => (value ? new Intl.NumberFormat("ja-JP").format(value) : "");

  // Auction入力変更処理
  const handleInputChange = (e) => {
    const { name, value } = e.target;
    setAuction((prev) => ({ ...prev, [name]: value }));
  };

  // Entry入力変更処理
  const handleEntryChange = (e) => {
    const { name, value } = e.target;
    setNewEntry((prev) => ({
      ...prev,
      [name]:
        name === "startPrice" || name === "reservePrice" || name === "quantity"
          ? value.replace(/\D/g, "")
          : value,
    }));
  };

  // エントリー追加または編集保存
  const addEntry = () => {
    if (editingIndex !== null) {
      setAuction((prev) => {
        const updatedEntries = [...prev.entries];
        updatedEntries[editingIndex] = newEntry;
        return { ...prev, entries: updatedEntries };
      });
      setEditingIndex(null);
    } else {
      setAuction((prev) => ({
        ...prev,
        entries: [...prev.entries, newEntry],
      }));
    }
    setNewEntry({
      entryName: "",
      startPrice: "",
      reservePrice: "",
      quantity: "",
      entryDescription: "",
      bulkSaleMode: "1",
      images: [],
    });
  };

  // エントリー編集・削除
  const editEntry = (index) => {
    setNewEntry(auction.entries[index]);
    setEditingIndex(index);
  };
  const deleteEntry = (index) => {
    setAuction((prev) => ({
      ...prev,
      entries: prev.entries.filter((_, i) => i !== index),
    }));
  };

  // useDropzone for images
  const { getRootProps, getInputProps } = useDropzone({
    accept: "image/*",
    onDrop: (acceptedFiles) => {
      setNewEntry((prev) => ({
        ...prev,
        images: [...prev.images, ...acceptedFiles],
      }));
    },
  });

  const auctionFormats = [
    "ネットオークションモード",
    "オークションホールモード",
    "入札モード",
    "プライスダウンモード",
    "中古車オークションモード",
    "ブランドオークションモード",
    "フリマモード",
  ];

  // 「オークションをはじめる」ボタン押下時の処理
  const handleStartAuction = async () => {
    // 各エントリーの images を Base64化（オブジェクト形式: { data: base64String }）
    const convertedEntries = [];
    for (const entry of auction.entries) {
      const base64Images = await convertImagesToBase64(entry.images);
      convertedEntries.push({
        ...entry,
        images: base64Images,
      });
    }

    const auctionData = {
      startDatetime: auction.startDatetime,
      endDatetime: auction.endDatetime,
      auctionType: auction.auctionType,
      publicMode,       // 0 or 1
      autoPilotMode,    // 0 or 1
      laneAmount: Number(laneAmount),
      fullEntries: convertedEntries,
    };

    const result = await generateAndStartAuction(auctionData);
    if (result) {
      setAuctionRoomId(result.auctionRoomId);
      setOpenModal(true);
    } else {
      setPendingAuctionData(auctionData);
    }
  };

  // ログイン処理
  const handleLogin = async () => {
    try {
      const response = await fetch("http://localhost:8080/api/auth/login", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ email: loginEmail, password: loginPassword }),
      });
      if (!response.ok) {
        return response.json().then((errData) => {
          throw new Error("ログインに失敗しました: " + (errData.message || ""));
        });
      }
      const data = await response.json();
      localStorage.setItem("token", data.token);
      setLoginSnackbarMessage("ログイン成功");
      setLoginSnackbarSeverity("success");
      setLoginSnackbarOpen(true);
      setShowLoginModal(false);
      // ログイン成功後、2秒後に pendingAuctionData があれば再試行
      setTimeout(async () => {
        if (pendingAuctionData) {
          const result = await generateAndStartAuction(pendingAuctionData);
          if (result) {
            setAuctionRoomId(result.auctionRoomId);
            setOpenModal(true);
            setPendingAuctionData(null);
          }
        }
      }, 2000);
    } catch (err) {
      setLoginSnackbarMessage(err.message);
      setLoginSnackbarSeverity("error");
      setLoginSnackbarOpen(true);
    }
  };

  // サインアップ処理
  const handleSignUp = async () => {
    if (!signUpEmail || !signUpNickname || !signUpPassword || !signUpPasswordConfirm) {
      alert("全ての項目を入力してください");
      return;
    }
    if (signUpPassword !== signUpPasswordConfirm) {
      alert("パスワードと確認が一致しません");
      return;
    }
    try {
      const response = await fetch("http://localhost:8080/api/auth/register", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({
          email: signUpEmail,
          nickname: signUpNickname,
          password: signUpPassword,
        }),
      });
      if (!response.ok) {
        const errData = await response.json();
        throw new Error(errData.message || "登録に失敗しました");
      }
      setSignUpMessage(
        "認証コードを登録されたメールに送信しました。メールのリンクから認証コードを入力して登録を完了してください。その後ログインが可能になります。"
      );
      setTimeout(() => {
        setOpenSignUpModal(false);
        setShowLoginModal(true);
      }, 5000);
    } catch (err) {
      alert(err.message);
    }
  };

  // コピー処理
  const handleCopyUrl = () => {
    const url = `http://localhost:3000/listener/${auctionRoomId}`;
    navigator.clipboard.writeText(url);
  };

  return (
    <>
      <Header />
      <Container maxWidth="md" sx={{ mt: 4, mb: 4 }}>
        <Stack spacing={4}>
          {/* タイトル部分 */}
          <Box textAlign="center">
            <Typography variant="h4" sx={{ fontWeight: "bold", mb: 1 }}>
              Auction As A Service
            </Typography>
            <Typography variant="subtitle1">
              誰でも、いつでも、簡単に、オークションを開催しよう！
            </Typography>
          </Box>

          {/* オークション開催日時 */}
          <Paper sx={{ p: 3 }}>
            <Typography variant="h5" gutterBottom sx={{ mb: 2 }}>
              <FontAwesomeIcon icon={faCalendarDays} /> オークション開催日時を決めましょう
            </Typography>
            <Grid container spacing={2}>
              <Grid item xs={12} sm={6}>
                <TextField
                  label="開始日時"
                  type="datetime-local"
                  name="startDatetime"
                  fullWidth
                  onChange={handleInputChange}
                  InputLabelProps={{ shrink: true }}
                />
              </Grid>
              <Grid item xs={12} sm={6}>
                <TextField
                  label="終了予定日時"
                  type="datetime-local"
                  name="endDatetime"
                  fullWidth
                  onChange={handleInputChange}
                  InputLabelProps={{ shrink: true }}
                />
              </Grid>
            </Grid>
          </Paper>

          {/* 出品商品登録 */}
          <Paper sx={{ p: 3 }}>
            <Typography variant="h5" gutterBottom>
              <FontAwesomeIcon icon={faPenToSquare} /> 出品商品を登録しましょう
            </Typography>
            <Box
              {...getRootProps()}
              sx={{
                p: 2,
                mt: 2,
                border: "2px dashed #aaa",
                textAlign: "center",
                borderRadius: 2,
                cursor: "pointer",
                "&:hover": { backgroundColor: "#f9f9f9" },
              }}
            >
              <input {...getInputProps()} />
              <CloudUploadIcon fontSize="large" color="primary" />
              <Typography variant="body2" sx={{ mt: 1 }}>
                ドラッグ＆ドロップまたはクリックして画像をアップロード
              </Typography>
            </Box>
            {newEntry.images.length > 0 && (
              <Box
                sx={{
                  display: "flex",
                  gap: 2,
                  flexWrap: "wrap",
                  mt: 2,
                }}
              >
                {newEntry.images.map((file, index) => (
                  <Box key={index}>
                    <img
                      src={file instanceof File ? URL.createObjectURL(file) : file}
                      alt="プレビュー"
                      width={80}
                      height={80}
                      style={{ objectFit: "cover", borderRadius: 4 }}
                    />
                  </Box>
                ))}
              </Box>
            )}
            <Grid container spacing={2} sx={{ mt: 2 }}>
              <Grid item xs={12} sm={6} md={4}>
                <TextField
                  label="商品名"
                  name="entryName"
                  fullWidth
                  onChange={handleEntryChange}
                  value={newEntry.entryName}
                />
              </Grid>
              <Grid item xs={6} sm={3} md={2}>
                <TextField
                  label="スタート価格"
                  name="startPrice"
                  fullWidth
                  onChange={handleEntryChange}
                  value={formatNumber(newEntry.startPrice)}
                  inputProps={{ style: { textAlign: "right" } }}
                  InputLabelProps={{ shrink: true }}
                />
              </Grid>
              <Grid item xs={6} sm={3} md={2}>
                <TextField
                  label="最低落札価格"
                  name="reservePrice"
                  fullWidth
                  onChange={handleEntryChange}
                  value={formatNumber(newEntry.reservePrice)}
                  inputProps={{ style: { textAlign: "right" } }}
                  InputLabelProps={{ shrink: true }}
                />
              </Grid>
              <Grid item xs={6} sm={3} md={2}>
                <TextField
                  label="数量"
                  name="quantity"
                  type="number"
                  fullWidth
                  onChange={handleEntryChange}
                  value={newEntry.quantity}
                />
              </Grid>
              <Grid item xs={6} sm={3} md={2}>
                <FormControl fullWidth>
                  <InputLabel>販売方法</InputLabel>
                  <Select
                    name="bulkSaleMode"
                    value={newEntry.bulkSaleMode}
                    onChange={handleEntryChange}
                    label="販売方法"
                  >
                    <MenuItem value="1">まとめて販売</MenuItem>
                    <MenuItem value="2">数量指定して販売</MenuItem>
                  </Select>
                </FormControl>
              </Grid>
              <Grid item xs={12}>
                <TextField
                  label="商品説明"
                  name="entryDescription"
                  fullWidth
                  multiline
                  rows={3}
                  onChange={handleEntryChange}
                  value={newEntry.entryDescription}
                />
              </Grid>
            </Grid>
            <Box sx={{ mt: 2 }}>
              <Button variant="contained" onClick={addEntry}>
                {editingIndex !== null ? "編集を保存" : "商品を追加"}
              </Button>
            </Box>
          </Paper>

          {/* 登録済みの商品一覧 */}
          <Paper sx={{ p: 3 }}>
            <Typography variant="h6" gutterBottom>
              登録済みの商品一覧
            </Typography>
            <Divider sx={{ mb: 2 }} />
            {auction.entries.length === 0 ? (
              <Typography variant="body2" color="text.secondary">
                まだ商品が登録されていません。
              </Typography>
            ) : (
              <Stack spacing={2}>
                {auction.entries.map((entry, index) => (
                  <Box
                    key={index}
                    sx={{
                      display: "flex",
                      alignItems: "center",
                      gap: 2,
                      p: 2,
                      borderRadius: 2,
                      border: "1px solid #ddd",
                    }}
                  >
                    {entry.images[0] && (
                      <Box sx={{ minWidth: 80 }}>
                        <img
                          src={
                            entry.images[0] instanceof File
                              ? URL.createObjectURL(entry.images[0])
                              : entry.images[0].data
                          }
                          alt=""
                          width={80}
                          height={80}
                          style={{ objectFit: "cover", borderRadius: 4 }}
                        />
                      </Box>
                    )}
                    <Box sx={{ flexGrow: 1 }}>
                      <Typography variant="subtitle1" fontWeight="bold">
                        {entry.entryName}
                      </Typography>
                      <Typography variant="body2" color="text.secondary">
                        スタート: ¥{formatNumber(entry.startPrice)} / 最低落札: ¥
                        {formatNumber(entry.reservePrice)} / 数量: {entry.quantity}
                      </Typography>
                    </Box>
                    <Box>
                      <IconButton onClick={() => editEntry(index)}>
                        <EditIcon />
                      </IconButton>
                      <IconButton onClick={() => deleteEntry(index)}>
                        <DeleteIcon />
                      </IconButton>
                    </Box>
                  </Box>
                ))}
              </Stack>
            )}
          </Paper>

          {/* オークション形式・モード設定・レーン数 */}
          <Paper sx={{ p: 3 }}>
            <Typography variant="h5" gutterBottom>
              <FontAwesomeIcon icon={faSquareCheck} /> オークション形式を選択しましょう
            </Typography>
            <Grid container spacing={2} sx={{ mt: 2 }}>
              {auctionFormats.map((format, idx) => {
                const isSelected = auction.auctionType === String(idx + 1);
                return (
                  <Grid item xs={12} sm={6} md={4} key={format}>
                    <Card
                      sx={{
                        cursor: "pointer",
                        backgroundColor: isSelected ? "#fff9c4" : "#e0e0e0",
                        border: isSelected ? "2px solid #fff9c4" : "1px solid #ccc",
                        "&:hover": { boxShadow: 4 },
                      }}
                      onClick={() =>
                        setAuction((prev) => ({ ...prev, auctionType: String(idx + 1) }))
                      }
                    >
                      <CardContent>
                        <Typography
                          variant="body1"
                          sx={{
                            textAlign: "center",
                            fontWeight: isSelected ? "bold" : "normal",
                          }}
                        >
                          {format}
                        </Typography>
                      </CardContent>
                    </Card>
                  </Grid>
                );
              })}
            </Grid>
            <Divider sx={{ my: 3 }} />
            <Typography variant="h6" gutterBottom>
              モード設定
            </Typography>
            <Stack direction="row" spacing={2} sx={{ mb: 2 }}>
              <ToggleButton
                value="public"
                selected={publicMode === 1}
                size="small"
                onChange={() => setPublicMode(publicMode === 0 ? 1 : 0)}
                sx={{
                  textTransform: "none",
                  backgroundColor: publicMode === 1 ? "#fff9c4" : "#e0e0e0",
                  border: publicMode === 1 ? "2px solid #fff9c4" : "1px solid #ccc",
                  "&:hover": {
                    boxShadow: 4,
                    backgroundColor: publicMode === 1 ? "#fff9c4" : "#f0f0f0",
                  },
                  "&.Mui-selected": {
                    backgroundColor: "#fff9c4",
                    border: "2px solid #fff9c4",
                    "&:hover": { backgroundColor: "#fff9c4" },
                  },
                }}
              >
                公開モード
              </ToggleButton>
              <ToggleButton
                value="autoPilot"
                selected={autoPilotMode === 1}
                size="small"
                onChange={() => setAutoPilotMode(autoPilotMode === 0 ? 1 : 0)}
                sx={{
                  textTransform: "none",
                  backgroundColor: autoPilotMode === 1 ? "#fff9c4" : "#e0e0e0",
                  border: autoPilotMode === 1 ? "2px solid #fff9c4" : "1px solid #ccc",
                  "&:hover": {
                    boxShadow: 4,
                    backgroundColor: autoPilotMode === 1 ? "#fff9c4" : "#f0f0f0",
                  },
                  "&.Mui-selected": {
                    backgroundColor: "#fff9c4",
                    border: "2px solid #fff9c4",
                    "&:hover": { backgroundColor: "#fff9c4" },
                  },
                }}
              >
                自動進行モード
              </ToggleButton>
            </Stack>
            <Typography variant="h6" gutterBottom>
              オークションレーン数
            </Typography>
            <Box sx={{ display: "flex", alignItems: "center", gap: 1 }}>
              <TextField
                type="number"
                value={laneAmount}
                onChange={(e) => setLaneAmount(e.target.value)}
                size="small"
                inputProps={{ min: 0 }}
                sx={{ width: 100 }}
              />
              <Typography variant="body1">レーン</Typography>
            </Box>
          </Paper>

          {/* オークション開始ボタン */}
          <Box textAlign="center">
            <Typography variant="subtitle1" sx={{ mb: 1 }}>
              準備は出来ましたか？それではオークションをはじめましょう
            </Typography>
            <Button
              variant="contained"
              sx={{ mt: 1 }}
              disabled={!auction.auctionType}
              onClick={handleStartAuction}
            >
              オークションをはじめる
            </Button>
          </Box>
        </Stack>
      </Container>
      <Footer />

      {/* オークションURL発行モーダル */}
      <Dialog open={openModal} onClose={() => setOpenModal(false)}>
        <DialogTitle>オークションURLはこちら</DialogTitle>
        <DialogContent>
          <Typography sx={{ mb: 2 }}>
            オークション参加者にはこちらの情報を送信してください。<br />
            また後で使用出来るように、この情報は必ず保存しておいてください。
          </Typography>
          <Box>
            <TextField
              fullWidth
              variant="outlined"
              value={`http://localhost:3000/listener/${auctionRoomId}`}
              InputProps={{
                readOnly: true,
                endAdornment: (
                  <InputAdornment position="end">
                    <IconButton onClick={handleCopyUrl}>
                      <ContentCopyIcon />
                    </IconButton>
                  </InputAdornment>
                ),
              }}
            />
          </Box>
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setOpenModal(false)} variant="contained">
            閉じる
          </Button>
        </DialogActions>
      </Dialog>

      {/* ログイン用モーダル */}
      <Dialog open={showLoginModal} onClose={() => setShowLoginModal(false)}>
        <DialogTitle>
          ログインが必要です
          <IconButton
            aria-label="close"
            onClick={() => setShowLoginModal(false)}
            sx={{
              position: "absolute",
              right: 8,
              top: 8,
              color: (theme) => theme.palette.grey[500],
            }}
          >
            ×
          </IconButton>
        </DialogTitle>
        <DialogContent>
          <Typography sx={{ mb: 2 }}>
            オークションを開始するためにはログインが必要です。メールアドレスとパスワードを入力してください。
          </Typography>
          <TextField
            fullWidth
            label="メールアドレス"
            variant="outlined"
            value={loginEmail}
            onChange={(e) => setLoginEmail(e.target.value)}
            sx={{ mb: 2 }}
          />
          <TextField
            fullWidth
            label="パスワード"
            variant="outlined"
            type="password"
            value={loginPassword}
            onChange={(e) => setLoginPassword(e.target.value)}
          />
        </DialogContent>
        <DialogActions sx={{ justifyContent: "center" }}>
          <Button onClick={handleLogin} variant="contained">
            ログイン
          </Button>
        </DialogActions>
        <Box textAlign="center" sx={{ mb: 2, mt: 1 }}>
          <Typography variant="body2">
            まだユーザー登録をしていない方は{" "}
            <Button
              variant="text"
              onClick={() => {
                setShowLoginModal(false);
                setOpenSignUpModal(true);
              }}
            >
              こちら
            </Button>
          </Typography>
        </Box>
      </Dialog>

      {/* サインアップ用モーダル */}
      <Dialog open={openSignUpModal} onClose={() => setOpenSignUpModal(false)}>
        <DialogTitle>
          ユーザー登録
          <IconButton
            aria-label="close"
            onClick={() => setOpenSignUpModal(false)}
            sx={{
              position: "absolute",
              right: 8,
              top: 8,
              color: (theme) => theme.palette.grey[500],
            }}
          >
            ×
          </IconButton>
        </DialogTitle>
        <DialogContent>
          {signUpMessage ? (
            <Typography sx={{ mb: 2 }}>{signUpMessage}</Typography>
          ) : (
            <>
              <TextField
                fullWidth
                label="メールアドレス"
                variant="outlined"
                value={signUpEmail}
                onChange={(e) => setSignUpEmail(e.target.value)}
                sx={{ mb: 2 }}
              />
              <TextField
                fullWidth
                label="ニックネーム"
                variant="outlined"
                value={signUpNickname}
                onChange={(e) => setSignUpNickname(e.target.value)}
                sx={{ mb: 2 }}
              />
              <TextField
                fullWidth
                label="パスワード"
                variant="outlined"
                type="password"
                value={signUpPassword}
                onChange={(e) => setSignUpPassword(e.target.value)}
                sx={{ mb: 2 }}
              />
              <TextField
                fullWidth
                label="パスワード確認"
                variant="outlined"
                type="password"
                value={signUpPasswordConfirm}
                onChange={(e) => setSignUpPasswordConfirm(e.target.value)}
              />
            </>
          )}
        </DialogContent>
        {!signUpMessage && (
          <DialogActions sx={{ justifyContent: "center" }}>
            <Button onClick={handleSignUp} variant="contained">
              ユーザー登録をする
            </Button>
          </DialogActions>
        )}
      </Dialog>

      {/* Snackbar for login notifications */}
      <Snackbar
        open={loginSnackbarOpen}
        autoHideDuration={5000}
        onClose={() => setLoginSnackbarOpen(false)}
      >
        <Alert
          onClose={() => setLoginSnackbarOpen(false)}
          severity={loginSnackbarSeverity}
          sx={{ width: "100%" }}
        >
          {loginSnackbarMessage}
        </Alert>
      </Snackbar>
    </>
  );
};

export default PageLanding;
