import axios from "axios";
import * as Swal from '../../api/common/alert';

// RequestMethod: HTTP 요청 메서드를 열거형(enum)으로 정의
// ex) RequestMethod.GET => 'get'
export enum RequestMethod {
  GET = 'get',
  POST = 'post',
  PUT = 'put',
  DELETE = 'delete'
}

// request 함수 정의
export const request = async <T>( // await 명령어 사용을 위해 async 작성. async 함수 안에서만 await 사용 가능함
  method: RequestMethod,
  uri: string,
  params: any,
  data: any,
  responseType: 'data' | 'full' = 'data' // 'data' or 'full' 중 선택하여 응답 처리 방식 결정 but 호출할 때 이 값을 안 넘기면, 기본값 'data'가 들어감
): Promise<any> => { // Promise: 비동기 작업의 결과를 담는 객체
  try {
    // JWT 토큰을 쿠키에서 가져옴
    const token = document.cookie // document.cookie: 브라우저에서 현재 페이지의 모든 쿠키 문자열 가져옴
      .split('; ') // split(): 문자열을 일정한 구분자로 잘라서 배열로 저장
      .find(row => row.startsWith('accessToken=')) // find(): 배열에서 조건을 만족하는 첫 번째 요소를 찾아서 반환
      ?.split('=')[1]; // ex) 'accessToken=abcdef12345'에서 'abcdef12345' 부분만 추출

    // axios API 호출
    const response = await axios(uri, {
      method,
      params,
      data,
      baseURL: '/',
      headers: {  // headers: HTTP 요청 헤더
        ...(token && { Authorization: `Bearer ${token}` }) // token이 있을 때만 Authorization 헤더 추가
      }
    })

    // full이면 response, 아니면 response.data 반환
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