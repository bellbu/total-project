import React, { useEffect, useState,  useCallback, useRef } from 'react';
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
  const [userList, setUserList] = useState<UserData[]>([]);
  const [isLoading, setIsLoading] = useState(false);
  const [page, setPage] = useState(0);
  const [hasMore, setHasMore] = useState(true);
  const observerRef = useRef<IntersectionObserver>();
  const loadingRef = useRef<HTMLDivElement>(null);

  const loadUsers = useCallback(async (pageNum: number) => {
    if (isLoading) return;

    setIsLoading(true);
    try {
      const data = await UserApi.getUser(pageNum, 1000); // 페이지당 20개로 수정
      if (data.length === 0) {
        setHasMore(false);
      } else {
        setUserList(prev => pageNum === 0 ? data : [...prev, ...data]);
        setPage(pageNum);
      }
    } catch (error) {
      console.error('Failed to load users:', error);
    } finally {
      setIsLoading(false);
    }
  }, [isLoading]);

  const handleRefresh = useCallback((updatedUser?: UserData) => {
    if (updatedUser) {
      setUserList(prev =>
        prev.map(user =>
          user.id === updatedUser.id ? updatedUser : user
        )
      );
    } else {
      setPage(0);
      loadUsers(0);
    }
  }, [loadUsers]);

  useEffect(() => {
    const observer = new IntersectionObserver(
      entries => {
        if (entries[0].isIntersecting && !isLoading && hasMore) {
          loadUsers(page + 1);
        }
      },
      { threshold: 0.3 }
    );
    observerRef.current = observer;
    return () => observer.disconnect();
  }, [loadUsers, isLoading, hasMore, page]);

  useEffect(() => {
    const currentObserver = observerRef.current;
    const currentLoadingRef = loadingRef.current;

    if (currentLoadingRef && currentObserver) {
      currentObserver.observe(currentLoadingRef);
    }

    return () => {
      if (currentLoadingRef && currentObserver) {
        currentObserver.unobserve(currentLoadingRef);
      }
    };
  }, [userList]);

  useEffect(() => {
    loadUsers(0);
  }, []);

  return (
    <Container>
      <UserListTableHeader userCount={userList.length} />
      {userList.map((item) => (
        <div key={item.id}>
          <UserListTableItem
            data={item}
            refresh={handleRefresh}
          />
        </div>
      ))}
      <div ref={loadingRef} style={{ textAlign: 'center', padding: '20px' }}>
        {isLoading ? '로딩 중...' : hasMore ? '' : '더 이상 데이터가 없습니다.'}
      </div>
    </Container>
  );
};

export default UserListPage;