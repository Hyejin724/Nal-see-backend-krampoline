package everycoding.nalseebackend.auth.customUser;

import com.amazonaws.services.kms.model.NotFoundException;
import everycoding.nalseebackend.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import everycoding.nalseebackend.user.repository.UserRepository;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        Optional<User> oUser = userRepository.findByEmail(email);

        User user = oUser
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email :" + email));
        return CustomUserDetails.create(user);
    }
    @Transactional(readOnly = true)
    public User selcetUser(String email) {
        Optional<User> byEmail = userRepository.findByEmail(email);
        User user = byEmail.orElseThrow(() -> new NotFoundException("유저없음"));
        return user;
    }
    public void clearRefreshToken(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("User Not Found with email: " + email));
        user.setRefreshToken(null); // 리프레시 토큰 값 제거
        userRepository.save(user);
    }

}