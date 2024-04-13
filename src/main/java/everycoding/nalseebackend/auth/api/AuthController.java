package everycoding.nalseebackend.auth.api;

import everycoding.nalseebackend.auth.dto.request.SignupRequestDto;
import everycoding.nalseebackend.auth.dto.request.UserResponse;
import everycoding.nalseebackend.user.UserService;
import everycoding.nalseebackend.user.domain.User;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    @GetMapping("/index")
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
    public ResponseEntity<?> deleteUser(@RequestBody SignupRequestDto signupRequestDto) {
        userService.deleteUser(signupRequestDto);
        return ResponseEntity.ok().build();
    }
}