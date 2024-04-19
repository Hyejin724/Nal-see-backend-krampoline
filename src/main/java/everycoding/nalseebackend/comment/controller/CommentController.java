package everycoding.nalseebackend.comment.controller;

import everycoding.nalseebackend.Mapper;
import everycoding.nalseebackend.api.ApiResponse;
import everycoding.nalseebackend.auth.customUser.CustomUserDetails;
import everycoding.nalseebackend.comment.service.CommentService;
import everycoding.nalseebackend.comment.controller.dto.CommentRequestDto;
import everycoding.nalseebackend.comment.controller.dto.CommentResponseDto;
import everycoding.nalseebackend.firebase.alarm.AlarmService;
import everycoding.nalseebackend.firebase.alarm.domain.AlarmType;
import everycoding.nalseebackend.user.service.UserService;
import everycoding.nalseebackend.user.domain.User;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;
    private final UserService userService;
    private final AlarmService alarmService;
    private final Mapper mapper;

    // 댓글 조회
    @GetMapping("/api/posts/{postId}/comments")
    public ApiResponse<List<CommentResponseDto>> getComments(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @PathVariable Long postId) {
        return ApiResponse.ok(
                commentService.getComments(customUserDetails.getId(), postId)
                        .stream()
                        .map(mapper::toDto)
                        .collect(Collectors.toList())
        );
    }

    // 댓글 작성
    @PostMapping("/api/posts/{postId}/comments")
    public ApiResponse<Void> writeComment(
            @PathVariable Long postId,
            @RequestBody CommentRequestDto requestDto,
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

        String userToken = userService.findUserTokenByPostId(postId);
        User userByPostId = userService.findUserByPostId(postId);
        String title = "새로운 댓글 알림";
        String message = username + "님께서 댓글을 작성했습니다.";
        alarmService.sendFcmAndSaveAlarm(userByPostId, user, username, userToken, title, message, postId, AlarmType.POST);
        commentService.writeComment(postId, requestDto);
        return ApiResponse.ok();
    }

    // 댓글 수정
    @PatchMapping("/api/posts/{postId}/comments/{commentId}")
    public ApiResponse<Void> updateComment(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @PathVariable Long postId,
            @PathVariable Long commentId,
            @RequestBody CommentRequestDto requestDto
    ) {
        commentService.updateComment(customUserDetails.getId(), postId, commentId, requestDto);
        return ApiResponse.ok();
    }

    // 댓글 삭제
    @DeleteMapping("/api/posts/{postId}/comments/{commentId}")
    public ApiResponse<Void> deleteComment(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @PathVariable Long postId,
            @PathVariable Long commentId
    ) {
        commentService.deleteComment(customUserDetails.getId(), postId, commentId);
        return ApiResponse.ok();
    }

    // 댓글 좋아요
    @PostMapping("/api/posts/{postId}/comment/{commentId}/likes")
    public ApiResponse<Void> likeComment(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @PathVariable Long postId,
            @PathVariable Long commentId,
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

        String userToken = userService.findUserTokenByCommentId(commentId);
        User userByCommentId = userService.findUserByCommentId(commentId);
        String message = username + "님이 댓글에 좋아요를 눌렀습니다.";
        String title = "좋아요 알람";
        alarmService.sendFcmAndSaveAlarm(userByCommentId, user, username, userToken, title, message, commentId, AlarmType.COMMENT);
        commentService.likeComment(customUserDetails.getId(), postId, commentId);
        return ApiResponse.ok();
    }

    // 댓글 좋아요 취소
    @PostMapping("/api/posts/{postId}/comment/{commentId}/likes/cancel")
    public ApiResponse<Void> cancelLikeComment(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @PathVariable Long postId,
            @PathVariable Long commentId
    ) {
        commentService.cancelLikeComment(customUserDetails.getId(), postId, commentId);
        return ApiResponse.ok();
    }
}
