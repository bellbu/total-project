export interface LoanData {
  id: number;
  userName: string;
  bookName: string;
  isReturn: boolean; // 반납 여부 (true: 반납됨, false: 미반납)
  loanedAt: string;  // 대출일자
  returnedAt: string | null; // 반납일자 (없으면 null)
}