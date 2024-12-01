package id.orbion.ecommerce_app.service.impl;

import java.util.List;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import id.orbion.ecommerce_app.entity.Role;
import id.orbion.ecommerce_app.entity.User;
import id.orbion.ecommerce_app.model.UserInfo;
import id.orbion.ecommerce_app.repository.RoleRepository;
import id.orbion.ecommerce_app.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserDetailImpl implements UserDetailsService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByKeyword(username).orElseThrow(
                () -> new UsernameNotFoundException("User not found with username: " + username));

        List<Role> roles = roleRepository.findByUserId(user.getUserId());
        return UserInfo.builder()
                .roles(roles)
                .user(user)
                .build();
    }

}
