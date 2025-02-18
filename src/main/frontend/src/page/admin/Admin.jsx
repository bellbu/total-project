import React, { useContext } from 'react'
import styled from "styled-components";
import * as auth from '../../api/login/auth'
import AdminForm from '../../component/form/admin/AdminForm'
import { LoginContext } from '../../context/LoginContextProvider';
import { useNavigate } from 'react-router-dom'
import * as Swal from '../../api/common/alert';
import MainTopBar from "../../component/MainTopBar";

const Container = styled.div`
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
  min-width: 1440px;
`

const ContentsContainer = styled.div`
  width: 100%;
  height: calc(100% - 60px);
  box-sizing: border-box;
  overflow-y: auto;
`;

const Admin = ({ setTab }) => {
    const { adminInfo, setAdminInfo, loginCheck, logout } = useContext(LoginContext);
    const navigate = useNavigate();

    const updateAdmin = async (form) => {
        try {
            const response = await auth.update(form);
            const data = response.data;
            const status = response.status;

            if (status === 200) {
                // 로그인 체크 전에 관리자 정보 바로 반영
                setAdminInfo((prev) => ({ ...prev, ...form }));

                Swal.alert("관리자 정보 수정 성공", "", "success", async () => {
                                                                                await loginCheck();
                                                                                navigate('/main');
                                                                            });
            } else {
                Swal.alert("관리자 정보 수정에 실패하였습니다.", "", "error");
            }
        } catch (error) {
            const errorMessage = error.response.data || '오류가 발생했습니다.';
            Swal.alert(errorMessage, '', 'error');
            return;
        }
    }

  // 관리자  탈퇴
  const deleteAdmin = async (email) => {
    Swal.confirm("관리자 삭제", "정말로 삭제하시겠습니까?", "warning", async(result) => {
        if (result.isConfirmed) {
            try {
                const response = await auth.remove(email);
                const data = response.data;
                const status = response.status;

                if (status === 200 )  {
                    Swal.alert("관리자 삭제 성공", "", "success", () => { logout(true) });
                } else {
                    Swal.alert("관리자 삭제 실패하였습니다.", "", "error");
                }

            } catch (error) {
                const errorMessage = error.response.data || '오류가 발생했습니다.';
                Swal.alert(errorMessage, '', 'error');
                return;
            }
        }
    });
  }

    return (
        <Container>
            <ContentsContainer>
              <div className="container">
                <AdminForm adminInfo={adminInfo} updateAdmin={updateAdmin} deleteAdmin={deleteAdmin} />
              </div>
            </ContentsContainer>
        </Container>
    )
}

export default Admin