package everycoding.nalseebackend.user;

import everycoding.nalseebackend.api.exception.BaseException;
import everycoding.nalseebackend.auth.jwt.JwtTokenProvider;
import everycoding.nalseebackend.comment.CommentRepository;
import everycoding.nalseebackend.comment.domain.Comment;
import everycoding.nalseebackend.post.PostRepository;
import everycoding.nalseebackend.post.domain.Post;
import everycoding.nalseebackend.user.domain.UserInfo;
import everycoding.nalseebackend.user.dto.UserFeedResponseDto;
import everycoding.nalseebackend.user.dto.UserInfoRequestDto;
import everycoding.nalseebackend.user.dto.UserInfoResponseDto;
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
    public UserInfoResponseDto getUserInfo(long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new BaseException("wrong userId"));
        return UserInfoResponseDto.builder()
                .username(user.getUsername())
                .height(user.getUserInfo().getHeight())
                .weight(user.getUserInfo().getWeight())
                .constitution(user.getUserInfo().getConstitution())
                .style(user.getUserInfo().getStyle())
                .gender(user.getUserInfo().getGender())
                .build();
    }

    public void setUserInfo(long userId, UserInfoRequestDto requestDto) {
        User user = userRepository.findById(userId).orElseThrow(() -> new BaseException("wrong userId"));

        // 새로운 username이 제공되었는지 확인하고 업데이트
        if (requestDto.getUsername() != null && !requestDto.getUsername().equals(user.getUsername())) {
            user.setUsername(requestDto.getUsername());
        }
        user.setUserInfo(
                UserInfo.builder()
                .height(requestDto.getHeight())
                .weight(requestDto.getWeight())
                .constitution(requestDto.getConstitution())
                .style(requestDto.getStyle())
                .gender(requestDto.getGender())
                .build()
        );
        user.setNewUser(false);
        userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public UserFeedResponseDto getFeed(long myId, long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new BaseException("wrong userId"));
        User me = userRepository.findById(myId).orElseThrow(() -> new BaseException("wrong userId"));

        return UserFeedResponseDto.builder()
                .feedCount(user.getPosts().size())
                .followingCount(user.getFollowings().size())
                .followerCount(user.getFollowers().size())
                .userId(user.getId())
                .userImage(user.getPicture())
                .username(user.getUsername())
                .isFollowed(user.getFollowers().contains(me))
                .build();
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

    public void deleteUser(SignupRequestDto signupRequestDto) {
        User user = userRepository.findByEmail(signupRequestDto.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + signupRequestDto.getEmail()));

        // 사용자 삭제
        userRepository.delete(user);

        // 포스트 삭제
        postRepository.deleteByUser(user);

        //댓글 삭제
        commentRepository.deleteByUser(user);



        log.info("회원 탈퇴 완료: " + user.getUsername());
    }

}
