import React, { useContext } from 'react';
import styled from "styled-components";
import { Colors } from "../resource/Colors";
import { Tab } from "../page/MainPage";
import { LoginContext } from "../context/LoginContextProvider";
import { useNavigate } from "react-router-dom";
import * as Swal from '../../src/api/common/alert';

const Container = styled.div`
  width: calc(100% - 40px);
  height: 60px;
  background-color: ${Colors.primaryColor};
  display: flex;
  flex-direction: row;
  align-items: center;
  justify-content: space-between; /* 양쪽 정렬 */
  padding: 0 20px;
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

interface Props { // 부모 컴퍼넌트에서 넘어온 setTab과 tab의 타입을 Props 인터페이스로 정의
  setTab: (newTab: Tab) => void; // Tab 타입의 매개변수("newTab")를 받아서 void를 반환하는 함수.
  tab: Tab; // Tab 타입
}

const MainTopBar = ({ setTab, tab }: Props) => {
    const { isLogin, adminInfo, authorities, logout } = useContext(LoginContext);
    const navigate = useNavigate();

    if (!isLogin) {
        navigate("/loginPage");
    }

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
                <TextButton onClick={() => setTab(Tab.ADMIN)} isActive={tab === Tab.ADMIN}>
                    {(adminInfo?.name || '사용자') + ' 님'}
                </TextButton>
                <TextButton onClick={() => logout()}>로그아웃</TextButton>
            </RightMenu>
        </Container>
    );

};

export default MainTopBar;