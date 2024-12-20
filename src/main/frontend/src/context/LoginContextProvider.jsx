import React, { createContext, useEffect, useState } from 'react';
import api from '../api/login/api';
import Cookies from 'js-cookie';

export const LoginContext = createContext(); // 새로운 Context 생성하여 로그인 상태(isLogin)와 로그아웃 함수(logout)를 전달 
                                             // LoginContext.Provider를 통해 값을 전달, 자식 컴포넌트에서 useContext(LoginContext)를 사용해 값을 가져옴
LoginContext.displayName = 'LoginContextName' // displayName: 컨텍스트를 디버깅할 때 표시되는 이름 지정(기본적으로 Context로 표시)

/**       
 * 로그인 상태(isLogin)와 로그아웃 함수(logout)를 제공하는 컨텍스트 제공자 
 * - 로그인 체크
 * - 로그인
 * - 로그 아웃
 * 
 * 로그인 세팅
 * 로그아웃 세팅
 */
const LoginContextProvider = ({children}) => {
    
    /**
     * 상태
     * - 로그인 여부 
     * - 유저 정보
     * - 권한 정보
     * - 아이디 저장 여부(쿠키)
     */

    // 로그인 여부
    const [isLogin, setLogin] = useState(false); // 초기 로그아웃 상태(false)

    // 관리자 정보
    const [adminInfo, setAdminInfo] = useState({});

    // 권한 정보
    const [authorities, setAuthorities] = useState({isUser : false, isAdmin : false});

    // 이메일 저장
    const [rememberEmail, setRememberEmail] = useState();


    // 로그인 세팅
    // adminData(관리자 정보), accessToken(jwt)
    const loginSetting = (adminData, accessToken) => {
        
        const {id, email, authorities} = adminData;
        const roleList = authorities.map((auth) => auth.authorities);
        
        console.log(`no : ${id}`);
        console.log(`email : ${email}`);
        console.log(`authorities : ${authorities}`);
        console.log(`roleList : ${roleList}`);
        
        // axios 객체의 header - Authorization : `Bearer ${accessToken}`
        api.defaults.headers.common.Authorization = `Bearer ${accessToken}`;

        // 쿠키에 accessToken(jwt) 저장 
        Cookies.set("accessToken", accessToken);
        
        // 로그인 여부 true 세팅
        setLogin(true);

        // 유저 정보 세팅
        const updatedAdminInfo = {id, email, roleList};
        setAdminInfo(updatedAdminInfo);

        // 권한 정보 세팅
        const updatedAuthorities = {isUser : false, isAdmin : false};
        
        roleList.forEach((role) => {
            if(role == 'ROLE_USER') updatedAuthorities.isUser = true;
            if(role == 'ROLE_ADMIN') updatedAuthorities.isAdmin = true;
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




    const logout = () => {
        setLogin(false)
    }

    // useEffect를 사용해 3초 뒤에 로그인 상태로 변경
    useEffect( () => {
     
    }, [])

    return (
        // LoginContext.Provider를 사용해 value 속성 값을 Context에 전달
        // LoginContextProvider의 자식 컴포넌트들이 Context 값을 사용할 수 있음
        <LoginContext.Provider value={ {isLogin, logout} }> 
            {children}  
        </LoginContext.Provider>
    )
}

export default LoginContextProvider