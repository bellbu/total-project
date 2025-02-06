import React from 'react'
import { useNavigate } from 'react-router-dom'
import '../login/LoginForm.css';
import * as Swal from '../../../api/common/alert';

const JoinForm = ({ join }) => {

  const navigate = useNavigate();
  const [isRoleOpen, setIsRoleOpen] = React.useState(false);
  const [selectedRole, setSelectedRole] = React.useState('ROLE_ADMIN');
  const roleRef = React.useRef(null);

  React.useEffect(() => {
    const handleClickOutside = (event) => {
      if (roleRef.current && !roleRef.current.contains(event.target)) {
        setIsRoleOpen(false);
      }
    };

    document.addEventListener('mousedown', handleClickOutside);
    return () => document.removeEventListener('mousedown', handleClickOutside);
  }, []);

  const onJoin = (e) => {

      e.preventDefault(); // submit 기본 동작 방지

      const form = e.target;
      const email = form.email.value.trim();
      const name = form.name.value.trim();
      const password = form.password.value;
      const emailVerified = form.emailVerified.value;

      // 이메일 정규식 검사
      const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
      if (!emailRegex.test(email)) {
            Swal.alert("올바른 이메일 형식을 입력해주세요.");
            return;
      }

      // 빈값 유효성 검사
      if(!email || !name || !password) {
        Swal.alert('모든 필드를 입력해 주세요.')
        return;
      }

      // 권한 선택 검사
      if (!selectedRole) {
          Swal.alert("권한을 선택해주세요.");
          return;
      }

      join({ email, name, password, emailVerified, role: selectedRole });
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

            <div className="custom-select" ref={roleRef}>
                <label htmlFor="role">권한</label>

                <div
                    className={`select-selected ${isRoleOpen ? 'select-arrow-active' : ''}`}
                    onClick={() => setIsRoleOpen(!isRoleOpen)}
                >
                    {selectedRole === 'ROLE_ADMIN' ? '관리자' : '부관리자'}
                </div>

                {isRoleOpen && (
                    <div className="select-items">
                        <div
                            className={selectedRole === 'ROLE_ADMIN' ? 'same-as-selected' : ''}
                            onClick={() => {
                                setSelectedRole('ROLE_ADMIN');
                                setIsRoleOpen(false);
                            }}
                        >
                            관리자
                        </div>

                        <div
                            className={selectedRole === 'ROLE_USER' ? 'same-as-selected' : ''}
                            onClick={() => {
                                setSelectedRole('ROLE_USER');
                                setIsRoleOpen(false);
                            }}
                        >
                            부관리자
                        </div>
                    </div>
                )}
            </div>

            <input
                type="hidden"
                name="emailVerified"
                value="true"
            />
          <div className="button-group">
            <button type='submit' className='btn btn--form btn-join'>가입</button>
            <button 
              type='button' 
              className='btn btn--form btn-cancel' 
              onClick={() => navigate("/login")}
            >
              취소
            </button>
          </div>
      </form>

    </div>
  )
}

export default JoinForm