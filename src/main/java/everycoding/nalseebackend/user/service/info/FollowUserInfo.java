package everycoding.nalseebackend.user.service.info;

import everycoding.nalseebackend.user.domain.User;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FollowUserInfo {
    private Long userId;
    private String username;
    private String picture;
    private Boolean isFollowed;

    public static FollowUserInfo createFollowUserInfo(User user, User me) {
        return new FollowUserInfo(user.getId(), user.getUsername(), user.getPicture(), me.isFollowed(user));
    }
}
