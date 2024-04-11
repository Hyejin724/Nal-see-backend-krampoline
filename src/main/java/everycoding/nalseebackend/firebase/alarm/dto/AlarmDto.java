package everycoding.nalseebackend.firebase.alarm.dto;

import lombok.Builder;
import lombok.Data;
import org.springframework.lang.Nullable;

import java.time.LocalDateTime;

@Data
public class AlarmDto {

    private String message;
    private Long senderId;
    private String senderName;
    private String senderImage;
    private LocalDateTime createAt;

    @Nullable
    private Long commentId;

    @Nullable
    private Long postId;

    @Nullable
    private Long userId;

    @Builder
    public AlarmDto(String message, Long senderId, String senderName, String senderImage, LocalDateTime createAt, @Nullable Long commentId, @Nullable Long postId, @Nullable Long userId) {
        this.message = message;
        this.senderId = senderId;
        this.senderName = senderName;
        this.senderImage = senderImage;
        this.createAt = createAt;
        this.commentId = commentId;
        this.postId = postId;
        this.userId = userId;
    }
}
