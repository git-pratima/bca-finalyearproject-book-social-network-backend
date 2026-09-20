package com.bca.pratima.dto;

import com.bca.pratima.appenum.AppStatus;
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

    private String bookName;

    private Integer borrowRequestId;

    private Date borrowFromDate;

    private Date borrowToDate;

    private Integer returnPeriodDay;

    private Date finalReturnDate;

    private AppStatus.YesNo agreeToFollowPickupInstruction;

    private AppStatus.YesNo agreeToReturnBorrowedBookAtSameLocation;

    private String comment;

}
