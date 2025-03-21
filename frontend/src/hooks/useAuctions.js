import { useState, useEffect } from 'react';

const API_BASE_URL = "http://localhost:8080/api/auctions";

export function useAuctions(navigate) {
    // 状態管理
    const [auctions, setAuctions] = useState([]);
    const [isLoading, setIsLoading] = useState(false);
    const [error, setError] = useState(null);
    const token = localStorage.getItem('token');

    // オークション一覧取得
    useEffect(() => {
        // 認証チェック
        if (!token) {
            navigate('/login');
            return;
        }

        // データ取得開始
        setIsLoading(true);
        setError(null);

        // API通信
        fetch(API_BASE_URL, {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${token}`
            }
        })
        .then(response => {
            // 認証エラー処理
            if (response.status === 401 || response.status === 403) {
                navigate('/login');
                return;
            }
            return response.json();
        })
        .then(data => {
            // 成功時の処理
            if (data) setAuctions(data.auctions);
            setIsLoading(false);
        })
        .catch(error => {
            // エラー時の処理
            console.error("APIエラー:", error);
            setError("オークション情報の取得に失敗しました");
            setIsLoading(false);
        });
    }, [token, navigate]);

    // オークション削除処理
    const deleteAuction = (auctionRoomId) => {
        // 確認ダイアログ
        if (!window.confirm(`オークション ${auctionRoomId} を削除してもよろしいですか？`)) return;

        // API通信
        fetch(`${API_BASE_URL}/${auctionRoomId}`, {
            method: "DELETE",
            headers: { "Authorization": `Bearer ${token}` }
        })
        .then(response => {
            if (response.ok) {
                // 成功時の処理
                setAuctions(prevAuctions => prevAuctions.filter(a => a.auctionRoomId !== auctionRoomId));
            } else {
                // エラー時の処理
                console.error("削除に失敗しました");
                setError("オークションの削除に失敗しました");
            }
        })
        .catch(error => {
            console.error("削除エラー:", error);
            setError("オークションの削除中にエラーが発生しました");
        });
    };

    // オークション追加処理
    const addAuction = (newAuction, handleClose) => {
        // 入力検証
        if (!newAuction.auctionRoomId || !newAuction.liverUlid) {
            setError("すべての項目を入力してください");
            return;
        }

        // API通信
        fetch(`${API_BASE_URL}/${newAuction.auctionRoomId}`, {
            method: "PUT",
            headers: {
                "Content-Type": "application/json",
                "Authorization": `Bearer ${token}`
            },
            body: JSON.stringify(newAuction),
        })
        .then(response => response.json())
        .then(data => {
            // 成功時の処理
            setAuctions(prevAuctions => [...prevAuctions, data]);
            handleClose();
        })
        .catch(error => {
            // エラー時の処理
            console.error("追加エラー:", error);
            setError("オークションの追加に失敗しました");
        });
    };

    // 返却値
    return { 
        auctions, 
        deleteAuction, 
        addAuction, 
        isLoading, 
        error, 
        setError 
    };
}
