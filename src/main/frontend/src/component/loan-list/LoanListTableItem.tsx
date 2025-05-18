import React, {useState} from 'react';
import styled from "styled-components";
import {LoanData} from "../../model/LoanData";

const Container = styled.div`
  position: relative;
  width: 100%;
  height: 85px;
  display: flex;
  flex-direction: row;
  align-items: center;
  border-bottom: 1px solid black;
`;

const Column = styled.div<{ left?: string }>`
  position: absolute;
  left: ${({ left }) => left || '0'};
  width: fit-content;
`;

const Text = styled.p`
  font-size: 18px;
  font-weight: 700;
  color: black;
  margin: 0;
`;

interface Props {
  data: LoanData
  refresh: () => void;
}

const formatDateToKorean = (dateStr?: string | null) => {
  if (!dateStr) return "-";
  const date = new Date(dateStr);
  return `${date.getFullYear()}년 ${date.getMonth() + 1}월 ${date.getDate()}일`;
};

const LoanListTableItem = ({ data }: Props) => {

  return (
    <Container>
        <Column>
            <Text>{data.userName}</Text>
        </Column>
        <Column left="22%">
            <Text>{data.bookName}</Text>
        </Column>
        <Column left="44%">
            <Text>{data.isReturn ? "반납됨" : "대출 중"}</Text>
        </Column>
        <Column left="66%">
            <Text>{formatDateToKorean(data.loanedAt)}</Text>
        </Column>
        <Column left="88%">
            <Text>{formatDateToKorean(data.returnedAt)}</Text>
        </Column>
    </Container>
  );

};

export default LoanListTableItem;