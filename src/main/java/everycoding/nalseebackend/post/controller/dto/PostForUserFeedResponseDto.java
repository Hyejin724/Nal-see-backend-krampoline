package everycoding.nalseebackend.post.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class PostForUserFeedResponseDto {
    private Long postId;
    private String postPicture;
    private Boolean isMany;
}
