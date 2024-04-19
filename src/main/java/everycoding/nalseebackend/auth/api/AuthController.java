package everycoding.nalseebackend.auth.api;

import everycoding.nalseebackend.auth.dto.request.DeleteRequestDto;
import everycoding.nalseebackend.auth.dto.request.SignupRequestDto;
import everycoding.nalseebackend.auth.dto.request.UserResponse;
import everycoding.nalseebackend.user.service.UserService;
import everycoding.nalseebackend.user.domain.User;
import everycoding.nalseebackend.webclient.DeleteUserNotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final UserService userService;
    private final DeleteUserNotificationService deleteUserNotificationService;
    @GetMapping("/api/index")
    public UserResponse getUserInfo() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName(); // JWT에서 사용자의 이메일 가져오기

        // 이메일을 사용하여 사용자 정보 조회
        User user = userService.findByEmail(email);

        // 필요한 정보만 UserResponse 객체에 담아 반환
        return new UserResponse(user.getId(), user.getUsername(), user.getEmail(), user.getPicture() ,user.isNewUser());
    }

    @PostMapping("/api/signup")
    public ResponseEntity<?> signUpUser(@RequestBody SignupRequestDto signupRequestDto) {
        userService.signUpUser(signupRequestDto);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/api/delete")
    public ResponseEntity<?> deleteUser(@RequestBody DeleteRequestDto deleteRequestDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName(); // JWT에서 사용자의 이메일 가져오기
        log.info("사용자의 email = {} ", email);
        User user = userService.findByEmail(email);
        String username = user.getUsername();
        log.info("사용자의 이름 = {}", username);

        if(!email.equals(deleteRequestDto.getEmail()) || !username.equals(deleteRequestDto.getUsername())){
        return ResponseEntity.status(400).body("email 또는 이름을 확인해주세요");
        }
        userService.deleteUser(deleteRequestDto);
        deleteUserNotificationService.checkDeleteUser(user.getId()).subscribe(
                null,
                error -> log.error("Failed to send user deletion notification", error),
                () -> log.info("User deletion notification sent successfully")

        );
        return ResponseEntity.ok().build();
    }
}
