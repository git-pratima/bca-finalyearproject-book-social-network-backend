package com.bca.pratima.dto;

import com.bca.pratima.appenum.AppStatus;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookBorrowRequestDto {

    @NotNull(message = "Book ID is required")
    @Positive(message = "Book ID must be greater than 0")
    private Integer bookId;

    @NotNull(message = "Borrow from date is required")
    private Date borrowFromDate;

    @NotNull(message = "Borrow to date is required")
    private Date borrowToDate;

    @NotNull(message = "Return period is required")
    @Positive(message = "Return period must be greater than 0")
    private Integer returnPeriodDay;

    @NotNull(message = "Final Return date is required")
    private Date finalReturnDate;

    @NotNull(message = "Agreement to follow pickup instructions is required")
    private AppStatus.YesNo agreeToFollowPickupInstruction;

    @NotNull(message = "Agreement to return the book at the same location is required")
    private AppStatus.YesNo agreeToReturnBorrowedBookAtSameLocation;

    private String comment;
}
