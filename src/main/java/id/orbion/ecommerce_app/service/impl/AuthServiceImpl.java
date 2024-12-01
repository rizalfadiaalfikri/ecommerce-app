package id.orbion.ecommerce_app.service.impl;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import id.orbion.ecommerce_app.common.error.InvalidPasswordException;
import id.orbion.ecommerce_app.model.AuthRequest;
import id.orbion.ecommerce_app.model.UserInfo;
import id.orbion.ecommerce_app.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;

    @Override
    public UserInfo authenticate(AuthRequest request) {

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));

            UserInfo userInfo = (UserInfo) authentication.getPrincipal();
            return userInfo;

        } catch (Exception e) {
            log.error(e.getMessage());
            throw new InvalidPasswordException("Invalid username or password");
        }

    }

}
