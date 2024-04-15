package everycoding.nalseebackend.comment.service.info;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
public class CommentInfo {
    private Long id;
    private String content;
    private Integer likeCNT;
    private Boolean isLiked;
    private LocalDateTime createDate;

    private Long userId;
    private String userImage;
    private String username;

    private Long postId;
}
