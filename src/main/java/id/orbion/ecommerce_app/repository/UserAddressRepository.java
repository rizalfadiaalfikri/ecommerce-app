package id.orbion.ecommerce_app.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import id.orbion.ecommerce_app.entity.UserAddress;

@Repository
public interface UserAddressRepository extends JpaRepository<UserAddress, Long> {

        List<UserAddress> findByUserId(Long userId);

        Optional<UserAddress> findByUserIdAndIsDefaultTrue(Long userId);

        @Modifying
        @Query(value = """
                        UPDATE user_addresses
                        SET is_default = FALSE
                        WHERE user_id = :userId
                        """, nativeQuery = true)
        void resetDefaultAddress(Long userId);

        @Modifying
        @Query(value = """
                        UPDATE user_addresses
                        SET is_default = TRUE
                        WHERE user_address_id = :addressId
                        """, nativeQuery = true)
        void setDefaultAddress(Long addressId);
}
