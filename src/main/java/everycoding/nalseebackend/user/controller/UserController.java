package everycoding.nalseebackend.user.controller;

import everycoding.nalseebackend.Mapper;
import everycoding.nalseebackend.api.ApiResponse;
import everycoding.nalseebackend.auth.customUser.CustomUserDetails;
import everycoding.nalseebackend.firebase.alarm.AlarmService;
import everycoding.nalseebackend.firebase.alarm.domain.AlarmType;
import everycoding.nalseebackend.user.controller.dto.FollowUserDto;
import everycoding.nalseebackend.user.controller.dto.UserDetailDto;
import everycoding.nalseebackend.user.repository.UserRepository;
import everycoding.nalseebackend.user.service.UserService;
import everycoding.nalseebackend.user.domain.User;
import everycoding.nalseebackend.user.controller.dto.UserFeedResponseDto;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final UserRepository userRepository;
    private final AlarmService alarmService;
    private final Mapper mapper;

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
        if (userToken == null || userToken.isEmpty()) {
            userToken = "error";
        }
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
    public ApiResponse<UserDetailDto> getUserInfo(@AuthenticationPrincipal CustomUserDetails customUserDetails) {
        return ApiResponse.ok(mapper.toDto(userService.getUserInfo(customUserDetails.getId())));
    }

    // 유저 개인정보 등록
    @PostMapping("/api/users/userInfo")
    public ApiResponse<Void> setUserInfo(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @RequestBody UserDetailDto requestDto
    ) {
        userService.setUserInfo(customUserDetails.getId(), mapper.toInfo(requestDto));
        return ApiResponse.ok();
    }

    // 개인 피드 페이지
    @GetMapping("/api/users/{userId}/feed")
    public ApiResponse<UserFeedResponseDto> getFeed(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @PathVariable Long userId
    ) {
        return ApiResponse.ok(mapper.toDto(userService.getFeed(customUserDetails.getId(), userId)));
    }

    @GetMapping("/api/users/{userId}/following")
    public ApiResponse<List<FollowUserDto>> getFollowingList(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @PathVariable Long userId
    ) {
        return ApiResponse.ok(
                userService.getFollowingList(customUserDetails.getId(), userId)
                        .stream()
                        .map(mapper::toDto)
                        .toList()
        );
    }

    @GetMapping("/api/users/{userId}/follower")
    public ApiResponse<List<FollowUserDto>> getFollowerList(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @PathVariable Long userId
    ) {
        return ApiResponse.ok(
                userService.getFollowerList(customUserDetails.getId(), userId)
                        .stream()
                        .map(mapper::toDto)
                        .toList()
        );
    }
}
