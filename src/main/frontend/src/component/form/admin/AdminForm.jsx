import React, { useEffect, useState, useRef } from 'react'
import { useNavigate } from 'react-router-dom'
import './AdminForm.css';
import * as Swal from '../../../api/common/alert';

const AdminForm = ({ adminInfo, updateAdmin, deleteAdmin }) => {

  const [isRoleOpen, setIsRoleOpen] = useState(false); // isRoleOpen: 권한 선택 드롭다운이 열려 있는지 여부 (true: 열림/false: 닫힘)
  const [selectedRole, setSelectedRole] = useState(adminInfo?.authorities.includes("ROLE_ADMIN") ? 'ROLE_ADMIN' : 'ROLE_USER');
  const roleRef = useRef(null); // 드롭다운 외부 클릭 시 닫기 위한 ref

  useEffect(() => {
    const handleClickOutside = (event) => {
      if (roleRef.current && !roleRef.current.contains(event.target)) { // 사용자가 클릭한 요소(event.target)가 roleRef(드롭다운 영역) 내부에 있는지 확인
        setIsRoleOpen(false); // 드롭다운 영역 바깥을 클릭하면 드롭다운을 닫음
      }
    };

    document.addEventListener('mousedown', handleClickOutside); // 마우스 클릭 시 handleClickOutside 함수 호출 이벤트 추가
    return () => document.removeEventListener('mousedown', handleClickOutside); // 언마운트될 때(unmount) 이벤트 제거
  }, []);

  const onUpdate = (e) => {

      e.preventDefault(); // submit 기본 동작 방지

      const form = e.target;
      const email = form.email.value;
      const name = form.name.value;
      const password = form.password.value;
      const emailVerified = form.emailVerified.value;

      // 빈값 유효성 검사
      if(!email || !name || !password) {
        Swal.alert('모든 필드를 입력해 주세요.', '', 'warning')
        return;
      }

      const nameRegex = /^[가-힣a-zA-Z]+$/;
      if (!nameRegex.test(name)) {
          Swal.alert("이름은 한글 또는 영문만 입력 가능하며, \n특수문자나 띄어쓰기는 사용할 수 없습니다.", "", "warning");
          return;
      }

      // 권한 선택 검사
      if (!selectedRole) {
          Swal.alert("권한을 선택해주세요.", "", "warning");
          return;
      }

      updateAdmin({ email: adminInfo.email, name, password, emailVerified, authorities: selectedRole });
  }

  return (
    <div className="admin-form">
      <h2 className='admin-title'>🛠️ 관리자 정보 수정</h2>

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

          <div className="custom-select" ref={roleRef} aria-labelledby="role-label">
            <label id="role-label">권한</label>

            <div
              className={`select-selected ${isRoleOpen ? 'select-arrow-active' : ''}`}
              onClick={() => setIsRoleOpen(!isRoleOpen)}
            >
              {selectedRole === 'ROLE_ADMIN' ? '관리자' : '부관리자'}
            </div>

            {isRoleOpen && (  // isRoleOpen이 true인 경우 오른쪽 코드 렌더링 false인 경우 렌더링 되지 않음

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