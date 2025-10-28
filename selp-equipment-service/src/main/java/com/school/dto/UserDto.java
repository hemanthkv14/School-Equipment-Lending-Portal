package com.school.dto;

import com.school.enums.UserRole; // Assuming this enum is correct
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UserDto {

    private Long id;
    private String username;
    private String email;
    private UserRole role;
    private String password;

    public UserDto(Long id, String username, String email, UserRole role) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.role = role;
    }

}