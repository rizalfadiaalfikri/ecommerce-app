package id.orbion.ecommerce_app.service;

import id.orbion.ecommerce_app.model.AuthRequest;
import id.orbion.ecommerce_app.model.UserInfo;

public interface AuthService {

    UserInfo authenticate(AuthRequest request);

}
