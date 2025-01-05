package id.orbion.ecommerce_app.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import id.orbion.ecommerce_app.model.ApiResponse;
import id.orbion.ecommerce_app.model.UserAddressRequest;
import id.orbion.ecommerce_app.model.UserAddressResponse;
import id.orbion.ecommerce_app.model.UserInfo;
import id.orbion.ecommerce_app.service.UserAddressService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/addresses")
@SecurityRequirement(name = "Bearer")
@RequiredArgsConstructor
public class AddressController {

    private final UserAddressService userAddressService;

    @PostMapping
    public ResponseEntity<ApiResponse> create(@Valid @RequestBody UserAddressRequest userAddressRequest) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserInfo userInfo = (UserInfo) authentication.getPrincipal();

        UserAddressResponse userAddressResponse = userAddressService.cerate(userInfo.getUser().getUserId(),
                userAddressRequest);

        return ResponseEntity.ok(
                ApiResponse.builder()
                        .status(200)
                        .message("Address created successfully")
                        .data(userAddressResponse)
                        .build());
    }

    @GetMapping
    public ResponseEntity<ApiResponse> findAddressByUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserInfo userInfo = (UserInfo) authentication.getPrincipal();

        List<UserAddressResponse> userAddressResponses = userAddressService
                .findByUserId(userInfo.getUser().getUserId());

        return ResponseEntity.ok(
                ApiResponse.builder()
                        .status(200)
                        .message("Addresses found")
                        .data(userAddressResponses)
                        .build());
    }

    @GetMapping("/{addressId}")
    public ResponseEntity<ApiResponse> get(@PathVariable("addressId") Long addressId) {

        UserAddressResponse userAddressResponse = userAddressService.findById(addressId);

        return ResponseEntity.ok(
                ApiResponse.builder()
                        .status(200)
                        .message("Address found")
                        .data(userAddressResponse)
                        .build());
    }

    @PutMapping("/{addressId}")
    public ResponseEntity<ApiResponse> update(@PathVariable("addressId") Long addressId,
            @Valid @RequestBody UserAddressRequest userAddressRequest) {

        UserAddressResponse userAddressResponse = userAddressService.update(addressId,
                userAddressRequest);

        return ResponseEntity.ok(
                ApiResponse.builder()
                        .status(200)
                        .message("Address updated successfully")
                        .data(userAddressResponse)
                        .build());
    }

    @DeleteMapping("/{addressId}")
    public ResponseEntity<ApiResponse> delete(@PathVariable("addressId") Long addressId) {
        userAddressService.delete(addressId);
        return ResponseEntity.ok(
                ApiResponse.builder()
                        .status(200)
                        .message("Address deleted successfully")
                        .build());
    }

    @PutMapping("/{addressId}/set-default")
    public ResponseEntity<ApiResponse> setDefault(@PathVariable("addressId") Long addressId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserInfo userInfo = (UserInfo) authentication.getPrincipal();

        UserAddressResponse defAddressResponse = userAddressService.setDefaultAddress(userInfo.getUser().getUserId(),
                addressId);
        return ResponseEntity.ok(
                ApiResponse.builder()
                        .status(200)
                        .message("Address set as default successfully")
                        .data(defAddressResponse)
                        .build());
    }

}
