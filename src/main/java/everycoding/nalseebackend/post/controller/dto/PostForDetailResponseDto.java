package everycoding.nalseebackend.post.controller.dto;

import everycoding.nalseebackend.comment.controller.dto.CommentResponseDto;
import everycoding.nalseebackend.user.controller.dto.UserDetailDto;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class PostForDetailResponseDto {
    private PostResponseDto postResponseDto;
    private UserDetailDto userDetailDto;
    private List<CommentResponseDto> comments;
}
