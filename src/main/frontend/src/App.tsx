import React, {useContext} from 'react';
import MainPage from "./page/MainPage";
import { BrowserRouter, Navigate, Route, Routes } from 'react-router-dom';
import LoginContextProvider from './context/LoginContextProvider';
import { LoginContext } from './context/LoginContextProvider'
import Login from './page/login/Login';
import Join from './page/join/Join';

const ProtectedRoute = ({ children }: { children: JSX.Element }) => {
  const { isLogin } = useContext(LoginContext);
  return isLogin ? children : <Navigate to="/login" replace />;
};

function App() {
  return (
    /*
    <React.StrictMode>
      <MainPage/>
    </React.StrictMode>
    */
    <BrowserRouter>
        <LoginContextProvider>
            <Routes>
              {/* 기본 URL에 접속 시 "/login"으로 리다이렉트 */}
              <Route path="/" element={<Navigate to="/login" />} />

              <Route path="/login" element={ <Login />}></Route>
              <Route path="/join" element={ <Join />}></Route>
              <Route path="/main"
                      element={
                              //<ProtectedRoute>
                                <MainPage />
                              //</ProtectedRoute>
                              }
              />
            </Routes>
        </LoginContextProvider>
    </BrowserRouter>
  );
}

export default App;
