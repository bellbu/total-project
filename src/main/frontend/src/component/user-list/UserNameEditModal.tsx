import React, {useState} from 'react';
import styled from "styled-components";
import {createPortal} from "react-dom";
import FormInput from "../common/FormInput";
import Button from "../common/Button";
import {UserApi} from "../../api/app/UserApi";
import * as Swal from '../../api/common/alert';

const Container = styled.div`
  position: absolute;
  width: 100%;
  height: 100%;
  left: 0;
  top: 0;
  display: flex;
  justify-content: center;
  align-items: center;
`;

const Background = styled.div`
  position: absolute;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.56);
`;

const Content = styled.div`
  width: 480px;
  height: 240px;
  background-color: white;
  padding: 20px;

  display: flex;
  flex-direction: column;
  align-items: center;
  
  z-index: 1;
`;

const Title = styled.p`
  font-size: 24px;
  font-weight: 800;
  color: black;
  margin-bottom: 16px;
`;

interface Props {
  userId: number;
  currentName: string;
  refresh: () => void;
  onClose: () => void;
}

const UserNameEditModal = ({userId, currentName, refresh, onClose}: Props) => {
  return createPortal(
      <UserNameEditModalContent
        userId={userId}
        currentName={currentName}
        refresh={refresh}
        onClose={onClose}
      />,
      document.getElementById('root')!
  );
};

const UserNameEditModalContent = ({userId, currentName, refresh, onClose}: Props) => {
  const [newName, setNewName] = useState<string>('')

  const edit = () => {
/*

    if (!newName.trim()) {
        Swal.alert('새 이름을 입력해 주세요.');
        return;
    }

    // 정규식: 영어 또는 한글로 시작하고, 뒤에 숫자가 올 수 있는 패턴만 허용
    const nameRegex = /^[a-zA-Z가-힣]+[0-9]*$/;
    if (!nameRegex.test(newName)) {
        Swal.alert('이름은 영어 또는 한글로 시작하고, \n뒤에 숫자를 입력할 수 있습니다.');
        return;
    }
 */

    UserApi.putUser(userId, newName)
        .then(() => {
            Swal.alert('이름이 성공적으로 수정되었습니다!');
            onClose()
            refresh()
        })
        .catch((error) => {
            // 백엔드에서 받은 에러 메시지를 화면에 표시
            const errorMessage = error?.data || '오류가 발생했습니다.';
            Swal.alert(errorMessage);
        });
  }

  return (
    <Container>
      <Background onClick={onClose}/>
      <Content>
        <Title>회원 정보 수정하기</Title>
        <div style={{display: 'flex', flexDirection: 'row', gap: '8px', marginBottom: '8px'}}>
          <p style={{fontSize: '15px', fontWeight: '400'}}>현재 이름</p>
          <p style={{fontSize: '15px', fontWeight: '600'}}>{currentName}</p>
        </div>
        <FormInput title={'새 이름'} value={newName} onChange={setNewName} width={'230px'} fontSize={'16px'} />
        <Button label={'수정'} onClick={edit} marginTop={'8px'}/>
      </Content>
    </Container>
  );
};

export default UserNameEditModal;