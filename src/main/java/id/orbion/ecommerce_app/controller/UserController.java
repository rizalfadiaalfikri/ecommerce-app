package id.orbion.ecommerce_app.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import id.orbion.ecommerce_app.common.error.ForbiddenAccessException;
import id.orbion.ecommerce_app.model.ApiResponse;
import id.orbion.ecommerce_app.model.UserInfo;
import id.orbion.ecommerce_app.model.UserResponse;
import id.orbion.ecommerce_app.model.UserUpdateRequest;
import id.orbion.ecommerce_app.service.UserService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/users")
@AllArgsConstructor
@SecurityRequirement(name = "Bearer")
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    public ResponseEntity<ApiResponse> me() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserInfo userInfo = (UserInfo) authentication.getPrincipal();

        UserResponse userResponse = UserResponse.fromUserAndRoles(userInfo.getUser(), userInfo.getRoles());

        return ResponseEntity.ok(
                ApiResponse.builder()
                        .status(200)
                        .message("User found")
                        .data(userResponse)
                        .build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse> updateUser(
            @PathVariable("id") Long id,
            @Valid @RequestBody UserUpdateRequest request) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserInfo userInfo = (UserInfo) authentication.getPrincipal();

        if (userInfo.getUser().getUserId() != id && !userInfo.getAuthorities().contains("ROLE_ADMIN")) {
            throw new ForbiddenAccessException(
                    "User " + userInfo.getUser().getUsername() + " is not allowed to update this user");
        }

        UserResponse userResponse = userService.updateUser(id, request);

        return ResponseEntity.ok(
                ApiResponse.builder()
                        .status(200)
                        .message("User updated successfully")
                        .data(userResponse)
                        .build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deleteUser(@PathVariable("id") Long id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserInfo userInfo = (UserInfo) authentication.getPrincipal();

        if (userInfo.getUser().getUserId() != id && !userInfo.getAuthorities().contains("ROLE_ADMIN")) {
            throw new ForbiddenAccessException(
                    "User " + userInfo.getUser().getUsername() + " is not allowed to delete this user");
        }

        userService.deleteUser(id);

        return ResponseEntity.ok(
                ApiResponse.builder()
                        .status(200)
                        .message("User deleted successfully")
                        .build());
    }

}
