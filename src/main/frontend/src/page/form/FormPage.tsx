import React from 'react';
import styled from "styled-components";
import UserRegisterForm from "../../component/form/UserRegisterForm";
import BookAdditionForm from "../../component/form/BookAdditionForm";
import BookBorrowForm from "../../component/form/BookBorrowForm";
import BookReturnForm from "../../component/form/BookReturnForm";

const GridContainer = styled.div`
  display: flex;
  height: 470px;
  gap: 40px;
  margin: 20px;
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