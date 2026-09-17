package com.bca.pratima.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BookResponse {

    private Integer id;
    private String title;
    private String authorName;
    private String isbn;
    private String synopsis;
    private String owner;
    /** Cloudinary delivery URL. Null when the book has no cover. */
    private String cover;
    private double rate;
    private boolean archived;
    private boolean shareable;
    private AddressDto bookAddress;
    private String pickUpLocation;
    private String pickupInstructions;
}
