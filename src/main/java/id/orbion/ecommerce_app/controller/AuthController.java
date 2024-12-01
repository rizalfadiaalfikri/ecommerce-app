package id.orbion.ecommerce_app.controller;

import java.net.URI;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import id.orbion.ecommerce_app.model.ApiResponse;
import id.orbion.ecommerce_app.model.AuthRequest;
import id.orbion.ecommerce_app.model.AuthResponse;
import id.orbion.ecommerce_app.model.UserInfo;
import id.orbion.ecommerce_app.model.UserRegisterRequest;
import id.orbion.ecommerce_app.model.UserResponse;
import id.orbion.ecommerce_app.service.AuthService;
import id.orbion.ecommerce_app.service.JwtService;
import id.orbion.ecommerce_app.service.UserService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/auth")
@AllArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final UserService userService;
    private final JwtService jwtService;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse> authenticate(
            @RequestBody AuthRequest request) {
        UserInfo userInfo = authService.authenticate(request);
        String token = jwtService.generateToken(userInfo);
        return ResponseEntity.ok(ApiResponse.builder()
                .status(200)
                .message("Login Successfully")
                .data(AuthResponse.fromUserInfo(userInfo, token))
                .build());
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse> register(
            @Valid @RequestBody UserRegisterRequest request) {
        UserResponse userResponse = userService.register(request);
        return ResponseEntity.created(URI.create("")).body(
                ApiResponse.builder()
                        .status(201)
                        .message("User created successfully")
                        .data(userResponse)
                        .build());
    }

}
