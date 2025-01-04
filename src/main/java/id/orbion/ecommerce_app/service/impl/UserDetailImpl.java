package id.orbion.ecommerce_app.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;

import id.orbion.ecommerce_app.entity.Role;
import id.orbion.ecommerce_app.entity.User;
import id.orbion.ecommerce_app.model.UserInfo;
import id.orbion.ecommerce_app.repository.RoleRepository;
import id.orbion.ecommerce_app.repository.UserRepository;
import id.orbion.ecommerce_app.service.CacheService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserDetailImpl implements UserDetailsService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final CacheService cacheService;

    private final String USER_CACHE_KEY = "cache:user:";
    private final String USER_ROLE_CACHE_KEY = "cache:user:roles:";

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        String userCacheKey = USER_CACHE_KEY + username;
        String roleCacheKey = USER_ROLE_CACHE_KEY + username;

        Optional<User> userOpt = cacheService.get(userCacheKey, User.class);
        Optional<List<Role>> roleOpt = cacheService.get(roleCacheKey, new TypeReference<List<Role>>() {
        });

        if (userOpt.isPresent() && roleOpt.isPresent()) {
            return UserInfo.builder()
                    .roles(roleOpt.get())
                    .user(userOpt.get())
                    .build();
        }

        User user = userRepository.findByKeyword(username).orElseThrow(
                () -> new UsernameNotFoundException("User not found with username: " + username));

        List<Role> roles = roleRepository.findByUserId(user.getUserId());
        UserInfo userInfo = UserInfo.builder()
                .roles(roles)
                .user(user)
                .build();

        // save user in cache
        cacheService.put(userCacheKey, user);
        cacheService.put(roleCacheKey, roles);

        return userInfo;
    }

}
