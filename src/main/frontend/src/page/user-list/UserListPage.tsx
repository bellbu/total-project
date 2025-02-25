import React, { useEffect, useState, useRef, useCallback } from 'react';
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
  const [page, setPage] = useState(0)
  const [hasMore, setHasMore] = useState(true)
  const observerTarget = useRef<HTMLDivElement>(null)

  const loadUsers = useCallback(() => {
    if (!hasMore) return

    UserApi.getUser(page)
      .then(data => {
        console.log(data);
        setUserList(prev => [...prev, ...data])
        setHasMore(data.length === 10)
        setPage(prev => prev + 1)
      })
  }, [page, hasMore])

  useEffect(() => {
    const observer = new IntersectionObserver(
      entries => {
        if (entries[0].isIntersecting) {
          loadUsers()
        }
      },
      { threshold: 0.5 }
    )

    if (observerTarget.current) {
      observer.observe(observerTarget.current)
    }

    return () => observer.disconnect()
  }, [loadUsers])

  const handleUserUpdate = (userId: number, newName: string) => {
    setUserList(prev =>
      prev.map(user =>
        user.id === userId ? { ...user, name: newName } : user
      )
    )
  }

  const handleUserDelete = (userName: string) => {
    setUserList(prev => prev.filter(user => user.name !== userName))
  }

  return (
    <Container>
      <UserListTableHeader userCount={userList.length} />
      {userList.map(item => (
        <UserListTableItem
          key={item.id}
          data={item}
          onUpdate={handleUserUpdate}
          onDelete={handleUserDelete}
        />
      ))}
      <div ref={observerTarget} style={{ height: '10px' }} />
    </Container>
  );
};

export default UserListPage;