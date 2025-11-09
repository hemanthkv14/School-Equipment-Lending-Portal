package com.school.service;

import com.school.dto.UserDto;
import com.school.entity.User;
import com.school.enums.UserRole;
import com.school.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class UserServiceTest {

    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder;
    private UserService userService;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        passwordEncoder = mock(PasswordEncoder.class);
        userService = new UserService(userRepository, passwordEncoder);
    }

    @Test
    void testCreateUserSuccess() {
        UserDto dto = new UserDto(null, "john", "john@example.com", UserRole.ADMIN);
        dto.setPassword("password123");

        when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("password123")).thenReturn("hashedPassword");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User u = invocation.getArgument(0);
            u.setUserId(1L);
            return u;
        });

        UserDto created = userService.createUser(dto);

        assertEquals(1L, created.getId());
        assertEquals("john", created.getUsername());
        verify(passwordEncoder).encode("password123");
        verify(userRepository).save(any(User.class));
    }

    @Test
    void testCreateUserDuplicateEmail() {
        UserDto dto = new UserDto(null, "john", "john@example.com", UserRole.ADMIN);
        when(userRepository.findByEmail("john@example.com")).thenReturn(Optional.of(new User()));

        assertThrows(IllegalArgumentException.class, () -> userService.createUser(dto));
    }

    @Test
    void testCreateUserMissingPassword() {
        UserDto dto = new UserDto(null, "john", "john@example.com", UserRole.ADMIN);
        dto.setPassword(null);

        assertThrows(IllegalArgumentException.class, () -> userService.createUser(dto));
    }

    @Test
    void testGetAllUsers() {
        User user1 = new User();
        user1.setUserId(1L);
        user1.setUsername("john");
        User user2 = new User();
        user2.setUserId(2L);
        user2.setUsername("jane");

        when(userRepository.findAll()).thenReturn(List.of(user1, user2));

        List<UserDto> users = userService.getAllUsers();
        assertEquals(2, users.size());
        assertEquals("john", users.get(0).getUsername());
        assertEquals("jane", users.get(1).getUsername());
    }

    @Test
    void testGetUserByIdFound() {
        User user = new User();
        user.setUserId(1L);
        user.setUsername("john");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        User result = userService.getUserById(1L);
        assertEquals(user, result);
    }

    @Test
    void testGetUserByIdNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> userService.getUserById(1L));
    }


    @Test
    void testUpdateUserNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());
        UserDto dto = new UserDto(null, "new", "new@example.com", UserRole.ADMIN);
        assertThrows(EntityNotFoundException.class, () -> userService.updateUser(1L, dto));
    }

    @Test
    void testDeleteUserSuccess() {
        when(userRepository.existsById(1L)).thenReturn(true);

        userService.deleteUser(1L);

        verify(userRepository).deleteById(1L);
    }

    @Test
    void testDeleteUserNotFound() {
        when(userRepository.existsById(1L)).thenReturn(false);
        assertThrows(EntityNotFoundException.class, () -> userService.deleteUser(1L));
    }
}
