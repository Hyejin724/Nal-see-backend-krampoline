package everycoding.nalseebackend.comment.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class CommentResponseDto {
    private Long id;
    private String content;
    private Integer likeCNT;
    private Boolean isLiked;
    private LocalDateTime createDate;

    private Long userId;
    private String userImage;
    private String username;

    private Long postId;

    @Builder
    public CommentResponseDto(Long id, String content, Integer likeCNT, Boolean isLiked, LocalDateTime createDate, Long userId, String userImage, String username, Long postId) {
        this.id = id;
        this.content = content;
        this.likeCNT = likeCNT;
        this.isLiked = isLiked;
        this.createDate = createDate;
        this.userId = userId;
        this.userImage = userImage;
        this.username = username;
        this.postId = postId;
    }
}
