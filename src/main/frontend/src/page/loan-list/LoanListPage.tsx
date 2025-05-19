import React, {useEffect, useState} from 'react';
import styled from "styled-components";
import {LoanData} from "../../model/LoanData";
import {BookApi} from "../../api/app/BookApi";
import LoanListTableHeader from "../../component/loan-list/LoanListTableHeader";
import LoanListTableItem from "../../component/loan-list/LoanListTableItem";
import * as Swal from '../../api/common/alert';

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
      .catch((error) => {
        const errorMessage = error?.data || error.message || '오류가 발생했습니다.'
        Swal.alert(errorMessage, '', 'error')
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