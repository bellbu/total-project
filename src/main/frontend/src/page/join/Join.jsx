import React from 'react'
import LoginContextConsumer from '../../context/LoginContextConsumer'
import JoinForm from '../../component/form/join/JoinForm'
import * as auth from '../../api/login/auth'
import { useNavigate } from 'react-router-dom'
import * as Swal from '../../api/common/alert';

const Join = () => {

  const navigate = useNavigate(); // useNavigate: React Router에서 제공하는 페이지 이동 훅

  // 관리자 가입 요청
  const join = async (form) => {  // form: 사용자 입력 데이터 객체  ex) { email, name, password }
    console.log(form);

    let response; // 회원가입 API 응답 객체
    let data;     // 회원가입 API 응답 데이터: SUCCESS or FAIL

    try {
        response = await auth.join(form);  // 회원가입 요청(POST 요청)
    } catch (error) {
        const errorMessage = error.response.data;
        Swal.alert(errorMessage);
        return; // 오류 발생 시 함수 종료
    }

    data = response.data; // 응답 데이터 추출
    const status = response.status; // HTTP 상태 코드 추출

    if(status === 200) {
        Swal.alert(`회원가입 성공!`);
        navigate("/login"); // 회원가입 성공 시 로그인 페이지 이동
    } else {
        Swal.alert(`회원가입에 실패하였습니다.`)
    }

  }

  return (
    // Header와 회원가입 폼(JoinForm)을 포함한 컨테이너를 렌더링
    // join={join}: {join} 함수를 join 이름으로 JoinForm에 전달
    <>
        <div className="container">
          <JoinForm join={join} />
        </div>
    </>
  )

}

export default Join