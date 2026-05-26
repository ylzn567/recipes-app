package com.forallergans.recepies.dtos;

import lombok.Data;

@Data
public class UserRegisterDTO {
    private String username;
    private String password;
    private String email;
}