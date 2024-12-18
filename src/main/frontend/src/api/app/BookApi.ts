import {request, RequestMethod} from "../base/Requests";

export class BookApi {
  static postBook = (name: string) => {
    return request(RequestMethod.POST, 'book', {}, {name})
  }

  static postBookLoan = (userName: string, bookName: string) => {
    return request(RequestMethod.POST, 'book/loan', {}, {userName, bookName})
  }

  static putBookReturn = (userName: string, bookName: string) => {
    return request(RequestMethod.PUT, 'book/return', {}, {userName, bookName})
  }
}