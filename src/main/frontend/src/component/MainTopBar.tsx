import React, { useContext, useEffect, useState } from 'react';
import styled from "styled-components";
import { Colors } from "../resource/Colors";
import { Tab } from "../page/MainPage";
import { LoginContext } from "../context/LoginContextProvider";
import { useNavigate } from "react-router-dom";
import * as Swal from '../../src/api/common/alert';
import { SweetAlertResult } from 'sweetalert2'; // SweetAlertResult 타입을 임포트

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
  color: white;
  font-size: 20px;
  font-weight: 700;
  margin-right: 20px;
`;

interface Props { // 부모 컴퍼넌트에서 넘어온 setTab과 tab의 타입을 Props 인터페이스로 정의
  setTab: (newTab: Tab) => void; // Tab 타입의 매개변수("newTab")를 받아서 void를 반환하는 함수.
  tab: Tab; // Tab 타입
}

const MainTopBar = ({ setTab, tab }: Props) => {
    const { isLogin, adminInfo, authorities, logout, jwtExpirationTime } = useContext(LoginContext);
    const navigate = useNavigate();
    const [timeRemaining, setTimeRemaining] = useState<number | null>(null); // 만료 시간까지 남은 시간(초)
    const [alertShown, setAlertShown] = useState<boolean>(false); // 알림이 이미 보여졌는지 체크하는 상태 추가

    useEffect(() => {
        const storedAlertShown = localStorage.getItem("alertShown");
        if (storedAlertShown === "true") {
            setAlertShown(true);
        }

        if (!isLogin) {
            navigate("/loginPage");
        }

        // JWT 만료 시간에 대한 카운트다운 시작
        if (jwtExpirationTime) {
            const interval = setInterval(() => {
                const currentTime = Date.now();
                const remainingTime = jwtExpirationTime - currentTime;

                if (remainingTime <= 0) {
                  clearInterval(interval); // 시간이 다 되면 인터벌 종료
                  logout(true); // 만료되면 강제 로그아웃
                } else {
                  setTimeRemaining(Math.floor(remainingTime / 1000)); // 초 단위로 남은 시간 표시
                }
            }, 1000);

            return () => clearInterval(interval); // 컴포넌트 언마운트 시 인터벌 정리
        }

    }, [isLogin, jwtExpirationTime, logout]);

    useEffect(() => {
        // timeRemaining이 60초 이하일 때만 알림을 띄우며, alertShown이 false일 때만
        if (timeRemaining !== null && timeRemaining <= 60 && timeRemaining > 0 && !alertShown) {
            // 알림창을 뜨게 하고 alertShown을 true로 설정
            setAlertShown(true);  // 알림을 띄운 후 alertShown을 true로 설정
            localStorage.setItem("alertShown", "true"); // 로컬 스토리지에 상태 저장

            Swal.confirm('1분 남았습니다!', '곧 자동 로그아웃됩니다. 연장하시겠습니까?', 'warning', (result: SweetAlertResult) => {
                if (result.isConfirmed) {
                    // 연장 시 3분 추가
                    setTimeRemaining(180); // 예: 3분 연장
                }
            });
        }

        // 시간이 60초 이상이면 alertShown을 다시 false로 설정하지 않음
        if (timeRemaining !== null && timeRemaining > 60 && alertShown) {
            // 60초를 넘은 경우 alertShown을 false로 설정
            setAlertShown(false);  // alertShown을 false로 되돌려 알림창을 다시 띄우지 않게 함
            localStorage.setItem("alertShown", "false");
        }

        // 만료 시간이 되면 로그아웃 처리
        if (timeRemaining !== null && timeRemaining <= 0) {
            logout(true); // 세션 만료 시 강제 로그아웃
        }

    }, [timeRemaining, alertShown, logout]); // timeRemaining과 alertShown이 변경될 때마다 실행

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
                    {timeRemaining !== null && timeRemaining > 0 ? `JWT 만료시간: ${Math.floor(timeRemaining / 60)}분 ${timeRemaining % 60}초` : ''}
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