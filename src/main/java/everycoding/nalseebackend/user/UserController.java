package everycoding.nalseebackend.user;

import everycoding.nalseebackend.api.ApiResponse;
import everycoding.nalseebackend.auth.customUser.CustomUserDetails;
import everycoding.nalseebackend.firebase.alarm.AlarmService;
import everycoding.nalseebackend.firebase.alarm.domain.AlarmType;
import everycoding.nalseebackend.user.domain.User;
import everycoding.nalseebackend.user.dto.UserFeedResponseDto;
import everycoding.nalseebackend.user.dto.UserInfoRequestDto;
import everycoding.nalseebackend.user.dto.UserInfoResponseDto;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final UserRepository userRepository;
    private final AlarmService alarmService;

    // 팔로우
    @PostMapping("/api/users/{userId}/follow")
    public ApiResponse<Void> followUser(
            @PathVariable Long userId,
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            HttpServletRequest request
    ) throws IOException {
        String token = "";
        Cookie[] cookies = request.getCookies();
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals("AccessToken")) {
                token = cookie.getValue();
            }
        }
        User user = userService.findUserByJwt(token);
        String username = user.getUsername();

        Optional<User> byId = userRepository.findById(userId);
        User owner = byId.orElseThrow();
        String userToken = owner.getFcmToken();
        String message = username +"님이 팔로우를 시작했습니다.";
        String title = "팔로우 알림";
        alarmService.sendFcmAndSaveAlarm(owner, user, username, userToken, title, message, user.getId(), AlarmType.USER);
        userService.followUser(userId, customUserDetails.getId());
        return ApiResponse.ok();
    }

    // 언팔로우
    @PostMapping("/api/users/{userId}/unfollow")
    public ApiResponse<Void> unfollowUser(
            @PathVariable Long userId,
            @AuthenticationPrincipal CustomUserDetails customUserDetails
    ) {
        userService.unfollowUser(userId, customUserDetails.getId());
        return ApiResponse.ok();
    }

    // 유저 개인정보 조회
    @GetMapping("/api/users/userInfo")
    public ApiResponse<UserInfoResponseDto> getUserInfo(@AuthenticationPrincipal CustomUserDetails customUserDetails) {
        return ApiResponse.ok(userService.getUserInfo(customUserDetails.getId()));
    }

    // 유저 개인정보 등록
    @PostMapping("/api/users/userInfo")
    public ApiResponse<Void> setUserInfo(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @RequestBody UserInfoRequestDto requestDto
    ) {
        userService.setUserInfo(customUserDetails.getId(), requestDto);
        return ApiResponse.ok();
    }

    // 개인 피드 페이지
    @GetMapping("/api/users/{userId}/feed")
    public ApiResponse<UserFeedResponseDto> getFeed(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @PathVariable Long userId
    ) {
        return ApiResponse.ok(userService.getFeed(customUserDetails.getId(), userId));
    }
}
