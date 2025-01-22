import React, { createContext, useEffect, useState } from 'react';
import api from '../api/login/api';
import Cookies from 'js-cookie';
import * as auth from '../api/login/auth';
import { useNavigate } from 'react-router-dom'
import * as Swal from '../api/common/alert';

export const LoginContext = createContext(); // LoginContext라는 새로운 컨텍스트 객체 생성
                                             // LoginContextProvider를 통해 값을 전달, 자식 컴포넌트에서 useContext(LoginContext)를 사용해 값을 가져옴
LoginContext.displayName = 'LoginContextName' // displayName: 컨텍스트를 디버깅할 때 표시되는 이름 지정(기본적으로 Context로 표시)

/**
 * 로그인 상태(isLogin)를 관리하고, 관련된 로직(로그인, 로그아웃, 로그인 체크)을 제공하는 컨텍스트 제공자
 * 로그인 상태(isLogin)와 로그아웃 함수(logout)를 제공하는 컨텍스트 제공자
 * - 로그인 상태 체크(로그인 세팅, 로그아웃 세팅)
 * - 로그인
 * - 로그아웃
 */
const LoginContextProvider = ({children}) => {

    /**
     * 상태
     * - 로그인 상태(여부)
     * - 관리자 정보
     * - 권한 정보
     * - 이메일 저장 여부(쿠키)
     */

    /*----------------------------[State]--------------------------- */
    // 로그인 상태(여부)
    const [isLogin, setLogin] = useState(false); // 초기값 로그아웃 상태(false)

    // 관리자 정보
    const [adminInfo, setAdminInfo] = useState({});

    // 권한 정보
    const [authorities, setAuthorities] = useState({isUser : false, isAdmin : false});

    // 이메일 저장 여부
    const [rememberEmail, setRememberEmail] = useState();

    // 로그인 체크 로딩 상태
    const [isLoading, setIsLoading] = useState(true);
    /*-------------------------------------------------------------- */

    // 페이지 이동
    const navigate = useNavigate();

    /**
     * 로그인 상태(여부) 체크
     * - 쿠키에 jwt가 있는지 확인
     * - jwt로 사용자 정보를 요청
     * -
     */
    const loginCheck = async () => { // async (): 비동기 코드 실행
        setIsLoading(true); // 로그인 체크 로딩 시작
        
        const accessToken = Cookies.get("accessToken");

        if(!accessToken) {
            logoutSetting(); // JWT 토큰없으면 로그아웃 처리
            setIsLoading(false); // 로그인 체크 로딩 완료
            return;
        }

        /**
         *  accessToken(jwt)이 있는 경우 헤더에 jwt 담기
         * - axios 헤더 추가: api 요청 시 인증을 위함
         *   1) api.defaults.headers.common: axios 모든 요청 api에 공통으로 사용할 헤더 설정
         *   2) Authorization: Authorization 헤더 추가
         *   3) controller에서 파라미터에 AuthenticationPrincipal 어노테이션을 사용하면 JWT에서 사용자 정보를 자동으로 가져옴
         */
        api.defaults.headers.common.Authorization = `Bearer ${accessToken}`

        // 사용자 정보 요청
        let response
        let data

        try {
            response = await auth.info(); // auth.info(): api 호출하여 관리자 정보 조회, api 요청 헤더에 JWT 정보 담겨있음
            data = response.data;
            
            if(data === 'UNAUTHRIZED' || response.status === 401) {  // 요청 실패 시 로그 아웃 처리
                logoutSetting();
            } else { // 요청 성공 시 로그인 설정
                loginSetting(data, accessToken);
            }
        } catch (error) {
            console.log(`error : ${error}`);
            logoutSetting();
        } finally {
            setIsLoading(false); // 로그인 체크 로딩 완료
        }
    }

    // 로그인
    const login = async(email, password) => {

        try {
            const response = await auth.login(email, password);
            const data = response.data;
            const status = response.status;
            const headers = response.headers;
            const authorization = headers.authorization;
            const accessToken = authorization.replace("Bearer ", ""); // jwt

            // 로그인 성공
            if(status === 200) {
                // 쿠키에 accessToken(jwt) 저장
                Cookies.set("accessToken", accessToken);

                // 로그인 체크 (/users/{email} => userData)
                loginCheck();

                navigate("/main");

            }
        } catch (error) {
            // 로그인 실패
             Swal.alert(`로그인 실패`, "아이디 혹은 비밀번호가 맞지 않습니다.");
        }

    }

    // 로그아웃(기본값: force=false)
    const logout = (force=false) => {

        if( force ) { // force=true인 경우 강제 로그아웃(확인 창X) ex) 세션 만료, 보안 문제 등 사용자가 강제로 로그아웃되어야 하는 경우
            // 로그아웃 세팅
            logoutSetting();

            // 페이지 이동 ➡ "/" (메인)
            navigate("/");
            return
        }

        Swal.confirm("로그아웃하시겠습니까?", "로그아웃을 진행합니다.", "warning", // force=false인 경우 일반 로그아웃(확인 창O)
                (result) => {
                    if( result.isConfirmed ) {
                        // 로그아웃 세팅
                        logoutSetting();

                        // 메인 페이지로 이동
                        navigate("/");
                    }
                }
        )
    }

    // 로그인 세팅
    // adminData(관리자 정보), accessToken(jwt)
    const loginSetting = (adminData, accessToken) => {

        // 객체 구조 분해 할당
        const {id, email, name, authorities} = adminData;

        // 필요없음
        // const roleList = authorities.map((auth) => auth.authorities);

        // 필요없음
        // console.log(`roleList : ${roleList}`);

        // axios 헤더 추가 - Authorization : `Bearer ${accessToken}`
        api.defaults.headers.common.Authorization = `Bearer ${accessToken}`;

        // 로그인 여부 true 세팅
        setLogin(true);

        // 유저 정보 세팅 (name 포함)
        const updatedAdminInfo = {id, email, name, authorities};
        setAdminInfo(updatedAdminInfo);

        // 권한 정보 세팅
        const updatedAuthorities = {isUser : false, isAdmin : false};
        authorities.forEach((role) => {
            if(role === 'ROLE_USER') updatedAuthorities.isUser = true;
            if(role === 'ROLE_ADMIN') updatedAuthorities.isAdmin = true;
        });
        setAuthorities(updatedAuthorities);
    }


    // 로그아웃 세팅
    const logoutSetting = () => {

        // axios 헤더 초기화
        api.defaults.headers.common.Authorization = undefined;

        // 쿠키 초기화
        Cookies.remove("accessToken");

        // 로그인 여부 : false
        setLogin(false);

        // 유저 정보 초기화
        setAdminInfo(null);

        // 권한 정부 초기화
        setAuthorities(null);

    }


    // useEffect: React 컴포넌트가 렌더링 될 때마다 실행되는 Hook
    useEffect( () => {
        // 로그인 체크
        loginCheck();
    }, []);

    return (
        // LoginContext.Provider를 사용해 데이터와 함수를 Context에 전달
        // LoginContextProvider의 자식 컴포넌트들이 Context 값을 사용할 수 있음
        <LoginContext.Provider value={{ isLogin, adminInfo, authorities, login, loginCheck, logout, isLoading }}>
            {children}  
        </LoginContext.Provider>
    )
}

export default LoginContextProvider