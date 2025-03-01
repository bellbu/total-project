import React, {useState} from 'react';
import styled from "styled-components";
import {UserData} from "../../model/UserData";
import Button from "../common/Button";
import {UserApi} from "../../api/app/UserApi";
import UserNameEditModal from "./UserNameEditModal";
import * as Swal from "../../api/common/alert";
import { PAGE_SIZE } from '../../constants/pageSize';

const Container = styled.div`
  position: relative;

  width: 100%;
  height: 85px;
  display: flex;
  flex-direction: row;
  align-items: center;
  border-bottom: 1px solid black;
`;

const NameBox = styled.div`
  width: 20%;
  padding-left: 20px;
  overflow-x: hidden;
`

const AgeBox = styled.div`
  position: absolute;
  left: 22%;
  width: fit-content;
`

const Text = styled.p`
  font-size: 18px;
  font-weight: 700;
  color: black;
`;

const ButtonsArea = styled.div`
  height: 100%;
  display: flex;
  flex-direction: row;
  margin-left: auto;
  align-items: center;
  gap: 4%;
`;

interface Props {
  data: UserData
  onUpdate: (userId: number, newName: string) => void
  onDelete: (userName: string) => void
}

const UserListTableItem = ({ data, onUpdate, onDelete }: Props) => {
  const [isEditModalOpen, setEditModalOpen] = useState(false)

  const deleteUser = () => {
    Swal.confirm("회원 삭제", "정말로 삭제하시겠습니까?", "warning", (result: any) => {
      if (result.isConfirmed) {
        UserApi.deleteUser(data.name, PAGE_SIZE)
          .then(() => {
            onDelete(data.name)
            Swal.alert("삭제 완료", "사용자가 삭제되었습니다.", "success")
          })
          .catch((error) => {
            const errorMessage = error?.data || error.message || '오류가 발생했습니다.'
            Swal.alert(errorMessage, '', 'error')
          })
      }
    })
  }

  return (
    <Container>
      <NameBox>
        <Text>{data.name}</Text>
      </NameBox>
      <AgeBox>
        <Text>{data.age ?? '00'}세</Text>
      </AgeBox>
      <ButtonsArea>
        <Button label={'수정'} onClick={() => setEditModalOpen(true)} marginTop={'0'}/>
        <Button label={'삭제'} onClick={() => deleteUser()} marginTop={'0'}/>
      </ButtonsArea>
      {isEditModalOpen && (
        <UserNameEditModal
          userId={data.id}
          currentName={data.name}
          onUpdate={onUpdate}
          onClose={() => setEditModalOpen(false)}
        />
      )}
    </Container>
  );
};

export default UserListTableItem;