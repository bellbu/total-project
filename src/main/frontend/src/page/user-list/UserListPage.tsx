import React, { useEffect, useState, useRef, useCallback } from 'react';
import styled from "styled-components";
import { UserData } from "../../model/UserData";
import { UserApi } from "../../api/app/UserApi";
import UserListTableHeader from "../../component/user-list/UserListTableHeader";
import UserListTableItem from "../../component/user-list/UserListTableItem";
import Lottie from "lottie-react";
import loadingAnimation from "../../resource/icon/loading.json";
import * as Swal from '../../api/common/alert';

const Container = styled.div`
  display: flex;
  flex-direction: column;
  margin: 20px 150px;
`;

const LoadingWrapper = styled.div`
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100px;
`;

const UserListPage = () => {
  const [userList, setUserList] = useState<UserData[]>([]); // userList: 조회 회원 목록 저장
  const [totalCount, setTotalCount] = useState(0); // totalCount: 전체 회원 수 저장
  const [page, setPage] = useState(0); // 현재 페이지 번호 저장
  const [hasMore, setHasMore] = useState(true); // 추가 테이터 조회 여부
  const [isLoading, setIsLoading] = useState(false); // 로딩 표시 여부
  const observerTarget = useRef<HTMLDivElement>(null);

  const loadUsers = useCallback(async () => {
    if (!hasMore || isLoading) return; // 로드할 데이터가 없거나 데이터 로딩 중인 경우 => 회원 조회 X

    try {
      setIsLoading(true); // isLoading이 true인 경우 => 회원 조회 O
      const data = await UserApi.getUser(page); // 현재 페이지 값으로 회원 조회 api 호출
      setUserList(prev => [...prev, ...data]); // prev(기존의 userList)와 data(새로운 회원 목록) 배열 병합
      setHasMore(data.length === 1000); // 데이터 1000개씩 조회하므로 1000미만인 경우 불러올 데이터 없다고 판단하여 false로 세팅
      setPage(prev => prev + 1); // 페이지 1 증가
    } catch (error) {
      console.error('Failed to load users:', error);
      Swal.alert(error, '', 'error');
    } finally {
      setIsLoading(false); // isLoading이 false인 경우 => 회원 조회 X
    }
  }, [page, hasMore, isLoading]);

  useEffect(() => {
    // observer(IntersectionObserver 객체) 선언
    const observer = new IntersectionObserver( // IntersectionObserver: 특정 요소(observerTarget)가 뷰포트(화면)에 나타나는 감지하는 API
      entries => { // entries: IntersectionObserver가 감지한 요소들의 정보가 담긴 배열
        if (entries[0].isIntersecting) { // entries[0]: 감지된 첫 번째 요소의 정보, isIntersecting: 요소가 뷰포트 안으로 들어오면 true, 나가면 false
          loadUsers();
        }
      },
      { threshold: 0.1, rootMargin: '100px' } // threshold: 0.1: 10% 이상 요소가 보일 때 감지, rootMargin: '100px': 뷰포트(화면)보다 100px 더 바깥 영역까지 감지하여 더 일찍 loadUsers() 실행시킴
    );

    if (observerTarget.current) {
      // observer 실행
      observer.observe(observerTarget.current); // observe(): 실행 메서드로 특정 요소 자동 감지 시작
    }

    // return(): 클린업 함수 - "useEffect 실행 전" 또는 "컴포넌트 언마운트 시" 실행
    return () => observer.disconnect(); // disconnect(): 감지 해제
  }, [loadUsers]);

  useEffect(() => {
    const fetchTotalCount = async () => {
      try {
        const count = await UserApi.getUserCount();
        setTotalCount(count);
      } catch (error) {
        console.error('Failed to load total count:', error);
        Swal.alert(error, '', 'error');
      }
    };
    fetchTotalCount();
  }, []);

  // 회원 수정
  const handleUserUpdate = (userId: number, newName: string) => {
    setUserList(prev =>
      prev.map(user =>
        user.id === userId ? { ...user, name: newName } : user // id가 일치한 경우 user 객체의 복사본(...user)에  name속성을 newName으로 덮어쓰기(name: newName)
      )
    );
  };

  // 회원 삭제
  const handleUserDelete = (userName: string) => {
    setUserList(prev => prev.filter(user => user.name !== userName)); // filter()를 사용해 user.name이 userName과 일치하지 않는 유저들만 남김
    setTotalCount(prev => Math.max(0, prev - 1)); // 삭제 시 총 회원수 -1 감소
  };

  return (
    <Container>
      <UserListTableHeader userCount={totalCount} searchedCount={userList.length} />
      {userList.map(item => (
        <UserListTableItem
          key={item.id}
          data={item}
          onUpdate={handleUserUpdate}
          onDelete={handleUserDelete}
        />
      ))}

      {/* 로딩 중일 때 Lottie 애니메이션 표시 */}
      {isLoading && (
        <LoadingWrapper>
          <Lottie animationData={loadingAnimation} loop={true} style={{ width: 200, height: 200 }} />
        </LoadingWrapper>
      )}

      <div ref={observerTarget} style={{ height: '10px' }} />
    </Container>
  );
};

export default UserListPage;