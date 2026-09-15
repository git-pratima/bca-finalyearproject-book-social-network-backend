package com.bca.pratima.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class BookRequest {
    private Integer id;

    @NotNull(message = "100")
    @NotEmpty(message = "100")
    private String title;

    @NotNull(message = "101")
    @NotEmpty(message = "101")
    private String authorName;

    @NotNull(message = "102")
    @NotEmpty(message = "102")
    private String isbn;

    @NotNull(message = "103")
    @NotEmpty(message = "103")
    private String synopsis;

    private boolean shareable;

    private AddressDto bookAddress;

    @NotNull(message = "104")
    @NotEmpty(message = "104")
    private String pickUpLocation;

    @NotNull(message = "105")
    @NotEmpty(message = "105")
    private String pickupInstructions;
}
