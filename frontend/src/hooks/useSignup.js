import { useState } from "react";
import { useNavigate } from "react-router-dom";

const API_BASE_URL = "http://localhost:8080/api/auth";

export function useSignup() {
    // 状態管理
    const [email, setEmail] = useState("");
    const [password, setPassword] = useState("");
    const [confirmPassword, setConfirmPassword] = useState("");
    const [nickname, setNickname] = useState("");
    const [alertOpen, setAlertOpen] = useState(false);
    const [alertMessage, setAlertMessage] = useState("");
    const [alertSeverity, setAlertSeverity] = useState("error");
    const [isLoading, setIsLoading] = useState(false);
    const navigate = useNavigate();

    // 入力検証
    const validateInputs = () => {
        if (!email || !password || !confirmPassword || !nickname) {
            setAlertMessage("全ての項目を入力してください");
            setAlertSeverity("error");
            setAlertOpen(true);
            return false;
        }

        if (password !== confirmPassword) {
            setAlertMessage("パスワードが一致しません");
            setAlertSeverity("error");
            setAlertOpen(true);
            return false;
        }

        return true;
    };

    // サインアップ処理
    const handleSignup = () => {
        // 入力検証
        if (!validateInputs()) return;

        // 登録処理開始
        setIsLoading(true);

        // API通信
        fetch(`${API_BASE_URL}/register`, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ email, password, nickname }),
        })
        .then(res => {
            if (!res.ok) {
                return res.json().then(errData => {
                    throw new Error(errData.message || "登録に失敗しました");
                });
            }
            return res.json();
        })
        .then(data => {
            // 成功時の処理
            setAlertMessage(data.message || "登録が完了しました");
            setAlertSeverity("success");
            setAlertOpen(true);
            setIsLoading(false);
            
            // 登録成功後、一定時間後にログインページへ遷移
            setTimeout(() => {
                navigate('/login');
            }, 2000);
        })
        .catch(err => {
            // エラー時の処理
            setAlertMessage(err.message);
            setAlertSeverity("error");
            setAlertOpen(true);
            setIsLoading(false);
        });
    };

    // 返却値
    return {
        email, setEmail,
        password, setPassword,
        confirmPassword, setConfirmPassword,
        nickname, setNickname,
        handleSignup,
        alertOpen, setAlertOpen,
        alertMessage,
        alertSeverity,
        isLoading
    };
}
