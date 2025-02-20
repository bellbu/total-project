import React from 'react';
import ReactDOM from 'react-dom/client';
import './index.css';
import App from './App';
import reportWebVitals from './reportWebVitals';
import { BrowserRouter } from 'react-router-dom';

/**
 * index.tsx: React 애플리케이션의 진입점
 * ReactDOM.createRoot(): 넘겨받은 실제 DOM 요소를 React의 가상 DOM과 연결하고, React Root를 생성
 */
const root = ReactDOM.createRoot(document.getElementById('root') as HTMLElement); // 실제 DOM에서 id="root"인 요소를 가져옴, React 애플리케이션이 렌더링될 컨테이너 역할

/**
 * .render(): React 컴포넌트를 가상 DOM에 생성하고, 이를 비교 및 계산한 후 필요한 부분만 실제 DOM에 반영하여 <div id="root"></div> 내부에 렌더링
 * <React.StrictMode>: 개발 모드에서만 작동하며, 애플리케이션이 올바른 방식으로 동작하도록 검사
 * <BrowserRouter>: 라우팅 컴포넌트로, 애플리케이션의 URL을 관리, 라우팅 설정이 필요한 컴포넌트를 감싸야 함(<App /> 내부에서 Routes와 Route를 설정해 특정 URL에 대해 화면을 렌더링)
 * <App />: 최상위 컴포넌트, 이 컴포넌트는 src/App.tsx에 위치
 */

root.render(
  <React.StrictMode>
    <BrowserRouter future={{ v7_startTransition: true, v7_relativeSplatPath: true }}>
      <App />
    </BrowserRouter>
  </React.StrictMode>
);

reportWebVitals();  // reportWebVitals(): React 애플리케이션의 성능 측정을 위한 도구
