package everycoding.nalseebackend.comment.controller.dto;

import lombok.Getter;

@Getter
public class CommentRequestDto {
    private String content;
    private Long userId;
}
