import React, {useState} from 'react';
import styled from "styled-components";
import {ReactComponent as User} from "../../resource/icon/user.svg";
import {Colors} from "../../resource/Colors";
import FormInput from "../common/FormInput";
import Button from "../common/Button";
import {UserApi} from "../../api/app/UserApi";

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

const UserRegisterForm = () => {
  const [name, setName] = useState<string>('');
  const [age, setAge] = useState<string>('');

  const handleClick = () => {
    UserApi.postUser(name, isNaN(parseInt(age)) ? null : parseInt(age))
      .then(data => {
        alert('등록에 성공했습니다!')
        setName('')
        setAge('')
      })
      .catch(error => {
        console.log("Error Occur")
      })
  }

  return (
    <Container>
      <User />
      <Title>사용자 등록</Title>
      <FormInput title={'이름'} value={name} onChange={setName} />
      <FormInput title={'나이'} value={age} onChange={setAge} />
      <Button label={'저장'} onClick={handleClick} />
    </Container>
  );
};

export default UserRegisterForm;