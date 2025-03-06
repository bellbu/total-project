import React from 'react';
import styled from "styled-components";
import UserRegisterForm from "../../component/form/UserRegisterForm";
import BookAdditionForm from "../../component/form/BookAdditionForm";
import BookBorrowForm from "../../component/form/BookBorrowForm";
import BookReturnForm from "../../component/form/BookReturnForm";

const GridContainer = styled.div`
  display: flex;
  height: 43vh;
  gap: 0 1.2vw;
  margin: 2.35vh 1.1vw;
`

const FormPage = () => {
  return (
    <>
      <GridContainer>
        <UserRegisterForm/>
        <BookAdditionForm/>
      </GridContainer>
      <GridContainer>
        <BookBorrowForm/>
        <BookReturnForm/>
      </GridContainer>
    </>
  );
};

export default FormPage;