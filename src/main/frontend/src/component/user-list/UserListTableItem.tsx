import React, {useState} from 'react';
import styled from "styled-components";
import {UserData} from "../../model/UserData";
import Button from "../common/Button";
import {UserApi} from "../../api/app/UserApi";
import UserNameEditModal from "./UserNameEditModal";

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
  refresh: () => void;
}

const UserListTableItem = ({ data, refresh }: Props) => {
  const [isEditModalOpen, setEditModalOpen] = useState(false)

  const deleteUser = () => {
    UserApi.deleteUser(data.name)
      .then(() => {
        refresh()
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
      {isEditModalOpen && <UserNameEditModal userId={data.id} currentName={data.name} refresh={refresh} onClose={() => setEditModalOpen(false)} />}
    </Container>
  );
};

export default UserListTableItem;