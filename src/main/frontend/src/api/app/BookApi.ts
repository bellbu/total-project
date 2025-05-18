import {request, RequestMethod} from "../base/Requests";
import {LoanData} from "../../model/LoanData";

export class BookApi {

  static getLoans = () => {
    return request<LoanData[]>(RequestMethod.GET, 'book', {}, {})
  }

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