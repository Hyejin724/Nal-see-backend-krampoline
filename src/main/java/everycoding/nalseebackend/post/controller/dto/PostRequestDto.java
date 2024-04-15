package everycoding.nalseebackend.post.controller.dto;

import everycoding.nalseebackend.user.controller.dto.UserDetailDto;
import lombok.Getter;

@Getter
public class PostRequestDto {

    private Long userId;

    private String content;

    private String address;
    private Double latitude;
    private Double longitude;

    private UserDetailDto userDetailDto;
}
