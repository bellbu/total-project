import React, {useEffect, useState} from 'react';
import styled from "styled-components";
import {LoanData} from "../../model/LoanData";
import {BookApi} from "../../api/app/BookApi";
import LoanListTableHeader from "../../component/loan-list/LoanListTableHeader";
import LoanListTableItem from "../../component/loan-list/LoanListTableItem";

const Container = styled.div`
  display: flex;
  flex-direction: column;
  margin: 20px 150px;
`;

const LoanListPage = () => {

  const [loanList, setLoanList] = useState<LoanData[]>([])

  useEffect(() => {
    refresh()
  }, [])

  const refresh = () => {
    BookApi.getLoans()
      .then(data => {
        setLoanList(data)
      })
  }

  return (
    <Container>
      <LoanListTableHeader />
      {loanList.map(item => (
          <LoanListTableItem key={item.id} data={item} refresh={refresh} />
      ))}
    </Container>
  );
};

export default LoanListPage;