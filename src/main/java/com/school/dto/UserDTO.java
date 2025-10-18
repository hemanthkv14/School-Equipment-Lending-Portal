package com.school.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.school.entity.User;
import com.school.enums.UserRole;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
public class UserDTO {
    private Long userId;

    private String name;

    private String email;

    private String password;

    @JsonFormat(with = JsonFormat.Feature.ACCEPT_CASE_INSENSITIVE_PROPERTIES)
    private UserRole role;

    private LocalDateTime createdAt;

    public UserDTO() {
    }

    public UserDTO(User e) {
        this.userId = e.getUserId();
        this.name = e.getName();
        this.email = e.getEmail();
        this.password = e.getPassword();
        this.role = e.getRole();
        this.createdAt = e.getCreatedAt();
    }
}
