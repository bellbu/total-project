import {request, RequestMethod} from "../base/Requests";
import {UserData} from "../../model/UserData";

export class UserApi {
  static postUser = (name: string, age: number | null) => {
    return request(RequestMethod.POST, 'user', {}, {name, age})
  }

  static getUser = (page: number, size: number = 10000) => {
    return request<UserData[]>(RequestMethod.GET, 'user', { page, size }, {})
  }

  static putUser = (id: number, name: string) => {
    return request(RequestMethod.PUT, 'user', {}, {id, name})
  }

  static deleteUser = (name: string) => {
    return request(RequestMethod.DELETE, 'user', {name}, {})
  }
}