import React, {useState} from 'react';
import styled from "styled-components";
import {ReactComponent as User} from "../../resource/icon/user.svg";
import {Colors} from "../../resource/Colors";
import FormInput from "../common/FormInput";
import Button from "../common/Button";
import {UserApi} from "../../api/app/UserApi";
import * as Swal from '../../api/common/alert';
import { PAGE_SIZE } from '../../constants/pageSize';

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

    if (!name.trim() || !age.trim()) { // trim()을 사용하여 공백 제거
        Swal.alert('이름과 나이를 입력해 주세요.', '', 'warning');
        return;
    }

    // 이름 유효성 검사: 영어(알파벳) 또는 한글만 허용
    const nameRegex = /^[a-zA-Z가-힣]+[0-9]*$/;
    if (!nameRegex.test(name)) {
        Swal.alert('이름은 영어 또는 한글로 시작해야 하며, 숫자를 포함할 수 있습니다.\n단, 띄워쓰기는 사용할 수 없습니다.', '', 'warning');
        return;
    }

    if (isNaN(parseInt(age))) { // isNaN: 값이 숫자인지 아닌지를 확인하는 함수(숫자가 아닌 경우 true 반환)
        Swal.alert('나이는 숫자만 입력 가능합니다.', '','warning');
        return;
    }

    UserApi.postUser(name, isNaN(parseInt(age)) ? null : parseInt(age), PAGE_SIZE)
      .then(data => {
        Swal.alert('등록에 성공했습니다!', '', 'success');
        setName('');
        setAge('');
      })
      .catch(error => {
        // 백엔드에서 받은 에러 메시지를 화면에 표시
        const errorMessage = error?.data || error.message || '오류가 발생했습니다.';
        Swal.alert(errorMessage, '', 'error');
      })
  }

  return (
    <Container>
      <User />
      <Title>회원 등록</Title>
      <FormInput title={'이름'} value={name} onChange={setName} />
      <FormInput title={'나이'} value={age} onChange={setAge} />
      <Button label={'등록'} onClick={handleClick} />
    </Container>
  );
};

export default UserRegisterForm;