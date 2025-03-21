import { useState } from "react";
import { useNavigate } from "react-router-dom";

const API_BASE_URL = "http://localhost:8080/api/auth";

export function useEmailVerification() {
    const [verificationToken, setVerificationToken] = useState("");
    const [verificationCode, setVerificationCode] = useState("");
    const [alertOpen, setAlertOpen] = useState(false);
    const [alertMessage, setAlertMessage] = useState("");
    const [alertSeverity, setAlertSeverity] = useState("error");
    const navigate = useNavigate();

    const verifyCode = () => {
        fetch(`${API_BASE_URL}/verify`, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ verificationToken, verificationCode })
        })
        .then(res => {
            if (!res.ok) {
                return res.json().then(errData => {
                    throw new Error("認証に失敗しました: " + errData.message);
                });
            }
            return res.json();
        })
        .then(() => {
            // 成功時の処理
            setAlertMessage("認証が完了しました");
            setAlertSeverity("success");
            setAlertOpen(true);

            setTimeout(() => {
                navigate('/');
            }, 1500);
        })
        .catch(err => {
            // エラー時の処理
            setAlertMessage(err.message);
            setAlertSeverity("error");
            setAlertOpen(true);
        });
    };

    return {
        verificationToken,
        setVerificationToken,
        verificationCode,
        setVerificationCode,
        verifyCode,
        alertOpen,
        setAlertOpen,
        alertMessage,
        alertSeverity,
    };
}