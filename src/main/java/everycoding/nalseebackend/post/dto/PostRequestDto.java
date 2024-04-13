package everycoding.nalseebackend.post.dto;

import everycoding.nalseebackend.user.domain.UserInfo;
import lombok.Getter;

import java.util.List;

@Getter
public class PostRequestDto {

    private Long userId;

    private String content;

    private String address;
    private Double latitude;
    private Double longitude;

    private UserInfo userInfo;
}
