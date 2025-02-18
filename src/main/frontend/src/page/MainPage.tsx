import React, {useState} from 'react';
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

const ContentsContainer = styled.div`
  width: 100%;
  height: calc(100% - 60px);
  box-sizing: border-box;
  overflow-y: auto;
`;

export enum Tab {
   FORM = "main",
   USER = "user",
   ADMIN = "admin",
}

const MainPage = () => {

  const location = useLocation();
  const navigate = useNavigate();

  // URL에서 현재 탭 가져오기 (기본값: FORM)
  const currentTab = location.pathname.replace("/", "") as Tab;
  const tab = Object.values(Tab).includes(currentTab) ? currentTab : Tab.FORM;

  // 탭 변경 시 URL도 변경
  const setTab = (newTab: Tab) => {
    navigate(`/${newTab}`);
  };

  return (
    <Container>
      <MainTopBar setTab={setTab} tab={tab}/>
      <ContentsContainer>
        {tab === Tab.FORM && <FormPage />}
        {tab === Tab.USER && <UserListPage />}
        {tab === Tab.ADMIN && <Admin  setTab={setTab} />}
      </ContentsContainer>
    </Container>
  );
};

export default MainPage;