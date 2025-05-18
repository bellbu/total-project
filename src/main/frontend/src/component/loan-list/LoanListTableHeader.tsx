import React from 'react';
import styled from "styled-components";
import {Colors} from "../../resource/Colors";

const Container = styled.header`
  position: sticky;
  background-color: #fff;
  top: 20px;
  z-index: 10;
  width: 100%;
  height: 62px;
  display: flex;
  box-sizing: border-box;
  flex-direction: row;
  align-items: center;
  border-bottom: 1px solid black;
`;

const Title = styled.p`
  position: absolute;
  margin: 0;
  font-size: 20px;
  font-weight: 800;
  color: ${Colors.primaryColor};
`

const LoanListTableHeader = () => {
  return (
    <Container>
      <Title>회원 이름</Title>
      <Title style={{ left: '22%' }}>책 이름</Title>
      <Title style={{ left: '44%' }}>반납 여부</Title>
      <Title style={{ left: '66%' }}>대출 일자</Title>
      <Title style={{ left: '88%' }}>반납 일자</Title>
    </Container>
  );
};

export default LoanListTableHeader;