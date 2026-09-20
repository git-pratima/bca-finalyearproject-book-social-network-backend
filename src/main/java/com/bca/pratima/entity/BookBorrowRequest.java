package com.bca.pratima.entity;

import com.bca.pratima.appenum.AppStatus;
import com.bca.pratima.appenum.BookBorrowStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Date;

@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name="tbl_book_borrow_request")
public class BookBorrowRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "request_date", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date requestDate;

    @Column(name="borrow_from_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date borrowFromDate;

    @Column(name="borrow_to_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date borrowToDate;

    @Column(name="return_period_day")
    private Integer returnPeriodDay;

    @Column(name="final_return_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date finalReturnDate;           //borrowToDate + returnPeriodDay

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private BookBorrowStatus status;

    @Column(length = 2500)
    private String comment;

    @Column(name="agree_to_follow_pickup_ins")
    @Enumerated(EnumType.STRING)
    private AppStatus.YesNo agreeToFollowPickupInstruction;

    @Column(name="agree_to_return_borrow_book_at_same_location")
    @Enumerated(EnumType.STRING)
    private AppStatus.YesNo agreeToReturnBorrowedBookAtSameLocation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "borrower_id", nullable = false)
    private User borrower;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_owner_id", nullable = false)
    private User bookOwner;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    @Column(name="CREATED_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdDate;

    @Column(name="CREATED_BY")
    private String createdBy;

    @Column(name="MODIFIED_DATE")
    private Date modifiedDate;

    @Column(name="MODIFIED_BY")
    private String modifiedBy;

    @PreUpdate
    @PrePersist
    public void updateTimeStamps()
    {
        String userName = null;
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            userName = authentication.getName();
        }
        this.modifiedDate = new Date();
        this.modifiedBy = userName;
        if(this.createdDate == null) {
            this.createdDate = new Date();
            this.createdBy = userName;
        }
    }
}
