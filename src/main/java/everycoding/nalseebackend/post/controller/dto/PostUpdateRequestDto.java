package everycoding.nalseebackend.post.controller.dto;

import everycoding.nalseebackend.user.controller.dto.UserDetailDto;
import everycoding.nalseebackend.user.domain.UserDetail;
import lombok.Getter;

@Getter
public class PostUpdateRequestDto {
    private String content;
    private UserDetailDto userDetailDto;
}
