package everycoding.nalseebackend.firebase;

import everycoding.nalseebackend.api.ApiResponse;
import everycoding.nalseebackend.auth.jwt.JwtTokenProvider;
import everycoding.nalseebackend.firebase.dto.FcmSendDto;
import everycoding.nalseebackend.user.UserRepository;
import everycoding.nalseebackend.user.domain.User;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/fcm")
public class FcmController {

    private final FcmService fcmService;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;


    @PostMapping("/send")
    public ResponseEntity<ApiResponse<Object>> pushMessage(@RequestBody @Validated FcmSendDto fcmSendDto) throws IOException {
        log.debug("[+] 푸시 메시지를 전송합니다. ");
        int result = fcmService.sendMessageTo(fcmSendDto);

        ApiResponse<Object> arw = ApiResponse.ok(result);
        return new ResponseEntity<>(arw, HttpStatus.OK);
    }

    @PostMapping("/fcmtoken")
    public void saveToken(HttpServletRequest request) {
        String fcmToken = request.getHeader("Fcmtoken");
        String token = "";
        Cookie[] cookies = request.getCookies();
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals("AccessToken")) {
                token = cookie.getValue();
                log.info("token={}", token);
            }
        }
        Claims claims = jwtTokenProvider.getClaims(token);
        String userEmail = claims.getSubject();
        Optional<User> byEmail = userRepository.findByEmail(userEmail);
        User user = byEmail.orElseThrow();
        user.setFcmToken(fcmToken);

        userRepository.save(user);
    }
}
