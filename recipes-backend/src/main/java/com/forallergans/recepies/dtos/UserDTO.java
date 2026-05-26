package com.forallergans.recepies.dtos;

import com.forallergans.recepies.entities.Role;

import lombok.Data;

@Data
public class UserDTO {
    private String username;
    private String email;
    private Role role;
}