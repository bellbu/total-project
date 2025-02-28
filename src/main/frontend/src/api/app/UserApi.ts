import {request, RequestMethod} from "../base/Requests";
import {UserData} from "../../model/UserData";

export class UserApi {
  static postUser = (name: string, age: number | null, pageSize: number) => {
    return request(RequestMethod.POST, 'user', {}, {name, age, pageSize})
  }

  static getUser = (cursor: number | null, size: number) => {
    return request<UserData[]>(RequestMethod.GET, 'user', cursor ? { cursor, size } : { size }, {})
  }

  static putUser = (id: number, name: string) => {
    return request(RequestMethod.PUT, 'user', {}, {id, name})
  }

  static deleteUser = (name: string) => {
    return request(RequestMethod.DELETE, 'user', {name}, {})
  }

  static getUserCount = () => {
    return request<number>(RequestMethod.GET, 'user/count', {}, {})
  }
}