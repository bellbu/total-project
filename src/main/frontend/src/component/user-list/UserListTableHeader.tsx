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
`;

const TitleRight = styled.p`
  margin-left: auto;
  font-size: 20px;
  font-weight: 800;
  color: ${Colors.primaryColor};
`;

const UserListTableHeader = ({ userCount }: { userCount: number } ) => {
    const formattedCount = userCount.toLocaleString();

    return (
        <Container>
            <Title>회원 이름</Title>
            <Title style={{ left: '22%' }}>나이</Title>
            <TitleRight>총 회원수 : {formattedCount} 명</TitleRight>
        </Container>
    );
};

export default UserListTableHeader;