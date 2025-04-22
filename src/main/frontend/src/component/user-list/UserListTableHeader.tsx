import React, { forwardRef, useRef, useImperativeHandle } from 'react';
import styled from "styled-components";
import {Colors} from "../../resource/Colors";
import Lottie, { LottieRefCurrentProps } from "lottie-react";
import refreshAnimation from "../../resource/icon/refresh.json";

const Container = styled.header`
  position: sticky;
  background-color: #fff;
  top: 108px;
  z-index: 99;
  width: 100%;
  height: 62px;
  display: flex;
  box-sizing: border-box;
  flex-direction: row;
  align-items: center;
  border-bottom: 1px solid black;
  padding: 0px 0px 0px 20px;
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
  font-size: 20px;
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

const ResetButton = styled.button`
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 0;
  border: none;
  background: none;
  cursor: pointer;
`;

interface UserListTableHeaderProps {
  userCount: number;
  searchedCount: number;
  onReset: () => void; // 새로 조회하는 함수
}

const UserListTableHeader = forwardRef<LottieRefCurrentProps, UserListTableHeaderProps>(({ userCount, searchedCount, onReset }, ref) => {
    const formattedTotalCount = userCount.toLocaleString();
    const formattedSearchedCount = searchedCount.toLocaleString();
    // 내부에서 lottieRef 생성
    const lottieRef = useRef<LottieRefCurrentProps>(null);
    // 부모에서 ref 사용할 수 있게 expose
    useImperativeHandle(ref, () => lottieRef.current as LottieRefCurrentProps);

    return (
        <Container>
            <Title>회원 이름</Title>
            <Title style={{ left: '22%' }}>나이</Title>
             <TitleRightContainer>
                <Highlighted>{formattedSearchedCount} 명 조회</Highlighted>
                <Separator>|</Separator>
                <span>총 회원 수 : {formattedTotalCount} 명</span>
                <ResetButton onClick={onReset}>
                    <Lottie lottieRef={lottieRef} animationData={refreshAnimation} loop={false} style={{ width: 45, height: 45 }} />
                </ResetButton>
              </TitleRightContainer>
        </Container>
    );
});

export default UserListTableHeader;