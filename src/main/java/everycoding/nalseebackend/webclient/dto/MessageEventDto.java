package everycoding.nalseebackend.webclient.dto;

import lombok.Builder;
import lombok.Data;


@Data
public class MessageEventDto {

    private Long senderId;
    private Long receiverId;
    private String senderName;
    private String receiverName;
    private String message;

    @Builder
    public MessageEventDto(Long senderId, Long receiverId, String senderName, String receiverName, String message) {
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.senderName = senderName;
        this.receiverName = receiverName;
        this.message = message;
    }
}
