package everycoding.nalseebackend.firebase.dto;

import lombok.*;
import org.springframework.lang.Nullable;

@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FcmSendDto {
    private String token;

    private String title;

    private String body;

    @Nullable
    @Setter
    private Long commentId;

    @Nullable
    @Setter
    private Long postId;

    @Nullable
    @Setter
    private Long userId;

    @Builder
    public FcmSendDto(String token, String title, String body, Long commentId, Long postId, Long userId) {
        this.token = token;
        this.title = title;
        this.body = body;
        this.commentId = commentId;
        this.postId = postId;
        this.userId = userId;
    }
}