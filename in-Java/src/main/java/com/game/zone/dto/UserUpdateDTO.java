package com.game.zone.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserUpdateDTO {
    private String username;
    private String email;
    private String password; // optional: only send if you want to change it
    private String role;
}