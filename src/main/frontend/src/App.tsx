import React, {useContext} from 'react';
import MainPage from "./page/MainPage";
import { Navigate, Route, Routes } from 'react-router-dom';
import LoginContextProvider from './context/LoginContextProvider';
import { LoginContext } from './context/LoginContextProvider'
import Login from './page/login/Login';
import Join from './page/join/Join';
import * as Swal from './api/common/alert';

/**
 * PublicRoute와 ProtectedRoute라는 별도의 컴포넌트를 정의해 접근 제어 구현
 * PublicRoute: 비로그인 상태에서만 접근 가능한 경로를 처리
 * ProtectedRoute: 로그인 상태에서만 접근 가능한 경로를 처리
 */
const PublicRoute = ({ children }: { children: JSX.Element }) => { // { children }: PublicRoute 안에 들어오는 컴포넌트를 의미
                                                                   // { children: JSX.Element }: children의 타입을 JSX.Element로 지정
  const { isLogin, isLoading } = useContext(LoginContext);
  
  if (isLoading) return null; // 로그인 체크 로딩 중인 경우 아무것도 렌더링하지 않음
  
  return !isLogin ? children : <Navigate to="/mainPage" replace />; // isLogin이 true인 경우: "/main"으로 리다이렉트, isLogin이 false인 경우: 자식 컴포넌트를 렌더링
};

const ProtectedRoute = ({ children, adminOnly }: { children: JSX.Element, adminOnly?: boolean }) => {
  const { isLogin, isLoading, authorities } = useContext(LoginContext);
  
  if (isLoading) return null;

  if (!isLogin) return <Navigate to="/loginPage" replace />;

  // 관리자 전용 페이지 접근 제어
  if (adminOnly && (!authorities || !authorities.isAdmin)) {
    Swal.alert("관리자만 접근할 수 있습니다.", "", "warning");
    return <Navigate to="/mainPage" replace />;
  }

  return children;

  // return isLogin ? children : <Navigate to="/loginPage" replace />; // isLogin이 true인 경우: 자식 컴포넌트를 렌더링, isLogin이 false인 경우: "/login"으로 리다이렉트

};

/**
 * App 컴포넌트: 라우팅과 로그인 컨텍스트 관리 역할
 * <LoginContextProvider>: 로그인 관련 정보를 관리하는 컨텍스트
 * <AppRoutes />: 라우팅 설정을 관리하는 컴포넌트, React Router의 Routes와 Route를 사용해 페이지별 라우팅을 설정
 */
function App() {
  return (
    <LoginContextProvider>
      <AppRoutes />
    </LoginContextProvider>
  );
}

// 라우팅 로직을 별도 컴포넌트로 분리
function AppRoutes() {
  const { isLoading } = useContext(LoginContext);

  if (isLoading) return null; // 로그인 체크 로딩 중이면 아무것도 렌더링하지 않음

  /**
   * <Routes>: Route들을 감싸는 컨테이너(페이지 전환을 관리)
   * <Route path="..." element={...} />: 각 경로(path)에 해당하는 페이지를 렌더링
   */
  return (
    <Routes>

      <Route path="/" 
        element={
          <ProtectedRoute>
            <MainPage />
          </ProtectedRoute>
        } 
      />
      
      <Route path="/loginPage"
        element={
          <PublicRoute>
            <Login />
          </PublicRoute>
        }
      />

      <Route path="/joinPage"
        element={
          <ProtectedRoute adminOnly={true}>
            <MainPage />
          </ProtectedRoute>
        }
      />

      <Route path="/mainPage"
        element={
          <ProtectedRoute>
            <MainPage />
          </ProtectedRoute>
        }
      />

      <Route path="/userPage"
        element={
          <ProtectedRoute>
            <MainPage />
          </ProtectedRoute>
        }
      />

      <Route path="/loanPage"
        element={
          <ProtectedRoute>
            <MainPage />
          </ProtectedRoute>
        }
      />

      <Route path="/adminPage"
        element={
          <ProtectedRoute>
            <MainPage />
          </ProtectedRoute>
        }
      />


    </Routes>
  );
}

export default App;
