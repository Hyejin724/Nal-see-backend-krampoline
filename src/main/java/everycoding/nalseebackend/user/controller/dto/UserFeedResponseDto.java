package everycoding.nalseebackend.user.controller.dto;

import lombok.*;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class UserFeedResponseDto {
    private Integer feedCount;
    private Integer followingCount;
    private Integer followerCount;

    private Long userId;
    private String userImage;
    private String username;

    private Boolean isFollowed;

}
