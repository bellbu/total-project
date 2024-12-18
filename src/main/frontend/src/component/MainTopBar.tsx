import React from 'react';
import styled from "styled-components";
import {Colors} from "../resource/Colors";
import {Tab} from "../page/MainPage";

const Container = styled.div`
  width: calc(100% - 40px);
  height: 60px;
  background-color: ${Colors.primaryColor};
  display: flex;
  flex-direction: row;
  align-items: center;
  padding: 0 20px
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
`

interface Props {
  setTab: (_: Tab) => void;
}

const MainTopBar = ({ setTab }: Props) => {
  return (
    <Container>
      <TextButton onClick={() => setTab(Tab.FORM)}>등록하기</TextButton>
      <TextButton onClick={() => setTab(Tab.LIST)} style={{marginLeft: '20px'}}>목록</TextButton>
    </Container>
  );
};

export default MainTopBar;