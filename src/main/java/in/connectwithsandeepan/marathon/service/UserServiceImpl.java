package in.connectwithsandeepan.marathon.service;

import in.connectwithsandeepan.marathon.dto.UserRequestDto;
import in.connectwithsandeepan.marathon.dto.UserResponseDto;
import in.connectwithsandeepan.marathon.entity.User;
import in.connectwithsandeepan.marathon.repo.UserRepo;
import in.connectwithsandeepan.marathon.util.RoleValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepo userRepo;
    private final PasswordEncoder passwordEncoder;
    private final RoleValidator roleValidator;

    @Override
    public UserResponseDto saveUser(UserRequestDto user) {
        User existingUser = userRepo.findByUsername(user.getUsername())
                .orElse(null);
        if (existingUser != null) {
            throw new IllegalArgumentException("Username already exists: " + user.getUsername());
        }
        User newUser = new User();
        newUser.setUsername(user.getUsername());
        newUser.setEmail(user.getEmail());
        newUser.setPassword(passwordEncoder.encode(user.getPassword()));
        newUser.setRoles(roleValidator.sanitizeRoles(null));
        return convertUserToDto(userRepo.save(newUser));
    }

    @Override
    public UserResponseDto findByUsername(String username) {
        User user = userRepo.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));
        return convertUserToDto(user);
    }

    @Override
    public List<UserResponseDto> findAllByEmail(String email) {
        List<User> users = userRepo.findAllByEmail(email);
        return users.stream().map(this::convertUserToDto).toList();
    }

    @Override
    public UserResponseDto findById(Long id) {
        User user = userRepo.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with id: " + id));

        return convertUserToDto(user);
    }

    @Override
    public List<UserResponseDto> findAllUsers() {
        return userRepo.findAll().stream()
                .map(user -> {
                    UserResponseDto dto = new UserResponseDto();
                    dto.setId(user.getId());
                    dto.setUsername(user.getUsername());
                    dto.setEmail(user.getEmail());
                    dto.setEnabled(user.isEnabled());
                    dto.setRoles(user.getRoles());
                    dto.setCreatedAt(user.getCreatedAt());
                    dto.setUpdatedAt(user.getUpdatedAt());
                    return dto;
                })
                .toList();
    }

    @Override
    public void deleteUserById(Long id) {
        userRepo.deleteById(id);
    }

    @Override
    public void updateUserEnabledStatus(Long id, boolean enabled) {
        Optional<User> userOptional = userRepo.findById(id);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            user.setEnabled(enabled);
            userRepo.save(user);
        } else {
            throw new UsernameNotFoundException("User not found with ID: " + id);
        }
    }

    @Override
    public void updateUserRoles(Long id, Set<String> roles) {
        User user = userRepo.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with id: " + id));
        user.setRoles(roles);
        userRepo.save(user);
    }

    private UserResponseDto convertUserToDto(User user) {
        UserResponseDto dto = new UserResponseDto();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setEnabled(user.isEnabled());
        dto.setRoles(user.getRoles());
        dto.setCreatedAt(user.getCreatedAt());
        dto.setUpdatedAt(user.getUpdatedAt());
        return dto;
    }
}