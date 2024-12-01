package id.orbion.ecommerce_app.service.impl;

import java.time.LocalDateTime;
import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.stereotype.Service;

import id.orbion.ecommerce_app.common.DateUtil;
import id.orbion.ecommerce_app.config.JwtSecretConfig;
import id.orbion.ecommerce_app.model.UserInfo;
import id.orbion.ecommerce_app.service.JwtService;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class JwtServiceImpl implements JwtService {

    private final JwtSecretConfig jwtSecretConfig;
    private SecretKey signinKey;

    @Override
    public String generateToken(UserInfo userInfo) {
        LocalDateTime expiration = LocalDateTime.now().plus(jwtSecretConfig.getJwtExpiration());
        Date expirationDate = DateUtil.convertLocalDateTimeToDate(expiration);

        return Jwts.builder()
                .setSubject(userInfo.getUsername())
                .setIssuedAt(new Date())
                .setExpiration(expirationDate)
                .signWith(signinKey)
                .compact();
    }

    @Override
    public boolean validateToken(String token) {
        try {
            JwtParser parser = Jwts.parserBuilder()
                    .setSigningKey(signinKey)
                    .build();
            parser.parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            log.error(e.getMessage());
            return false;
        }
    }

    @Override
    public String getUsernameFromToken(String token) {
        JwtParser parser = Jwts.parserBuilder()
                .setSigningKey(signinKey)
                .build();
        return parser.parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

}
