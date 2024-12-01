package id.orbion.ecommerce_app.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import id.orbion.ecommerce_app.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    @Query(value = """
            SELECT * FROM users
            WHERE username = :keyword OR
            email = :keyword
            """, nativeQuery = true)
    Optional<User> findByKeyword(String keyword);

    Boolean existsByUsername(String username);

    Boolean existsByEmail(String email);

    @Query(value = """
            SELECT * FROM users
            WHERE lower(username) LIKE :keyword OR lower(email) LIKE :keyword
            """, nativeQuery = true)
    Page<User> searchUser(String keyword, Pageable pageable);
}
