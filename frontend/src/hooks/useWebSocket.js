import React, { useEffect, useState } from "react";
import SockJS from "sockjs-client";
import { Stomp } from "@stomp/stompjs";

const SOCKET_URL = "http://localhost:8080/gs-guide-websocket";

const useWebSocket = (roomId) => {
    const [stompClient, setStompClient] = useState(null);
    const [wsObj, setWsObj] = useState(null);

    useEffect(() => {
        connectWebSocket();
        return () => disconnectWebSocket(); // Cleanup on unmount
    }, []);

    const connectWebSocket = () => {
        console.log("Opening WebSocket...");

        const token = localStorage.getItem("token");

        // クエリパラメータとしてトークンを付与
        const socket = new SockJS(`${SOCKET_URL}?token=${token}`);
        const stomp = Stomp.over(socket);
        stomp.debug = () => {};

        stomp.connect({}, () => {
            console.log("WebSocket connected!");
            stomp.subscribe(`/topic/messages/${roomId}`, (message) => {
                onMessageReceived(message);
            });
        });

        setStompClient(stomp);
    };

    const disconnectWebSocket = () => {
        if (stompClient) {
            stompClient.disconnect(() => {
                console.log("WebSocket disconnected!");
            });
        }
    };

    const onMessageReceived = (message) => {
        const obj = JSON.parse(message.body);
        let wsObj = JSON.parse(obj.content);
        setWsObj(wsObj);
    };

    return {
        wsObj
    };
};

export default useWebSocket;