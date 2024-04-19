package everycoding.nalseebackend.user.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FollowUserDto {
    private Long userId;
    private String username;
    private String picture;
    private Boolean isFollowed;
}
