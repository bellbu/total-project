import React, {useState} from 'react';
import styled from "styled-components";
import {ReactComponent as Book} from "../../resource/icon/book.svg";
import {Colors} from "../../resource/Colors";
import FormInput from "../common/FormInput";
import Button from "../common/Button";
import {BookApi} from "../../api/app/BookApi";

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

const BookAdditionForm = () => {
  const [bookTitle, setBookTitle] = useState<string>('');

  const handleClick = () => {
    BookApi.postBook(bookTitle)
      .then(data => {
        alert('책 등록에 성공했습니다!')
        setBookTitle('')
      })
      .catch(error => {

      })
  }

  return (
    <Container>
      <Book />
      <Title>책 등록</Title>
      <FormInput title={'책 이름'} value={bookTitle} onChange={setBookTitle} />
      <Button label={'저장'} onClick={handleClick} />
    </Container>
  );
};

export default BookAdditionForm;