package id.orbion.ecommerce_app.service.impl;

import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import id.orbion.ecommerce_app.common.error.BadRequestException;
import id.orbion.ecommerce_app.common.error.InvalidPasswordException;
import id.orbion.ecommerce_app.common.error.UserNotFoundException;
import id.orbion.ecommerce_app.common.error.UsernameAlreadyExistsException;
import id.orbion.ecommerce_app.entity.Role;
import id.orbion.ecommerce_app.entity.User;
import id.orbion.ecommerce_app.entity.UserRole;
import id.orbion.ecommerce_app.model.UserRegisterRequest;
import id.orbion.ecommerce_app.model.UserResponse;
import id.orbion.ecommerce_app.model.UserUpdateRequest;
import id.orbion.ecommerce_app.repository.RoleRepository;
import id.orbion.ecommerce_app.repository.UserRepository;
import id.orbion.ecommerce_app.repository.UserRoleRepository;
import id.orbion.ecommerce_app.service.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserRoleRepository userRoleRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public UserResponse register(UserRegisterRequest request) {
        try {
            if (existsByUsername(request.getUsername())) {
                throw new UsernameAlreadyExistsException("Username already exists");
            }

            if (existsByEmail(request.getEmail())) {
                throw new UsernameAlreadyExistsException("Email already exists");
            }

            if (!request.getPassword().equals(request.getPasswordConfirmation())) {
                throw new BadRequestException("Password and password confirmation must be the same");
            }

            User user = User.builder()
                    .username(request.getUsername())
                    .email(request.getEmail())
                    .password(passwordEncoder.encode(request.getPassword()))
                    .enable(true)
                    .build();

            userRepository.save(user);

            Role role = roleRepository.findByName("ROLE_USER").orElseThrow(
                    () -> new UserNotFoundException("Role not found with name: ROLE_USER"));

            UserRole userRole = UserRole.builder()
                    .id(new UserRole.UserRoleId(user.getUserId(), role.getRoleId()))
                    .build();

            userRoleRepository.save(userRole);

            return UserResponse.fromUserAndRoles(user, List.of(role));
        } catch (Exception e) {
            throw new BadRequestException(e.getMessage());
        }
    }

    @Override
    public UserResponse findById(Long id) {
        User user = userRepository.findById(id).orElseThrow(
                () -> new UserNotFoundException("User not found with id: " + id));

        List<Role> roles = roleRepository.findByUserId(user.getUserId());
        return UserResponse.fromUserAndRoles(user, roles);
    }

    @Override
    public UserResponse findByKeyword(String keyword) {
        User user = userRepository.findByKeyword(keyword).orElseThrow(
                () -> new UserNotFoundException("User not found with keyword: " + keyword));

        List<Role> roles = roleRepository.findByUserId(user.getUserId());
        return UserResponse.fromUserAndRoles(user, roles);
    }

    @Override
    @Transactional
    public UserResponse updateUser(Long id, UserUpdateRequest request) {

        try {
            User user = userRepository.findById(id).orElseThrow(
                    () -> new UserNotFoundException("User not found with id: " + id));

            if (request.getCurrentPassword() != null && request.getNewPassword() != null) {
                if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
                    throw new InvalidPasswordException("Current password is incorrect");
                }

                user.setPassword(passwordEncoder.encode(request.getNewPassword()));
            }

            if (request.getUsername() != null && !request.getUsername().equals(user.getUsername())) {
                if (existsByUsername(request.getUsername())) {
                    throw new UsernameAlreadyExistsException("Username already exists");
                }

                user.setUsername(request.getUsername());
            }

            if (request.getEmail() != null && !request.getEmail().equals(user.getEmail())) {
                if (existsByEmail(request.getEmail())) {
                    throw new UsernameAlreadyExistsException("Email already exists");
                }

                user.setEmail(request.getEmail());
            }

            userRepository.save(user);
            List<Role> roles = roleRepository.findByUserId(user.getUserId());
            return UserResponse.fromUserAndRoles(user, roles);

        } catch (Exception e) {
            throw new BadRequestException(e.getMessage());
        }

    }

    @Override
    @Transactional
    public void deleteUser(Long id) {
        User user = userRepository.findById(id).orElseThrow(
                () -> new UserNotFoundException("User not found with id: " + id));

        userRoleRepository.deleteByUserId(user.getUserId());
        userRepository.deleteById(id);

    }

    @Override
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    @Override
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

}
