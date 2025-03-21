import { useState } from 'react';
import { useNavigate } from 'react-router-dom';

const API_BASE_URL = 'http://localhost:8080/api/auth';

export function useAuth() {
    // 状態管理
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const [alertOpen, setAlertOpen] = useState(false);
    const [alertMessage, setAlertMessage] = useState('');
    const [alertSeverity, setAlertSeverity] = useState('success');
    const navigate = useNavigate();

    // ログイン処理
    const handleLogin = () => {
        // 入力検証
        if (!email || !password) {
            setAlertMessage('メールアドレスとパスワードを入力してください');
            setAlertSeverity('error');
            setAlertOpen(true);
            return;
        }

        // API通信
        fetch(`${API_BASE_URL}/login`, {
            method: 'POST',
            headers: { 
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({ email, password })
        })
        .then(res => {
            if (!res.ok) {
                return res.json().then(errData => {
                    throw new Error('ログインに失敗しました: ' + errData.message);
                });
            }
            return res.json();
        })
        .then(data => {
            // 成功時の処理
            localStorage.setItem('token', data.token);
            setAlertMessage('ログイン成功');
            setAlertSeverity('success');
            setAlertOpen(true);
            setTimeout(() => {
                navigate('/');
            }, 1500);
        })
        .catch(err => {
            // エラー時の処理
            setAlertMessage(err.message);
            setAlertSeverity('error');
            setAlertOpen(true);
        });
    };

    // 返却値
    return {
        email, setEmail,
        password, setPassword,
        handleLogin,
        alertOpen, setAlertOpen,
        alertMessage,
        alertSeverity,
    };
}
