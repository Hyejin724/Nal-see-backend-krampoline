package everycoding.nalseebackend.user.repository;

import everycoding.nalseebackend.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findAllByRefreshToken(String refreshToken);
    Optional<User> findByEmail(String email);




}
