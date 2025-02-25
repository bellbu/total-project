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
  onUpdate: (userId: number, newName: string) => void;
  onClose: () => void;
}

const UserNameEditModal = ({userId, currentName, onUpdate, onClose}: Props) => {
  return createPortal(
      <UserNameEditModalContent
        userId={userId}
        currentName={currentName}
        onUpdate={onUpdate}
        onClose={onClose}
      />,
      document.getElementById('root')!
  );
};

const UserNameEditModalContent = ({userId, currentName, onUpdate, onClose}: Props) => {
  const [newName, setNewName] = useState<string>('')

  const edit = () => {
    UserApi.putUser(userId, newName)
      .then(() => {
        onUpdate(userId, newName)
        Swal.alert('이름이 성공적으로 수정되었습니다!', '', 'success')
        onClose()
      })
      .catch((error) => {
        const errorMessage = error?.data || '오류가 발생했습니다.'
        Swal.alert(errorMessage, '', 'error')
      })
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