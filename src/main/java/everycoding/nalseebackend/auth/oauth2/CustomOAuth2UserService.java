package everycoding.nalseebackend.auth.oauth2;

import com.amazonaws.util.StringUtils;
import everycoding.nalseebackend.auth.customUser.CustomUserDetails;
import everycoding.nalseebackend.auth.exception.OAuth2AuthenticationProcessingException;
import everycoding.nalseebackend.user.repository.UserRepository;
import everycoding.nalseebackend.user.domain.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        log.info("OAuth user loading...");

        try {
            return processOAuth2User(userRequest, oAuth2User);
        } catch (Exception ex) {
            throw new InternalAuthenticationServiceException(ex.getMessage(), ex.getCause());
        }
    }

    private OAuth2User processOAuth2User(OAuth2UserRequest oAuth2UserRequest, OAuth2User oAuth2User) {
        OAuth2UserInfo oAuth2UserInfo = OAuth2UserInfoFactory.getOAuth2UserInfo(oAuth2UserRequest.getClientRegistration().getRegistrationId(), oAuth2User.getAttributes());
        log.info("Email={}", oAuth2UserInfo.getEmail());
        log.info("Id={}", oAuth2UserInfo.getId());
        log.info("Name={}", oAuth2UserInfo.getName());

        if (StringUtils.isNullOrEmpty(oAuth2UserInfo.getEmail())) {
            log.info("Email is Null or Empty");
            throw new OAuth2AuthenticationProcessingException("Email not found from OAuth2 provider");
        }

        log.info("Find User By Email");
        Optional<User> byEmail = userRepository.findByEmail(oAuth2UserInfo.getEmail());
        User user;
        if (byEmail.isPresent()) {
            user = byEmail.get();
            if (!user.getProvider().equals(AuthProvider.valueOf((oAuth2UserRequest.getClientRegistration().getRegistrationId())))) {
                log.info("provider Id is True");
                throw new OAuth2AuthenticationProcessingException("Looks like you're signed up with " +
                        user.getProvider() + " account. Please use your " + user.getProvider() +
                        " account to login.");
            }
            log.info("Is different Provider Id then Process Update User");
        } else {
            log.info("Welcome New User, Sign In");
            user = registerNewUser(oAuth2UserRequest, oAuth2UserInfo);
        }
        return CustomUserDetails.create(user, oAuth2User.getAttributes());
    }

    private User registerNewUser(OAuth2UserRequest oAuth2UserRequest, OAuth2UserInfo oAuth2UserInfo) {
        AuthProvider provider = AuthProvider.valueOf(oAuth2UserRequest.getClientRegistration().getRegistrationId());
        String providerId = oAuth2UserInfo.getId();
        String username = oAuth2UserInfo.getName();
        String email = oAuth2UserInfo.getEmail();
        String imageUrl = oAuth2UserInfo.getImageUrl();
        String role = "USER";
        log.info("find all info from New User");

        User newUser = User.createNewUser(provider, providerId, username, email, imageUrl, role);
        log.info("provider={}", provider);
        log.info("providerId={}", providerId);
        log.info("name={}", username);
        log.info("email={}", email);
        log.info("imageUrl={}", imageUrl);

        return userRepository.save(newUser);
    }
}
