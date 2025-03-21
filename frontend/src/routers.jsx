import { Routes, Route } from "react-router-dom";
import PageTop from "./page/pageTop.jsx";
import PageAuctions from "./page/pageAuctions.jsx";
import PageLogin from "./page/pageLogin.jsx";
import PageUsers from "./page/pageUsers.jsx";
import PageWebsocket from "./page/pageWebsocket.jsx"
import PageSignup from "./page/pageSignup.jsx";
import PageEmailVerification from "./page/pageEmailVerification.jsx";
import PageLanding from "./page/pageLanding.jsx";
import PageListener from "./page/pageListener.jsx";

export const AppRoutes = () => {
   return (
       <Routes>
            <Route path="/" element={<PageTop />} />
            <Route path="/auctions" element={<PageAuctions />} />
            <Route path="/login" element={<PageLogin />} /> 
            <Route path="/users" element={<PageUsers />} />
            <Route path="/websocket" element={<PageWebsocket />} />
            <Route path="/signup" element={<PageSignup/>} />
            <Route path="/emailVerification/:token" element={<PageEmailVerification/>} />
            <Route path="/landing" element={<PageLanding/>}/>
            <Route path="/listener/:auctionRoomId" element={<PageListener/>} />
       </Routes>
   )
}