import React, { useContext } from 'react'
import { LoginContext } from './LoginContextProvider'

// LoginContext를 소비하여 로그인 상태를 화면에 표시
// 로그인 여부를 텍스트(로그인, 로그아웃)로 출력
const LoginContextConsumer = () => {
    const {isLogin} = useContext(LoginContext)

    return (
    <div>
        <h3>로그인 여부 : {isLogin ? '로그인' : '로그아웃'}</h3>
    </div>
  )
}

export default LoginContextConsumer