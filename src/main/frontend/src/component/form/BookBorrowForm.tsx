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

    if (!name.trim() || !bookTitle.trim()) { // trim()을 사용하여 공백 제거
        Swal.alert('회원명과 책 이름을 입력해 주세요.', '', 'warning');
        return;
    }

    BookApi.postBookLoan(name, bookTitle)
      .then(data => {
        Swal.alert('책 대출에 성공했습니다!', '', 'success');
        setName('');
        setBookTitle('');
      })
      .catch(error => {
        // 백엔드에서 받은 에러 메시지 화면 표시
        const errorMessage = error?.data || error.message || '오류가 발생했습니다.';
        Swal.alert(errorMessage, '', 'error');
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