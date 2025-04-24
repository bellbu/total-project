import React from 'react';
import styled from "styled-components";
import MainTopBar from "../component/MainTopBar";
import FormPage from "./form/FormPage";
import UserListPage from "./user-list/UserListPage";
import Admin from "./admin/Admin"; // 관리자 페이지 추가
import { useLocation, useNavigate } from "react-router-dom";

const Container = styled.div`
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
  min-width: 1440px;
`

const ContentsContainer = styled.div<{ isUserTab: boolean }>`
  width: 100%;
  height: calc(100% - 6vh);
  box-sizing: border-box;
  overflow-y: auto;
  ${(props) => props.isUserTab && `
      scrollbar-gutter: stable;
` }
`;

// Tab 타입
export enum Tab {
   FORM = "mainPage", // Tab.FORM = "main"
   USER = "userPage", // Tab.USER = "user"
   ADMIN = "adminPage", // Tab.ADMIN = "admin"
}

const MainPage = () => {

  const location = useLocation(); // 현재 URL 정보를 가져옴
  const navigate = useNavigate();

  // URL에서 현재 탭 가져오기
  const currentTab = location.pathname.replace("/", "") as Tab; // 현재 URL에서 "/" 제거 후 Tab 타입으로 변환 currentTab = main/user/admin
  const tab = Object.values(Tab).includes(currentTab) ? currentTab : Tab.FORM; // currentTab이 Tab에 속하면 currentTab이 유지, 속하지 않으면 Tab.FORM(메인) 유지

  // 탭 변경 시 URL도 변경
  const setTab = (newTab: Tab) => {  // (newTab: Tab) - newTab의 타입을 Tab으로 지정
    navigate(`/${newTab}`);
  };

  return (
    <Container>
      <MainTopBar setTab={setTab} tab={tab}/>
      <ContentsContainer isUserTab={tab === Tab.USER}>
        {tab === Tab.FORM && <FormPage />} {/* tab === Tab.FORM가 true인 경우 => <FormPage /> 렌더링 */}
        {tab === Tab.USER && <UserListPage />}
        {tab === Tab.ADMIN && <Admin />}
      </ContentsContainer>
    </Container>
  );
};

export default MainPage;