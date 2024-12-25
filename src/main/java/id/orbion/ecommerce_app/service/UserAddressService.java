package id.orbion.ecommerce_app.service;

import java.util.List;

import id.orbion.ecommerce_app.model.UserAddressRequest;
import id.orbion.ecommerce_app.model.UserAddressResponse;

public interface UserAddressService {

    UserAddressResponse cerate(Long userId, UserAddressRequest request);

    List<UserAddressResponse> findByUserId(Long userId);

    UserAddressResponse findById(Long userAddressId);

    UserAddressResponse update(Long addressId, UserAddressRequest request);

    void delete(Long addressId);

    UserAddressResponse setDefaultAddress(Long userId, Long addressId);

}
