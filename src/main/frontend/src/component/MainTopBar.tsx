import React, { useContext } from 'react';
import styled from "styled-components";
import { Colors } from "../resource/Colors";
import { Tab } from "../page/MainPage";
import { LoginContext } from "../context/LoginContextProvider";
import { Link } from "react-router-dom";

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

const TextButton = styled.button`
  border: 0;
  outline: 0;
  background-color: rgba(0, 0, 0, 0);
  color: white;
  cursor: pointer;
  font-size: 18px;
  font-weight: 700;

  &:hover {
    color: whitesmoke;
  }
`;

const StyledLink = styled(Link)`
  text-decoration: none;
  color: white;
  font-size: 18px;
  font-weight: 700;
  margin-left: 20px;

  &:hover {
    color: whitesmoke;
  }
`;

interface Props {
  setTab: (_: Tab) => void;
}

const MainTopBar = ({ setTab }: Props) => {
  const { logout } = useContext(LoginContext); // 로그아웃 함수 가져오기

  return (
    <Container>
      {/* 왼쪽 메뉴 */}
      <LeftMenu>
        <TextButton onClick={() => setTab(Tab.FORM)}>등록하기</TextButton>
        <TextButton onClick={() => setTab(Tab.LIST)} style={{ marginLeft: '20px' }}>목록</TextButton>
      </LeftMenu>

      {/* 오른쪽 메뉴 */}
      <RightMenu>
        <StyledLink to="/admin">마이페이지</StyledLink>
        <TextButton onClick={logout} style={{ marginLeft: '20px' }}>로그아웃</TextButton>
      </RightMenu>
    </Container>
  );
};

export default MainTopBar;