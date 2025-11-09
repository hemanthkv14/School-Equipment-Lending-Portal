package com.school.service;

import com.school.dto.UserDto;
import com.school.entity.User;
import com.school.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service class for managing User entities.
 *
 * <p>Responsibilities:
 * <ul>
 *     <li>Create, retrieve, update, and delete users.</li>
 *     <li>Convert between {@link User} entities and {@link UserDto} transfer objects.</li>
 *     <li>Ensure password hashing using {@link PasswordEncoder} for security.</li>
 * </ul>
 */
@Slf4j
@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Creates a new user from the provided {@link UserDto}.
     *
     * @param dto Data transfer object containing user information
     * @return The created user as a {@link UserDto}
     * @throws IllegalArgumentException if the email already exists or password is missing
     */
    @Transactional
    public UserDto createUser(UserDto dto) {
        if (userRepository.findByEmail(dto.getEmail()).isPresent()) {
            log.warn("Attempted to create user with existing email: {}", dto.getEmail());
            throw new IllegalArgumentException("Email already exists: " + dto.getEmail());
        }

        User user = mapDtoToEntity(dto);
        User savedUser = userRepository.save(user);

        log.info("User created with ID: {} | email: {}", savedUser.getUserId(), savedUser.getEmail());
        return mapEntityToDto(savedUser);
    }

    /**
     * Retrieves all users in the system.
     *
     * @return List of all users as {@link UserDto} objects
     */
    @Transactional(readOnly = true)
    public List<UserDto> getAllUsers() {
        List<UserDto> users = userRepository.findAll().stream()
                .map(this::mapEntityToDto)
                .collect(Collectors.toList());
        log.debug("Retrieved {} users from repository", users.size());
        return users;
    }

    /**
     * Retrieves a {@link User} entity by its ID.
     *
     * @param userId The ID of the user
     * @return User entity
     * @throws EntityNotFoundException if no user exists with the given ID
     */
    @Transactional(readOnly = true)
    public User getUserById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with ID: " + userId));
        log.debug("Retrieved user with ID: {}", userId);
        return user;
    }

    /**
     * Updates an existing user with new data.
     *
     * @param userId The ID of the user to update
     * @param dto Data transfer object containing updated user information
     * @return Updated user as a {@link UserDto}
     * @throws EntityNotFoundException if no user exists with the given ID
     */
    @Transactional
    public UserDto updateUser(Long userId, UserDto dto) {
        User existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with ID: " + userId));

        existingUser.setUsername(dto.getUsername());
        existingUser.setEmail(dto.getEmail());
        existingUser.setRole(dto.getRole());

        if (dto.getPassword() != null && !dto.getPassword().isEmpty()) {
            existingUser.setPasswordHash(passwordEncoder.encode(dto.getPassword()));
        }

        User updatedUser = userRepository.save(existingUser);
        log.info("User updated with ID: {} | email: {}", updatedUser.getUserId(), updatedUser.getEmail());
        return mapEntityToDto(updatedUser);
    }

    @Transactional
    public void deleteUser(Long userId) {
        if (!userRepository.existsById(userId)) {
            log.warn("Attempted to delete non-existing user with ID: {}", userId);
            throw new EntityNotFoundException("User not found with ID: " + userId);
        }
        userRepository.deleteById(userId);
        log.info("User deleted with ID: {}", userId);
    }

    /**
     * Maps a {@link UserDto} to a {@link User} entity.
     *
     * @param dto User data transfer object
     * @return User entity
     * @throws IllegalArgumentException if password is null or empty
     */
    private User mapDtoToEntity(UserDto dto) {
        User user = new User();
        user.setUsername(dto.getUsername());
        user.setEmail(dto.getEmail());
        user.setRole(dto.getRole());

        if (dto.getPassword() == null || dto.getPassword().isEmpty()) {
            throw new IllegalArgumentException("Password must be provided for new user creation.");
        }

        String hashedPassword = passwordEncoder.encode(dto.getPassword());
        user.setPasswordHash(hashedPassword);

        return user;
    }

    /**
     * Maps a {@link User} entity to a {@link UserDto}.
     *
     * @param user User entity
     * @return UserDto containing user information
     */
    public UserDto mapEntityToDto(User user) {
        return new UserDto(
                user.getUserId(),
                user.getUsername(),
                user.getEmail(),
                user.getRole()
        );
    }
}