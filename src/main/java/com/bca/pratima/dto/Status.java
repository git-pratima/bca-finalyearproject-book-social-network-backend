package com.bca.pratima.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor   // generates public Status(int, String)
@NoArgsConstructor
public class Status {
    private int status;

    private String message;
}
