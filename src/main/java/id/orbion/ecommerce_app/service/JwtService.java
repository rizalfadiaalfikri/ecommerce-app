package id.orbion.ecommerce_app.service;

import id.orbion.ecommerce_app.model.UserInfo;

public interface JwtService {

    String generateToken(UserInfo userInfo);

    boolean validateToken(String token);

    String getUsernameFromToken(String token);

}
