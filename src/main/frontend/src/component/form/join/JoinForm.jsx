import React from 'react'
import { useNavigate } from 'react-router-dom'

const JoinForm = ({ join }) => {

  const navigate = useNavigate();

  const onJoin = (e) => {
      e.preventDefault(); // submit 기본 동작 방지
      const form = e.target;
      const email = form.email.value;
      const name = form.name.value;
      const password = form.password.value;
      const emailVerified = form.emailVerified.value;

      console.log(email, name, password, emailVerified);

      join( {email, name, password, emailVerified} ); // Join 컴포넌트의 join 함수를 호출하여 회원가입 처리
  }

  return (
    <div className="form">
      <h2 className='login-title'>회원가입</h2>

      <form className="login-form" onSubmit={(e) => onJoin(e)}>
          <div>
              <label htmlFor="email">이메일</label>
              <input type="text" 
                    id='email' 
                    placeholder='이메일 입력' 
                    name='email' 
                    autoComplete='email' 
                    required 
              />
          </div>

          <div>
            <label htmlFor="name">이름</label>
                <input type="text" 
                      id='name'
                      placeholder='이름 입력' 
                      name='name'
                      autoComplete='name'
                      required 
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
            {/* 이메일 인증여부 추후에 추가 */}
            <input
                type="hidden"
                name="emailVerified"
                value="true"
            />
          <div className="button-group">
            <button type='submit' className='btn btn--form btn-join'>가입</button>
            <button type='button' className='btn btn--form btn-cancel' onClick={() => navigate("/")}>취소</button>
          </div>
      </form>

    </div>
  )
}

export default JoinForm