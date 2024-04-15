package everycoding.nalseebackend.post.service.info;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class PostForUserFeedInfo {
    private Long postId;
    private String postPicture;
    private Boolean isMany;
}
