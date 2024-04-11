package everycoding.nalseebackend.user.dto;

import lombok.*;

import java.util.List;

@Getter
@NoArgsConstructor
public class UserFeedResponseDto {
    private Integer feedCount;
    private Integer followingCount;
    private Integer followerCount;

    private Long userId;
    private String userImage;
    private String username;

    private Boolean isFollowed;

    @Builder
    public UserFeedResponseDto(int feedCount, int followingCount, int followerCount, long userId, String userImage, String username, boolean isFollowed) {
        this.feedCount = feedCount;
        this.followingCount = followingCount;
        this.followerCount = followerCount;
        this.userId = userId;
        this.userImage = userImage;
        this.username = username;
        this.isFollowed = isFollowed;
    }
}
