import React, {useState} from 'react';
import styled from "styled-components";
import {ReactComponent as Borrow} from "../../resource/icon/borrow.svg";
import {Colors} from "../../resource/Colors";
import FormInput from "../common/FormInput";
import Button from "../common/Button";
import {BookApi} from "../../api/app/BookApi";
import * as Swal from '../../api/common/alert';

const Container = styled.div`
  width: 50%;
  height: 100%;
  border-radius: 10px;
  border: 1px solid #D9D9D9;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  box-sizing: border-box;
`;

const Title = styled.p`
  font-size: 24px;
  font-weight: 800;
  color: ${Colors.primaryColor};
`;

const BookBorrowForm = () => {
  const [name, setName] = useState<string>('');
  const [bookTitle, setBookTitle] = useState<string>('');

  const handleClick = () => {
    BookApi.postBookLoan(name, bookTitle)
      .then(data => {
        Swal.alert('책 대출에 성공했습니다!');
        setName('');
        setBookTitle('');
      })
      .catch(error => {
        Swal.alert('회원명 또는 책 이름을 확인해주세요.');
      })
  }

  return (
    <Container>
      <Borrow />
      <Title>책 대출</Title>
      <FormInput title={'회원명'} value={name} onChange={setName} />
      <FormInput title={'책 이름'} value={bookTitle} onChange={setBookTitle} />
      <Button label={'저장'} onClick={handleClick} />
    </Container>
  );
};

export default BookBorrowForm;