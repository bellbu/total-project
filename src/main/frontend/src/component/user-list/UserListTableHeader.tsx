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
  padding: 0 20px;
`;

const Title = styled.p`
  position: absolute;
  margin: 0;
  font-size: 20px;
  font-weight: 800;
  color: ${Colors.primaryColor};
`;

const TitleRightContainer = styled.div`
  margin-left: auto;
  display: flex;
  align-items: center;
  font-size: 18px;
  font-weight: 700;
  color: ${Colors.primaryColor};
`;

const Highlighted = styled.span`
  color: darkorange; // 파란색 강조
`;

const Separator = styled.span`
  margin: 0 10px;
  color: #6c757d; // 구분선 색상 연하게
  font-weight: 400;
`;
const UserListTableHeader = ({ userCount, searchedCount }: { userCount: number, searchedCount:number } ) => {
    const formattedTotalCount = userCount.toLocaleString();
    const formattedSearchedCount = searchedCount.toLocaleString();

    return (
        <Container>
            <Title>회원 이름</Title>
            <Title style={{ left: '22%' }}>나이</Title>
             <TitleRightContainer>
                <Highlighted>{formattedSearchedCount} 명 조회</Highlighted>
                <Separator>|</Separator>
                <span>총 회원 수 : {formattedTotalCount} 명</span>
              </TitleRightContainer>
        </Container>
    );
};

export default UserListTableHeader;