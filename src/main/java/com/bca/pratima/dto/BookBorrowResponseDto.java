package com.bca.pratima.dto;

import com.bca.pratima.appenum.AppStatus;
import com.bca.pratima.appenum.BookBorrowStatus;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.util.Date;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookBorrowResponseDto {

    private Integer bookId;

    private String bookName;

    private String bookCover;

    @Enumerated(EnumType.STRING)
    private BookBorrowStatus status;

    private Integer borrowRequestId;

    private Date borrowFromDate;

    private Date borrowToDate;

    private Integer returnPeriodDay;

    private Date finalReturnDate;

    private AppStatus.YesNo agreeToFollowPickupInstruction;

    private AppStatus.YesNo agreeToReturnBorrowedBookAtSameLocation;

    private String comment;

}
