import React, {useEffect, useState} from 'react';
import styled from "styled-components";
import {UserData} from "../../model/UserData";
import {UserApi} from "../../api/app/UserApi";
import UserListTableHeader from "../../component/user-list/UserListTableHeader";
import UserListTableItem from "../../component/user-list/UserListTableItem";

const Container = styled.div`
  display: flex;
  flex-direction: column;
  margin: 20px 150px;
`;

const UserListPage = () => {
  const [userList, setUserList] = useState<UserData[]>([])

  useEffect(() => {
    refresh()
  }, [])

  const refresh = () => {
    UserApi.getUser()
      .then(data => {
        setUserList(data)
      })
  }

  return (
    <Container>
      <UserListTableHeader />
      {userList.map(item => {
        return <UserListTableItem data={{id: item.id, name: item.name, age: item.age}} refresh={refresh} />
      })}
    </Container>
  );
};

export default UserListPage;