package everycoding.nalseebackend.post.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PostScoreDto {

    private PostResponseDto postResponseDto;
    private Double score;
}
