package com.bca.pratima.entity;

import com.bca.pratima.dto.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name="tbl_book")
public class Book{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String title;

    private String authorName;

    private String isbn;

    @Size(max = 2500, message = "Pickup instructions must not exceed 2500 characters")
    @Column(length = 2500)
    private String synopsis;

    private String bookCover;

    private boolean archived;

    private boolean shareable;

    @Size(max = 2500, message = "Pickup instructions must not exceed 2500 characters")
    @Column(length = 2500)
    private String pickupInstructions;

    @Size(max = 2500, message = "Pickup instructions must not exceed 2500 characters")
    @Column(length = 2500)
    private String pickUpLocation;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "address_id")
    private Address address;

    @ManyToOne
    @JoinColumn(name = "owner_id")
    private User owner;

    @OneToMany(mappedBy = "book")
    private List<Feedback> feedbacks;

    @OneToMany(mappedBy = "book")
    private List<BookTransactionHistory> histories;

    @OneToMany(mappedBy = "book")
    private List<BookBorrowRequest> borrowRequests;

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

    @Transient
    public double getRate() {
        if (feedbacks == null || feedbacks.isEmpty()) {
            return 0.0;
        }
        var rate = this.feedbacks.stream()
                .mapToDouble(Feedback::getNote)
                .average()
                .orElse(0.0);
        double roundedRate = Math.round(rate * 10.0) / 10.0;

        // Return 4.0 if roundedRate is less than 4.5, otherwise return 4.5
        return roundedRate;
    }
}
