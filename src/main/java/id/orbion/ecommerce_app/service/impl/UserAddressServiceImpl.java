package id.orbion.ecommerce_app.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import id.orbion.ecommerce_app.common.error.ResourceNotFoundException;
import id.orbion.ecommerce_app.entity.UserAddress;
import id.orbion.ecommerce_app.model.UserAddressRequest;
import id.orbion.ecommerce_app.model.UserAddressResponse;
import id.orbion.ecommerce_app.repository.UserAddressRepository;
import id.orbion.ecommerce_app.service.UserAddressService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserAddressServiceImpl implements UserAddressService {

    private final UserAddressRepository userAddressRepository;

    @Override
    @Transactional
    public UserAddressResponse cerate(Long userId, UserAddressRequest request) {
        UserAddress newAddress = UserAddress.builder()
                .userId(userId)
                .addressName(request.getAddressName())
                .streetAddress(request.getStreetAddress())
                .city(request.getCity())
                .state(request.getState())
                .postalCode(request.getPostalCode())
                .country(request.getCountry())
                .isDefault(request.isDefault())
                .build();

        if (request.isDefault()) {
            Optional<UserAddress> existingDefault = userAddressRepository.findByUserIdAndIsDefaultTrue(userId);
            existingDefault.ifPresent(
                    address -> {
                        address.setIsDefault(false);
                        userAddressRepository.save(address);
                    });
        }

        UserAddress savedUserAddress = userAddressRepository.save(newAddress);

        return UserAddressResponse.fromUserAddress(savedUserAddress);
    }

    @Override
    public List<UserAddressResponse> findByUserId(Long userId) {
        List<UserAddress> addresses = userAddressRepository.findByUserId(userId);
        return addresses.stream()
                .map(UserAddressResponse::fromUserAddress)
                .toList();
    }

    @Override
    public UserAddressResponse findById(Long userAddressId) {
        return userAddressRepository.findById(userAddressId)
                .map(UserAddressResponse::fromUserAddress)
                .orElseThrow(
                        () -> new ResourceNotFoundException("No user address found for id " + userAddressId));
    }

    @Override
    @Transactional
    public UserAddressResponse update(Long addressId, UserAddressRequest request) {
        UserAddress existingAddress = userAddressRepository.findById(addressId)
                .orElseThrow(
                        () -> new ResourceNotFoundException("No user address found for id " + addressId));
        UserAddress updatedAddress = UserAddress.builder()
                .userAddressId(existingAddress.getUserAddressId())
                .addressName(request.getAddressName())
                .streetAddress(request.getStreetAddress())
                .city(request.getCity())
                .state(request.getState())
                .postalCode(request.getPostalCode())
                .country(request.getCountry())
                .isDefault(request.isDefault())
                .build();

        if (request.isDefault()) {
            Optional<UserAddress> existingDefault = userAddressRepository
                    .findByUserIdAndIsDefaultTrue(existingAddress.getUserId());
            existingDefault.ifPresent(
                    address -> {
                        address.setIsDefault(false);
                        userAddressRepository.save(address);
                    });
        }

        UserAddress savUserAddress = userAddressRepository.save(updatedAddress);

        return UserAddressResponse.fromUserAddress(savUserAddress);
    }

    @Override
    public void delete(Long addressId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'delete'");
    }

    @Override
    public UserAddressResponse setDefaultAddress(Long userId, Long addressId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'setDefaultAddress'");
    }

}
