import React, {useState} from 'react';
import styled from "styled-components";
import MainTopBar from "../component/MainTopBar";
import FormPage from "./form/FormPage";
import UserListPage from "./user-list/UserListPage";

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
  overflow-y: auto;
`;

export enum Tab {
  FORM, LIST,
}

const MainPage = () => {
  const [tab, setTab] = useState<Tab>(Tab.FORM);

  return (
    <Container>
      <MainTopBar setTab={setTab} />
      <ContentsContainer>
        {tab === Tab.FORM && <FormPage />}
        {tab === Tab.LIST && <UserListPage />}
      </ContentsContainer>
    </Container>
  );
};

export default MainPage;