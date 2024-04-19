package everycoding.nalseebackend.user.domain;

import everycoding.nalseebackend.BaseEntity;
import everycoding.nalseebackend.auth.oauth2.AuthProvider;
import everycoding.nalseebackend.auth.oauth2.OAuth2UserInfo;
import everycoding.nalseebackend.comment.domain.Comment;
import everycoding.nalseebackend.firebase.alarm.domain.Alarm;
import everycoding.nalseebackend.post.domain.Post;
import jakarta.persistence.Entity;
import jakarta.persistence.*;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "users")
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //카카오에서 받아올 username, email, picture
    @Setter
    private String username;
    private String email;
    private String password;
    private String picture;

    private String role;
    @Setter
    private boolean newUser = true;

    @Setter
    private String refreshToken;

    private AuthProvider provider;
    private String providerId;

    @ElementCollection
    private List<Long> postLikeList = new ArrayList<>();

    @ElementCollection
    private List<Long> commentLikeList = new ArrayList<>();

    @Setter
    private String fcmToken;

    @ManyToMany
    @JoinTable(
            name = "user_follow",
            joinColumns = @JoinColumn(name = "follower_id"),
            inverseJoinColumns = @JoinColumn(name = "following_id")
    )
    private Set<User> followings = new HashSet<>();

    @ManyToMany
    private Set<User> followers = new HashSet<>();

    @Setter
    @Embedded
    private UserDetail userDetail;

    @OneToMany(mappedBy = "user", cascade = {CascadeType.ALL})
    private List<Post> posts;

    @OneToMany(mappedBy = "user", cascade = {CascadeType.ALL})
    private List<Comment> comments;

    @OneToMany(mappedBy = "user", cascade = {CascadeType.ALL})
    private List<Alarm> alarms;



    public User(String username, String email) {
        this.username = username;
        this.email = email;
    }

    @Builder
    public User(String username, String email, String password, String picture, String role, String refreshToken, AuthProvider provider, String providerId) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.picture = picture;
        this.role = role;
        this.refreshToken = refreshToken;
        this.provider = provider;
        this.providerId = providerId;
    }

    public static User createNewUser(AuthProvider provider, String providerId, String username, String email, String imageUrl, String role){
        return User.builder()
                .provider(provider)
                .providerId(providerId)
                .username(username)
                .email(email)
                .picture(imageUrl)
                .role(role)
                .build();
    }

    public void addPostLike(Long postId) {
        postLikeList.add(postId);
    }

    public void cancelPostLike(Long postId) {
        postLikeList.remove(postId);
    }

    public void addCommentLike(Long commentId) {
        commentLikeList.add(commentId);
    }

    public void cancelCommentLike(Long commentId) {
        commentLikeList.remove(commentId);
    }

    public void follow(User user) {
        this.followings.add(user);
        user.getFollowers().add(this);
    }

    public void unfollow(User user) {
        this.followings.remove(user);
        user.getFollowers().remove(this);
    }

    public boolean isFollowed(User user) {
        return this.followings.contains(user);
    }


}
