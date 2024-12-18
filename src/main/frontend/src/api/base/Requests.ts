import axios from "axios";

export enum RequestMethod {
  GET = 'get',
  POST = 'post',
  PUT = 'put',
  DELETE = 'delete'
}

export const request = async <T>(
  method: RequestMethod,
  uri: string,
  params: any,
  data: any,
): Promise<T> => {
  try {
    const response = await axios(uri, {
      method,
      params,
      data,
      baseURL: '/'
    })

    return response.data
  } catch (error: any) {
    if (error.code && error.code === 'ERR_NETWORK') {
      alert('서버에 연결이 불가능하거나, 네트워크 오류입니다.')
    }
    if (error.response.status === 404) {
      alert(`해당 URI에 대한 서버의 응답이 없습니다. : ${method} /${uri}`)
    }
    if (error.response.status === 500) {
      alert('서버 내부 오류입니다.')
    }
    throw new Error(error)
  }
}