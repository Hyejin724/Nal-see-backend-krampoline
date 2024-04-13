package everycoding.nalseebackend.firebase.alarm.dto;

import lombok.Builder;
import lombok.Data;
import org.springframework.lang.Nullable;

import java.time.LocalDateTime;

@Data
public class AlarmDto {

    private Long id;
    private String message;
    private Long senderId;
    private String senderName;
    private String senderImage;
    private Boolean isRead;
    private LocalDateTime createAt;

    @Nullable
    private Long commentId;

    @Nullable
    private Long postId;

    @Nullable
    private Long userId;

    @Builder
    public AlarmDto(Long id, String message, Long senderId, String senderName, String senderImage, Boolean isRead, LocalDateTime createAt, @Nullable Long commentId, @Nullable Long postId, @Nullable Long userId) {
        this.id = id;
        this.message = message;
        this.senderId = senderId;
        this.senderName = senderName;
        this.senderImage = senderImage;
        this.isRead = isRead;
        this.createAt = createAt;
        this.commentId = commentId;
        this.postId = postId;
        this.userId = userId;
    }
}
