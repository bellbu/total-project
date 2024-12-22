import React from 'react'

const JoinForm = () => {
  const onJoin = () => {

  }
  return (
    <div className="form">
      <h2 className='login-title'>회원가입</h2>

      <form className="login-form" onSubmit={(e) => onJoin}>
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
            <label htmlFor="Name">이름</label>
                <input type="text" 
                      id='Name' 
                      placeholder='이름 입력' 
                      name='Name' 
                      autoComplete='Name' 
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
          <button type='submit' className='btn btn--form btn-join'>가입</button>
      </form>

    </div>
  )
}

export default JoinForm