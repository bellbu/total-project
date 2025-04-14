import axios from "axios";
import * as Swal from '../../api/common/alert';

// RequestMethod: HTTP 요청 메서드를 열거형(enum)으로 정의
export enum RequestMethod {
  GET = 'get',
  POST = 'post',
  PUT = 'put',
  DELETE = 'delete'
}

// request: HTTP 요청을 axios 사용하여 수행하는 함수
export const request = async <T>(
  method: RequestMethod,
  uri: string,
  params: any,
  data: any,
  responseType: 'data' | 'full' = 'data' // ✅ 추가
): Promise<any> => {
  try {
    // JWT 토큰을 쿠키에서 가져옵니다
    const token = document.cookie
      .split('; ')
      .find(row => row.startsWith('accessToken='))
      ?.split('=')[1];

    const response = await axios(uri, {
      method,
      params,
      data,
      baseURL: '/',
      headers: {
        // Authorization 헤더에 JWT 토큰을 추가합니다
        ...(token && { Authorization: `Bearer ${token}` })
      }
    })

    // ✅ full이면 전체 response, 아니면 data만 반환
    return responseType === 'full' ? response : response.data;

  } catch (error: any) {
    console.log("error : ", error);

    if (error.code && error.code === 'ERR_NETWORK') {
      Swal.alert('서버에 연결이 불가능하거나, 네트워크 오류입니다.', '', 'error')
    }

    if (error.response.status === 404) {
      Swal.alert(`해당 URI에 대한 서버의 응답이 없습니다. : ${method} /${uri}`, '', 'error')
    }

    if (error.response.status === 500) {
      Swal.alert('서버 내부 오류입니다.', '', 'error')
    }

    throw error.response || { message: '알 수 없는 오류가 발생했습니다.' };

  }
}