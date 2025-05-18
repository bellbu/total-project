package com.group.totalproject.dto.book.response;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.group.totalproject.domain.user.loanhistory.UserLoanHistory;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class LoanResponse {
    private final long id;
    private final String userName;
    private final String bookName
            ;
    @JsonProperty("isReturn")
    private final boolean isReturn;

    private final LocalDateTime loanedAt;

    @JsonInclude(JsonInclude.Include.NON_NULL) // null이면 JSON에서 제외
    private final LocalDateTime returnedAt;

    @JsonCreator
    public LoanResponse(
            @JsonProperty("id") long id,
            @JsonProperty("userName") String userName,
            @JsonProperty("bookName") String bookName,
            @JsonProperty("isReturn") boolean isReturn,
            @JsonProperty("loanedAt") LocalDateTime loanedAt,
            @JsonProperty("returnedAt") LocalDateTime returnedAt
    ) {
        this.id = id;
        this.userName = userName;
        this.bookName = bookName;
        this.isReturn = isReturn;
        this.loanedAt = loanedAt;
        this.returnedAt = returnedAt;
    }

    public LoanResponse(UserLoanHistory history) {
        this.id = history.getId();
        this.userName = history.getUser().getName();
        this.bookName = history.getBook().getName();
        this.isReturn = history.isReturn();
        this.loanedAt = history.getLoanedAt();
        this.returnedAt = history.getReturnedAt();
    }
}