package everycoding.nalseebackend.webclient;

import everycoding.nalseebackend.auth.jwt.JwtTokenProvider;
import everycoding.nalseebackend.firebase.FcmService;
import everycoding.nalseebackend.firebase.dto.FcmSendDto;
import everycoding.nalseebackend.user.UserRepository;
import everycoding.nalseebackend.user.domain.User;
import everycoding.nalseebackend.webclient.dto.MessageEventDto;
import everycoding.nalseebackend.webclient.dto.UserInfo;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@Slf4j
public class ChatServerController {

    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;
    private final FcmService fcmService;

    @GetMapping("/user/info")
    public UserInfo getUser(HttpServletRequest request) {
        String authorization = request.getHeader("Authorization");
        log.info("token={}", authorization);
        Claims claims = jwtTokenProvider.getClaims(authorization);
        String subject = claims.getSubject();
        Optional<User> byEmail = userRepository.findByEmail(subject);
        User user = byEmail.orElseThrow();

        return UserInfo.builder()
                .userName(user.getUsername())
                .userId(user.getId())
                .userImg(user.getPicture())
                .build();
    }

    @GetMapping("/user/exist")
    public UserInfo checkUserExistence(@RequestParam Long userId) {
        User user = userRepository.findById(userId).orElseThrow();
        return UserInfo.builder()
                .userName(user.getUsername())
                .userId(user.getId())
                .userImg(user.getPicture())
                .build();
    }

    @PostMapping("/msg-alarm")
    public void messageEvent(@RequestBody MessageEventDto messageEventDto) throws IOException {
        log.info("fcm메시지왔어영");
        Optional<User> byId = userRepository.findById(messageEventDto.getReceiverId());
        User receiver = byId.orElseThrow();
        String fcmToken = receiver.getFcmToken();
        log.info("messageEvent 진입, 메시지: {}", messageEventDto);
        log.info(messageEventDto.getSenderName() + "님께서 메시지를 보냈습니다.");
        if (fcmToken != null && !fcmToken.isEmpty()) {
            if (!fcmToken.equals("error")) {
                // FCM 메시지 생성 및 전송
                FcmSendDto fcmSendDto = FcmSendDto.builder()
                        .token(fcmToken)
                        .title(messageEventDto.getSenderName() + "님께서 메시지를 보냈습니다.")
                        .body(messageEventDto.getMessage())
                        .build();

                fcmService.sendMessageTo(fcmSendDto);
            }
        }

    }
}
