package id.orbion.ecommerce_app.service;

import id.orbion.ecommerce_app.model.UserRegisterRequest;
import id.orbion.ecommerce_app.model.UserResponse;
import id.orbion.ecommerce_app.model.UserUpdateRequest;

public interface UserService {
    UserResponse register(UserRegisterRequest request);

    UserResponse findById(Long id);

    UserResponse findByKeyword(String keyword);

    UserResponse updateUser(Long id, UserUpdateRequest request);

    void deleteUser(Long id);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);
}
