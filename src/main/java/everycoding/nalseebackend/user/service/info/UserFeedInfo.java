package everycoding.nalseebackend.user.service.info;

import everycoding.nalseebackend.user.domain.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class UserFeedInfo {
    private Integer feedCount;
    private Integer followingCount;
    private Integer followerCount;

    private Long userId;
    private String userImage;
    private String username;

    private Boolean isFollowed;

    public static UserFeedInfo createUserFeedInfo(User user, User me) {
        return UserFeedInfo.builder()
                .feedCount(user.getPosts().size())
                .followingCount(user.getFollowings().size())
                .followerCount(user.getFollowers().size())
                .userId(user.getId())
                .userImage(user.getPicture())
                .username(user.getUsername())
                .isFollowed(user.getFollowers().contains(me))
                .build();
    }
}
