import { useState, useEffect } from 'react';

const API_BASE_URL = "http://localhost:8080/api/users";

export function useUsers(navigate) {
    const [users, setUsers] = useState([]);
    const token = localStorage.getItem('token');

    useEffect(() => {
        if (!token) {
            navigate('/login');
            return;
        }

        fetchUsers();
    }, [token, navigate]);

    const fetchUsers = () => {
        fetch(API_BASE_URL, {
            method: 'GET',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${token}`
            }
        })
        .then(response => {
            if (response.status === 401 || response.status === 403) {
                navigate('/login');
                return;
            }
            return response.json();
        })
        .then(data => {
            if (data) setUsers(data.users);
        })
        .catch(error => console.error("APIエラー:", error));
    };

    const deleteUser = (email) => {
        fetch(`${API_BASE_URL}/${email}`, {
            method: "DELETE",
            headers: { "Authorization": `Bearer ${token}` }
        })
        .then(() => {
            setUsers(prevUsers => prevUsers.filter(u => u.email !== email));
        })
        .catch(error => console.error("削除エラー:", error));
    };

    const saveUser = (user, handleClose) => {
        const method = user.isEditing ? "PUT" : "PUT";
        const url = `${API_BASE_URL}/${user.email}`;

        fetch(url, {
            method: method,
            headers: {
                "Content-Type": "application/json",
                "Authorization": `Bearer ${token}`
            },
            body: JSON.stringify({
                email: user.email,
                nickname: user.nickname,
                password: user.password,
                role: user.role,
                verified: user.verified
            }),
        })
        .then(response => response.json())
        .then(() => {
            handleClose();  // モーダルを閉じる
            fetchUsers();   // 最新のユーザー一覧を取得
        })
        .catch(error => console.error("エラー:", error));
    };

    return { users, deleteUser, saveUser };
}