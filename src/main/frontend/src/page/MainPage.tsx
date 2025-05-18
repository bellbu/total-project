import React from 'react';
import styled from "styled-components";
import MainTopBar from "../component/MainTopBar";
import FormPage from "./form/FormPage";
import UserListPage from "./user-list/UserListPage";
import LoanListPage from "./loan-list/LoanListPage";
import Admin from "./admin/Admin"; // 관리자 페이지 추가
import { useLocation, useNavigate } from "react-router-dom";

const Container = styled.div`
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
  min-width: 1440px;
`

const ContentsContainer = styled.div<{ isStableScroll: boolean }>`
  width: 100%;
  height: calc(100% - 6vh);
  box-sizing: border-box;
  overflow-y: auto;
  ${(props) => props.isStableScroll && `
      scrollbar-gutter: stable;
` }
`;

// 열거형 객체: Tab, 속성: FORM, USER, ADMIN
export enum Tab {
   FORM = "mainPage", // Tab.FORM = "main"
   USER = "userPage", // Tab.USER = "user"
   LOAN = "loanPage", // Tab.LOAN = "loan"
   ADMIN = "adminPage", // Tab.ADMIN = "admin"
}

const MainPage = () => {

  const location = useLocation(); // 현재 URL 정보를 가져옴
  const navigate = useNavigate();

  // URL에서 현재 탭 가져오기
  const currentTab = location.pathname.replace("/", "") as Tab; // 현재 URL에서 "/" 제거 후 Tab 타입으로 변환 currentTab = mainPage/userPage/adminPage
  // 현재 탭이 정의한 Tab 값에 포함되면 현재탭 유지, 포함되지 않으면 메인 페이지 유지
  const tab = Object.values(Tab).includes(currentTab) ? currentTab : Tab.FORM; // Object.values(Tab): 열거형 Tab객체의 값인 ["mainPage", "userPage", "adminPage"]을 반환

  // 탭 변경 시 URL도 변경
  const setTab = (newTab: Tab) => {  // (newTab: Tab) - newTab의 타입을 Tab으로 지정
    navigate(`/${newTab}`);
  };

  return (
    <Container>
      <MainTopBar setTab={setTab} tab={tab}/> {/* setTab: 탭에 해당하는 URL로 이동 / tab: URL 이동 후 현재 탭 저장 */}
      <ContentsContainer isStableScroll={tab === Tab.USER || tab === Tab.LOAN}>
        {tab === Tab.FORM && <FormPage />} {/* tab === Tab.FORM가 true인 경우 => <FormPage /> 렌더링 */}
        {tab === Tab.USER && <UserListPage />}
        {tab === Tab.LOAN && <LoanListPage />}
        {tab === Tab.ADMIN && <Admin />}
      </ContentsContainer>
    </Container>
  );
};

export default MainPage;