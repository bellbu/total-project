import React from 'react'
import styles from './LoginForm.css';

const LoginForm = () => {

    const onLogin = () => {

    }

    return (
        <div className="form">
            <h2 className='login-title'>로그인</h2>

            <form className="login-form" onSubmit={(e) => onLogin}>
                <div>
                    <label htmlFor="email">이메일</label>
                    <input type="text" 
                           id='email' 
                           placeholder='이메일 입력' 
                           name='email' 
                           autoComplete='email' 
                           required 
                           // TODO: 아이디 저장 기능 구현 후 추가
                           // FIXME: rememberEmail를 넣어줘    
                           // defaultValue={}
                    />
                </div>
                <div>
                    <label htmlFor="password">비밀번호</label>
                    <input type="password"
                           id='password'
                           placeholder='비밀번호 입력'
                           name='password'
                           autoComplete='password'
                           required       
                    />
                </div>
                <button type='submit' className='btn btn--form btn-login'>로그인</button>
            </form>
            
        </div>
    )
}

export default LoginForm