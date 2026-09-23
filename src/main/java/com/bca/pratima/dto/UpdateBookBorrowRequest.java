package com.bca.pratima.dto;

import com.bca.pratima.appenum.BookBorrowStatus;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateBookBorrowRequest {

    Integer bookId;

    Integer borrowRequestId;

    private boolean shareable;

    private boolean archived;

    @Enumerated(EnumType.STRING)
    private BookBorrowStatus status;

}
