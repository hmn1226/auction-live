import React, { useEffect, useState } from "react";
import useWebSocket from "../hooks/useWebSocket";
import StartButton from "../components/StartButton";
import StopButton from "../components/StopButton";
import CurrentPriceButton from "../components/CurrentPriceButton";
import AuctionLaneStatus from "../components/AuctionLaneStatus";
import BidButton from "../components/LiveBidButton";
import { useNavigate } from 'react-router-dom';
import Header from '../components/Header';
import Footer from '../components/Footer';
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
    Container
} from "@mui/material";

const SOCKET_URL = "http://localhost:8080/gs-guide-websocket";

const PageWebsocket = ()=>{
    const roomId = "room1";
    const {wsObj,sendMessage} = useWebSocket(roomId);
    const navigate = useNavigate();
    const changePage = () => {
        navigate('/');
    };
    
    return (
        <>
            <Header/>
            <Container>
                <button onClick={sendMessage}>Send Message</button>
                <div style={{ border: "1px solid #F00" }}>
                {wsObj?.auctionLaneMonitors?.map((monitor, index) => (
                    <div key={index}>
                    <span>{monitor.hoge}</span>
                    <span>{monitor.auctionEntryId}  </span>
                    <span>{monitor.auctionEntryName}    </span>
                    <span>{monitor.currentPrice}    </span>
                    <span>{monitor.currentHolderUserId} </span>
                    
                    <AuctionLaneStatus monitor={monitor}/>
                    {/* auctionLaneStatus に応じてボタンを切り替え */}
                    {monitor.auctionLaneStatus !== "START" && <StartButton monitor={monitor} />}
                    {monitor.auctionLaneStatus !== "STOP" && <StopButton monitor={monitor} />}

                    <CurrentPriceButton monitor={monitor} />
                    <BidButton monitor={monitor} />
                    
                    </div> 

                ))}
                </div>
            {/* 🔹 戻るボタン */}
            <Button variant="outlined" onClick={changePage}>TOPへ戻る</Button>
            </Container>
            <Footer/>
        </>
    );
}
export default PageWebsocket;