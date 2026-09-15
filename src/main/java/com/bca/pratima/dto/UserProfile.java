package com.bca.pratima.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.Date;

@Setter
@Getter
@Builder
@AllArgsConstructor   // generates public Status(int, String)
@NoArgsConstructor
public class UserProfile {

    private Integer id;

    private String firstName;

    private String lastName;

    private String email;

    private LocalDateTime memberSince;

    private Integer numberOfBooksShared;

    private Integer numberOfBooksBorrowed;

    @Builder.Default
    private AddressDto address = new AddressDto();
}
