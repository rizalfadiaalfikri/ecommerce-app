package id.orbion.ecommerce_app.config.middleware;

import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import id.orbion.ecommerce_app.service.JwtService;
import id.orbion.ecommerce_app.service.impl.UserDetailImpl;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final HandlerExceptionResolver handlerExceptionResolver;
    private final UserDetailImpl userDetailImpl;
    private final JwtService jwtService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            try {
                filterChain.doFilter(request, response);
            } catch (Exception e) {
                handlerExceptionResolver.resolveException(request, response, null, e);
            }
            return;
        }

        try {
            final String jwt = authHeader.substring(7);
            if (jwtService.validateToken(jwt)) {
                final String username = jwtService.getUsernameFromToken(jwt);
                Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

                if (username != null && authentication != null) {
                    UserDetails userDetails = userDetailImpl.loadUserByUsername(username);
                    UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities());

                    authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                }
                filterChain.doFilter(request, response);
            }

        } catch (Exception e) {
            handlerExceptionResolver.resolveException(request, response, null, e);
        }

    }

}
