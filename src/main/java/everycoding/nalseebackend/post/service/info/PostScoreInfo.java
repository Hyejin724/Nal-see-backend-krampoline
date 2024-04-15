package everycoding.nalseebackend.post.service.info;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PostScoreInfo {

    private PostInfo postInfo;
    private Double score;
}
