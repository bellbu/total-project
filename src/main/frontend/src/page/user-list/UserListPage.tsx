import React, { useEffect, useState, useRef, useCallback } from 'react';
import styled from "styled-components";
import { UserData } from "../../model/UserData";
import { UserApi } from "../../api/app/UserApi";
import UserListTableHeader from "../../component/user-list/UserListTableHeader";
import UserListTableItem from "../../component/user-list/UserListTableItem";
import Lottie from "lottie-react";
import loadingAnimation from "../../resource/icon/loading.json";
import * as Swal from '../../api/common/alert';
import { PAGE_SIZE } from '../../constants/pageSize';

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

// 캐시 상태 배지 CSS
const CacheStatusBadge = styled.div<{ visible: boolean; type: 'HIT' | 'MISS' }>`
  position: fixed;
  right: 20px;
  bottom: 20px;
  padding: 14px 24px;
  background-color: ${(props) => (props.type === 'HIT' ? '#28a745' : 'firebrick')};
  color: #fff;
  border-radius: 12px;
  font-size: 18px;
  font-weight: bold;
  z-index: 9999;
  white-space: nowrap;
  display: flex;
  align-items: center;
  gap: 12px;
  box-shadow: 0 3px 8px rgba(0, 0, 0, 0.25);
  opacity: ${(props) => (props.visible ? 1 : 0)};
  transform: ${(props) => (props.visible ? 'translateY(0)' : 'translateY(10px)')};
  transition: all 0.4s ease;
`;

const UserListPage = () => {
  const [userList, setUserList] = useState<UserData[]>([]); // userList: 조회 회원 목록 저장
  const [totalCount, setTotalCount] = useState(0); // totalCount: 전체 회원 수 저장
  const [cursor, setCursor] = useState<number | null>(null); // cursor: 마지막 회원의 id
  const [hasMore, setHasMore] = useState(true); // 추가 테이터 조회 여부
  const [isLoading, setIsLoading] = useState(false); // 로딩 표시 여부
  const observerTarget = useRef<HTMLDivElement>(null); // 무한 스크롤 감지 대상 요소
  const [cacheStatus, setCacheStatus] = useState<{ type: 'HIT' | 'MISS', message: string, speed: string } | null>(null); // 캐시 상태 관리(HIT/MISS/조회 속도)
  const [ttlSeconds, setTtlSeconds] = useState<number | null>(null); // 현재 캐시 TTL(유효시간) 관리
  const [cacheVisible, setCacheVisible] = useState(false); // 캐시 상태 배지 표시 여부
  const cacheTimeoutRef = useRef<number | null>(null); // 캐시 배지 표시 시간 타이머

  // TTL 카운트다운
  useEffect(() => {
    if (ttlSeconds === null || ttlSeconds <= 0) return; // TTL 값이 없거나 0 이하인 경우 타이머 실행 안함

    const interval = setInterval(() => { // setInterval(code, 1000): 타이머 큐에 등록되어 1초마다 실행
        setTtlSeconds(prev => {
          if (prev !== null) {
            if (prev <= 1) { // ttl 값 1 이하
              clearInterval(interval); // 타이머 종료
              setCacheStatus({ type: 'MISS', message: '캐시 만료', speed: '-' }); // 캐시 상태 변경
              return 0;
            }
            return prev - 1; // ttl 값 1 이상인 경우 -1씩 카운트 다운
          }
          return null;
        });
    }, 1000);

    return () => clearInterval(interval); // 클린업 함수
  }, [ttlSeconds]);

  const loadUsers = useCallback(async () => {
    if (!hasMore || isLoading) return; // 불러올 데이터가 없거나 데이터 로딩 중인 경우 함수 종료

    try {
      setIsLoading(true); // isLoading이 true인 경우 => 로딩 중(회원 조회 중)

      const response = await UserApi.getUser(cursor, PAGE_SIZE); // 회원 조회 API
      // 응답 데이터 추출
      const data = response.data; // 회원 리스트
      // 응답 헤더 값 추출
      const ttlHeader = response.headers['x-ttl']; // 캐시 TTL 값
      const cacheHeader = response.headers['x-cache']; // 캐시 HIT/MISS 여부
      const responseTime = response.headers['x-response-time']; // 응답 속도

      // 기존에 보이던 캐시 상태 배지 숨기기
      setCacheVisible(false);

      // 0.2초 뒤에 다시 표시
      setTimeout(() => {

        // 조회된 캐시 상태 관리
        if (cacheHeader === 'HIT') {
            const ttl = parseInt(ttlHeader, 10); // parseInt('300', 10): 문자열에서 10진수 정수로 변환
            setTtlSeconds(ttl); // TTL 카운트 다운 작동
            // 캐시 상태값 변경
            setCacheStatus({
                type: 'HIT',
                message: `✅ 캐시 HIT `,
                speed: responseTime
            });
        } else {
            setCacheStatus({
                type: 'MISS',
                message: `❌ 캐시 MISS `,
                speed: responseTime
            });
        }

        // 캐시 상태 배지 보이기
        setCacheVisible(true);

        // 기존 설정한 타이머 취소
        if (cacheTimeoutRef.current) {
            clearTimeout(cacheTimeoutRef.current);
        }

        // 새로운 타이머 설정
        cacheTimeoutRef.current = window.setTimeout(() => {
            setCacheVisible(false); // 캐시 상태 배지 숨기기
            cacheTimeoutRef.current = null;
        }, 4000);

      }, 200);

      setUserList(prev => [...prev, ...data]); // prev(기존의 userList)와 data(새로운 회원 목록) 배열 병합

      if(data.length > 0) {
          // 마지막 요소의 회원 id를 cursor 값으로 설정
          setCursor(data[data.length -1].id);
          setHasMore(true);
      } else {
          setHasMore(false);
      }

    } catch (error) {
      console.error('Failed to load users:', error);
      Swal.alert(error, '', 'error');
    } finally {
      setIsLoading(false); // isLoading이 false인 경우 => 회원 조회 X
    }
  }, [cursor, hasMore, isLoading]);

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
          pageSize={PAGE_SIZE}
        />
      ))}

      {/* 캐시 상태 배지 */}
      <CacheStatusBadge visible={cacheVisible} type={cacheStatus?.type || 'MISS'}>
        {cacheStatus ? `${cacheStatus.message} - 조회 속도: ${cacheStatus.speed} ${cacheStatus.type === 'HIT' && ttlSeconds !== null ? `, TTL: ${ttlSeconds}초` : ''}` : ''}
      </CacheStatusBadge>

      {/* 로딩 중일 때 Lottie 애니메이션 표시 */}
      {isLoading && (
        <LoadingWrapper>
          <Lottie animationData={loadingAnimation} loop={true} style={{ width: 200, height: 200 }} />
        </LoadingWrapper>
      )}

      <div ref={observerTarget} style={{ height: '100px' }} />
    </Container>
  );
};

export default UserListPage;