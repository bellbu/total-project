import React, { useContext } from 'react';
import styled from "styled-components";
import { Colors } from "../resource/Colors";
import { Tab } from "../page/MainPage";
import { LoginContext } from "../context/LoginContextProvider";
import { Link, useNavigate } from "react-router-dom";
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
    const { isLogin, adminInfo, authorities, logout } = useContext(LoginContext);
    const navigate = useNavigate();

    if (!isLogin) {
        navigate("/login");
    }

    // 회원 목록 클릭 핸들러 추가
    const handleListClick = () => {
        // authorities가 null이 아니고 isUser가 true이며 isAdmin이 false인 경우
        if (authorities && authorities.isUser && !authorities.isAdmin) {
            Swal.alert("관리자만 접근할 수 있습니다.");
            return;
        }
        setTab(Tab.LIST);
    }

    return (
        <Container>
            {/* 왼쪽 메뉴 */}
            <LeftMenu>
                <TextButton onClick={() => setTab(Tab.FORM)}>홈</TextButton>
                <TextButton onClick={handleListClick} style={{ marginLeft: '20px' }}>회원 목록</TextButton>
            </LeftMenu>

            {/* 오른쪽 메뉴 */}
            <RightMenu>
                <StyledLink to="/admin">{(adminInfo?.name || '사용자') + ' 님'}</StyledLink>
                <TextButton onClick={() => logout()} style={{ marginLeft: '20px' }}>로그아웃</TextButton>
            </RightMenu>
        </Container>
    );

};

export default MainTopBar;