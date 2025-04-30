import React, { useContext, useEffect, useState } from 'react';
import styled from "styled-components";
import { Colors } from "../resource/Colors";
import { Tab } from "../page/MainPage";
import { LoginContext } from "../context/LoginContextProvider";
import { useNavigate } from "react-router-dom";
import api from '../api/login/api';
import * as Swal from '../../src/api/common/alert';
import SwalLib, { SweetAlertResult } from 'sweetalert2'; // SweetAlertResult 타입을 임포트
import Cookies from 'js-cookie'; // Cookies 모듈 임포트

const Container = styled.div`
  width: calc(100% - 2.4vw);
  height: 7vh;
  background-color: ${Colors.primaryColor};
  display: flex;
  flex-direction: row;
  align-items: center;
  justify-content: space-between; /* 양쪽 정렬 */
  padding: 0 1.24vw;
`;

const LeftMenu = styled.div`
  display: flex;
  flex-direction: row;
`;

const RightMenu = styled.div`
  display: flex;
  flex-direction: row;
  align-items: center;
`;

const TextButton = styled.button<{ isActive?: boolean }>`
  border: 0;
  outline: 0;
  background-color: transparent;
  color: ${({ isActive }) => (isActive ? "#FFD700" : "white")}; /* 선택된 탭은 금색 */
  font-size: 20px;
  font-weight: 700;
  cursor: pointer;
  position: relative;
  padding-bottom: 4px;
  margin-right: 20px; /* 버튼 간격 추가 */

  &:hover {
    color: #f0e68c;
  }

  ${({ isActive }) =>
    isActive &&
    `
    &::after {
      content: "";
      position: absolute;
      left: 0;
      bottom: 0;
      width: 100%;
      height: 3px;
      background-color: #FFD700; /* 선택된 탭 아래 금색 밑줄 */
    }
  `}

  &:last-child {
    margin-right: 0; /* 마지막 버튼에는 마진을 적용하지 않음 */
  }
`;

const TimeDisplay = styled.div`
  background-color: #fffbe6;
  color: #581717;
  opacity: 0.8;
  padding: 4px 10px;
  border-radius: 6px;
  font-size: 19px;
  font-weight: 700;
  margin-right: 20px;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.08);
`;

interface Props { // 부모 컴퍼넌트에서 넘어온 setTab과 tab의 값을 Props 인터페이스로 정의
  setTab: (newTab: Tab) => void; // Tab 타입의 매개변수("newTab")를 받아서 void를 반환하는 함수.
  tab: Tab; // Tab 타입
}

const MainTopBar = ({ setTab, tab }: Props) => {
    const { isLogin, adminInfo, authorities, logout, jwtExpirationTime, setJwtExpirationTime } = useContext(LoginContext);
    const navigate = useNavigate();
    const [timeRemaining, setTimeRemaining] = useState<number | null>(null); // 토큰 남은 만료시간
    const [alertShown, setAlertShown] = useState<boolean>(false); // 토큰 연장 알림 보여졌는지 여부 확인 상태 (alertShown 기본값은 false)

    // JWT 만료시간이 있는 경우 타이머 실행
    useEffect(() => {
        const storedAlertShown = localStorage.getItem("alertShown"); // localStorage: 웹 브라우저에 저장(새로고침 시 다시 알림을 띄우지 않기 위함), getItem("alertShown"): "alertShown"라는 키에 해당하는 값을 가져옴
        if (storedAlertShown === "true") {
            setAlertShown(true); // 이전에 토큰 연장 알림을 보여준 적이 있으면 alertShown값 true로 변경
        }

        if (!isLogin) {
            navigate("/loginPage");
        }

        if (jwtExpirationTime) {
            const interval = setInterval(() => { // setInterval: 일정한 시간 간격으로 반복적으로 작업을 실행하게 해주는 JS 함수
                const currentTime = Date.now(); // currentTime: 현재 시간(밀리 초)
                const remainingTime = jwtExpirationTime - currentTime; // remainingTime: 토큰 남은 만료시간

                if (remainingTime <= 0) {
                  clearInterval(interval); // 토큰 만료 시 인터벌 종료
                  SwalLib.close(); // 토큰 만료 시 남아있는 알림창 닫기
                  logout(true); // 강제 로그아웃
                } else {
                  setTimeRemaining(Math.floor(remainingTime / 1000)); // (remainingTime / 1000): 밀리초 → 초로 변환, Math.floor(): 소수점 내림
                }
            }, 1000);

            return () => clearInterval(interval); // 컴포넌트 언마운트 시 인터벌 정리
        }

    }, [isLogin, jwtExpirationTime, logout]);

    // 토큰 만료 60초 전 알림
    useEffect(() => {
        // timeRemaining이 60초 이하, alertShown이 false일 때 알림 띄움
        if (timeRemaining !== null && timeRemaining <= 60 && timeRemaining > 0 && !alertShown) {
            // 알림창을 뜨게 하고 alertShown을 true로 설정
            setAlertShown(true);  // 알림을 띄운 후 alertShown을 true로 설정
            localStorage.setItem("alertShown", "true"); // 로컬 스토리지에 상태 저장: 새로고침 시 다시 알림을 띄우지 않기 위함

            Swal.confirm('1분 남았습니다!', '곧 자동 로그아웃됩니다. 연장하시겠습니까?', 'warning', (result: SweetAlertResult) => {
                if (result.isConfirmed) {
                   refreshAccessToken(); // 새로운 토큰 발급
                }
            });
        }

        // 만료 시간 연장한 경우 초기화 처리
        if (timeRemaining !== null && timeRemaining > 60 && alertShown) {
            setAlertShown(false);
            localStorage.setItem("alertShown", "false");
        }

        // 토큰 만료 시 로그아웃 처리
        if (timeRemaining !== null && timeRemaining <= 0) {
            SwalLib.close(); // 토큰 만료 시 남아있는 알림창 닫기
            logout(true); // 강제 로그아웃
        }

    }, [timeRemaining, alertShown, logout]); // timeRemaining과 alertShown이 변경될 때마다 실행

    const refreshAccessToken = async () => {
        try {
            const response = await api.post('/token/refresh-token');

            const newToken = await response.data; // text(): 새 access token을 문자열 형식으로 반환
            Cookies.set("accessToken", newToken);

            // 새로운 토큰으로 만료 시간 업데이트
            const decoded = parseJwt(newToken);
            if (decoded && decoded.exp) {
                const newExpiration = decoded.exp * 1000;
                setJwtExpirationTime(newExpiration);
            }
        } catch (error) {
            Swal.alert('로그인 정보가 만료되었습니다.', '다시 로그인해주세요.', 'warning');
            logout(true);
        }
    };

    const parseJwt = (token: string) => {
        try {
            // 토큰의 두번째 부분(payload) 가져오기
            const base64Url = token.split('.')[1];

            // Base64URL → Base64로 변환
            const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');

            // Base64 → UTF-8 문자열로 복원  ex) jsonPayload: {"exp":1744204137,"adminNo":"1","email":"admin@admin.com","authorities":["ROLE_ADMIN"]}
            const jsonPayload = decodeURIComponent( // decodeURIComponent(): 마지막으로 퍼센트 인코딩된 문자열을 정상적인 UTF-8 문자열로 변환
                atob(base64) // atob(): Base64 인코딩된 문자열 디코딩 ex) "eyJleHAiOjE2..." → {"exp":168...}
                .split('') // 한 글자씩 쪼개기
                .map((c) => '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2)) // 각 글자 → 16진수 → 퍼센트 인코딩
                .join('')
            );
            // JSON.parse(): JSON 문자열 → 객체로 파싱
            return JSON.parse(jsonPayload);
        } catch (e) {
            console.error('JWT 파싱 실패', e);
            return null;
        }
    };

    // 회원 목록 클릭 핸들러 추가
    const handleListClick = () => {
        // authorities가 null이 아니고 isUser가 true이며 isAdmin이 false인 경우
        if (!authorities?.isAdmin) {
            Swal.alert("관리자만 접근할 수 있습니다.", "", "warning");
            return;
        }
        setTab(Tab.USER);
    }

    return (
        <Container>
            {/* 왼쪽 메뉴 */}
            <LeftMenu>
                <TextButton onClick={() => setTab(Tab.FORM)} isActive={tab === Tab.FORM}>홈</TextButton>
                <TextButton onClick={handleListClick} isActive={tab === Tab.USER}>회원 목록</TextButton>
            </LeftMenu>

            {/* 오른쪽 메뉴 */}
            <RightMenu>
                <TimeDisplay>
                    {timeRemaining !== null && timeRemaining > 0 ? `토큰 만료시간: ${Math.floor(timeRemaining / 60)}분 ${timeRemaining % 60}초` : ''}
                </TimeDisplay>
                <TextButton onClick={() => setTab(Tab.ADMIN)} isActive={tab === Tab.ADMIN}>
                    {(adminInfo?.name || '사용자') + ' 님'}
                </TextButton>
                <TextButton onClick={() => logout()}>로그아웃</TextButton>
            </RightMenu>
        </Container>
    );

};

export default MainTopBar;