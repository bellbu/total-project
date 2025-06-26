import React, { useContext } from 'react'
import { useNavigate } from 'react-router-dom'; // 추가
import './LoginForm.css';
import { LoginContext } from '../../../context/LoginContextProvider'

const LoginForm = () => {

    const navigate = useNavigate();

    const {login} = useContext(LoginContext); // LoginContextProvider에서 생성된 LoginContext의 login 함수 사용

    const onLogin = (e) => { // 사용자가 form을 submit하면 onLogin 함수 호출
        e.preventDefault(); //  기본 이벤트 실행 방지 ※ <submit>태그의 경우 해당 페이지 새로고침 방지, <a>태그의 경우 링크 페이지 이동 방지

        const form = e.target; // 이벤트 객체 e를 통해 form의 이메일과 비밀번호 값을 가져옴
        const email = form.email.value;
        const password = form.password.value;

        // 로그인 요청: LoginContext의 login 함수 호출
        login(email, password);
    }

    return (
        <div className="form">
            <h2 className='login-title2'>🔒 관리자 로그인</h2>

            <form className="login-form login-form2" onSubmit={(e) => onLogin(e)}>
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
                <button type='submit' className='btn btn--form btn-login btn-login2'>로그인</button>
                <hr className='login-hr2'/>
                <div className="login-autologin-guide">
                  ⚠️ 자동 로그인은 이메일·비밀번호 입력 없이 로그인됩니다.
                </div>
                <div className="button-group">
                    <button type='button' className='btn btn--form btn-login btn-login2 btn-login--admin' onClick={() => login('autoAdmin', '')}>관리자 자동 로그인</button>
                    <button type='button' className='btn btn--form btn-login btn-login2 btn-login--subadmin' onClick={() => login('autoSubAdmin','')}>부관리자 자동 로그인</button>
                </div>
            </form>
            
        </div>
    )
}

export default LoginForm