package everycoding.nalseebackend.user.service;

import everycoding.nalseebackend.Mapper;
import everycoding.nalseebackend.api.exception.BaseException;
import everycoding.nalseebackend.auth.dto.request.DeleteRequestDto;
import everycoding.nalseebackend.auth.jwt.JwtTokenProvider;
import everycoding.nalseebackend.comment.repository.CommentRepository;
import everycoding.nalseebackend.comment.domain.Comment;
import everycoding.nalseebackend.post.repository.PostRepository;
import everycoding.nalseebackend.post.domain.Post;
import everycoding.nalseebackend.user.repository.UserRepository;
import everycoding.nalseebackend.user.service.info.FollowUserInfo;
import everycoding.nalseebackend.user.service.info.UserDetailInfo;
import everycoding.nalseebackend.user.service.info.UserFeedInfo;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;

import everycoding.nalseebackend.auth.dto.request.SignupRequestDto;
import everycoding.nalseebackend.auth.exception.EmailAlreadyUsedException;
import everycoding.nalseebackend.user.domain.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final CommentRepository commentRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final Mapper mapper;

    public void followUser(Long userId, Long myId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new BaseException("wrong userId"));
        User me = userRepository.findById(myId).orElseThrow(() -> new BaseException("wrong userId"));

        me.follow(user);

        userRepository.save(me);
    }

    public void unfollowUser(Long userId, Long myId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new BaseException("wrong userId"));
        User me = userRepository.findById(myId).orElseThrow(() -> new BaseException("wrong userId"));

        me.unfollow(user);

        userRepository.save(me);
    }

    @Transactional(readOnly = true)
    public UserDetailInfo getUserInfo(long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new BaseException("wrong userId"));
        return UserDetailInfo.createUserDetailInfo(user);
    }

    public void setUserInfo(long userId, UserDetailInfo userDetailInfo) {
        User user = userRepository.findById(userId).orElseThrow(() -> new BaseException("wrong userId"));

        // 새로운 username이 제공되었는지 확인하고 업데이트
        if (userDetailInfo.getUsername() != null && !userDetailInfo.getUsername().equals(user.getUsername())) {
            user.setUsername(userDetailInfo.getUsername());
        }
        user.setUserDetail(
                mapper.toUserDetail(userDetailInfo)
        );
        user.setNewUser(false);
        userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public UserFeedInfo getFeed(long myId, long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new BaseException("wrong userId"));
        User me = userRepository.findById(myId).orElseThrow(() -> new BaseException("wrong userId"));

        return UserFeedInfo.createUserFeedInfo(user, me);
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
    }

    public void signUpUser(SignupRequestDto signupRequestDto) {
        if (userRepository.findByEmail(signupRequestDto.getEmail()).isPresent()) {
            throw new EmailAlreadyUsedException("이미 사용 중인 이메일입니다.");
        }

        User user = User.builder()
                .username(signupRequestDto.getUsername())
                .email(signupRequestDto.getEmail())
                .password(passwordEncoder.encode(signupRequestDto.getPassword()))
                .role("USER")
                .build();
        userRepository.save(user);

        log.info("회원가입 완료");
    }

    public String findUserTokenByPostId(Long postId) {
        Optional<Post> byId = postRepository.findById(postId);
        Post post = byId.orElseThrow();
        User user = post.getUser();
        String fcmToken = user.getFcmToken();
        if (fcmToken == null || fcmToken.isEmpty()) {
            return "error";
        }
        return fcmToken;
    }

    public User findUserByPostId(Long postId) {
        Optional<Post> byId = postRepository.findById(postId);
        Post post = byId.orElseThrow();

        return post.getUser();
    }

    public String findUserTokenByCommentId(Long commentId) {
        Optional<Comment> byId = commentRepository.findById(commentId);
        Comment comment = byId.orElseThrow();
        User user = comment.getUser();
        String fcmToken = user.getFcmToken();
        if (fcmToken == null ||fcmToken.isEmpty()) {
            return "error";
        }
        return fcmToken;
    }

    public User findUserByCommentId(Long commentId) {
        Optional<Comment> byId = commentRepository.findById(commentId);
        Comment comment = byId.orElseThrow();
        return comment.getUser();
    }

    public User findUserByJwt(String token) {
        Claims claims = jwtTokenProvider.getClaims(token);
        String userEmail = claims.getSubject();
        Optional<User> byEmail = userRepository.findByEmail(userEmail);
        return byEmail.orElseThrow();
    }

    @Transactional
    public void deleteUser(DeleteRequestDto deleteRequestDto) {
        User user = userRepository.findByEmail(deleteRequestDto.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + deleteRequestDto.getEmail()));

        // 팔로잉 목록 해제
        for (User following : user.getFollowings()) {
            following.getFollowers().remove(user);
        }
        user.getFollowings().clear();

        // 팔로워 목록 해제
        for (User follower : user.getFollowers()) {
            follower.getFollowings().remove(user);
        }
        user.getFollowers().clear();

        // 사용자 삭제
        userRepository.delete(user);

        // 포스트 삭제
        postRepository.deleteByUser(user);

        //댓글 삭제
        commentRepository.deleteByUser(user);

        log.info("회원 탈퇴 완료: " + user.getUsername());
    }

    public List<FollowUserInfo> getFollowingList(Long myId,Long userId) {
        User me = userRepository.findById(myId).orElseThrow(() -> new BaseException("wrong userId"));
        User user = userRepository.findById(userId).orElseThrow(() -> new BaseException("wrong userId"));

        return user.getFollowings()
                .stream()
                .map(following -> FollowUserInfo.createFollowUserInfo(following, me))
                .toList();
    }

    public List<FollowUserInfo> getFollowerList(Long myId, Long userId) {
        User me = userRepository.findById(myId).orElseThrow(() -> new BaseException("wrong userId"));
        User user = userRepository.findById(userId).orElseThrow(() -> new BaseException("wrong userId"));

        return user.getFollowers()
                .stream()
                .map(follower -> FollowUserInfo.createFollowUserInfo(follower, me))
                .toList();
    }
}
