package com.jsoft.magenta.security.model;

import com.jsoft.magenta.users.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginResponse {
    private User user;
    private String jwt;
    private String refreshToken;
}
