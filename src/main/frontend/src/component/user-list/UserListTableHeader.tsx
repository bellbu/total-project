import React from 'react';
import styled from "styled-components";
import {Colors} from "../../resource/Colors";

const Container = styled.header`
  position: relative;
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

const UserListTableHeader = () => {
  return (
    <Container>
      <Title>사용자 이름</Title>
      <Title style={{ left: '22%' }}>나이</Title>
      <Title style={{ left: '75%' }}>총 회원수</Title>
    </Container>
  );
};

export default UserListTableHeader;