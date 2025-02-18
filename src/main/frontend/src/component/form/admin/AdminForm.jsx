import React from 'react'
import { useNavigate } from 'react-router-dom'
import './AdminForm.css';
import * as Swal from '../../../api/common/alert';

const AdminForm = ({ adminInfo, updateAdmin, deleteAdmin }) => {

  const navigate = useNavigate();
  const [isRoleOpen, setIsRoleOpen] = React.useState(false);
  const [selectedRole, setSelectedRole] = React.useState(adminInfo?.authorities.includes("ROLE_ADMIN") ? 'ROLE_ADMIN' : 'ROLE_USER');
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

  const onUpdate = (e) => {

      e.preventDefault(); // submit 기본 동작 방지

      const form = e.target;
      const email = form.email.value.trim();
      const name = form.name.value.trim();
      const password = form.password.value;
      const emailVerified = form.emailVerified.value;

      // 빈값 유효성 검사
      if(!email || !name || !password) {
        Swal.alert('모든 필드를 입력해 주세요.', '', 'warning')
        return;
      }

      // 권한 선택 검사
      if (!selectedRole) {
          Swal.alert("권한을 선택해주세요.", "", "warning");
          return;
      }

      updateAdmin({ email, name, password, emailVerified, authorities: selectedRole });
  }

  return (
    <div className="admin-form">
      <h2 className='admin-title'>관리자 정보 수정</h2>

        <form className="login-form" onSubmit={(e) => onUpdate(e)}>
          <div>
            <label htmlFor="email">이메일</label>
            <input type="text" 
                  id='email' 
                  placeholder='이메일 입력' 
                  name='email' 
                  autoComplete='email' 
                  readOnly
                  defaultValue={ adminInfo?.email }
                  className="readonly-input"
                  style={{ backgroundColor: 'lightgray' }}
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
                  defaultValue={ adminInfo?.name }
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

            {/* 이메일 인증여부 추후에 추가 */}
            <input
                type="hidden"
                name="emailVerified"
                value="true"
            />
        <button type='submit' className='btn btn--form btn-join'>정보 수정</button>
        <hr className='login-hr'/>
        <button type='button' className="btn btn--form btn-login" onClick={ () => deleteAdmin(adminInfo.email) }>관리자 탈퇴</button>
      </form>

    </div>
  )
}

export default AdminForm