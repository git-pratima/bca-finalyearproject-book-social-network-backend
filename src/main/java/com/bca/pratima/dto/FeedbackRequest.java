package com.bca.pratima.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FeedbackRequest{
    @NotNull(message = "204")
    Integer bookId;

    @Positive(message = "200")
    @Min(value = 0, message = "201")
    @Max(value = 5, message = "202")
    Double rating;

    @NotNull(message = "203")
    @NotEmpty(message = "203")
    @NotBlank(message = "203")
    String comment;
}
