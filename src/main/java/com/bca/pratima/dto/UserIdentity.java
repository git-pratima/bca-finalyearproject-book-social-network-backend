package com.bca.pratima.dto;
import lombok.Data;

@Data
public class UserIdentity {
    private Long userId;
    private String email;
    private String token;
    private String userName;
}
