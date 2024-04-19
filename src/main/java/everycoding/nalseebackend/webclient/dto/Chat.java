package everycoding.nalseebackend.webclient.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Chat {

    private String id;
    private String chatId;
    private String msg;
    private Long senderId;
    private String sender;
    private String senderImg;
    private Long receiverId;
    private String receiver;
    private String receiverImg;
    private Integer readCnt;

    private LocalDateTime createAt;

    @Builder
    public Chat(String id, String chatId, String msg, Long senderId, String sender, String senderImg, Long receiverId, String receiver, String receiverImg, Integer readCnt, LocalDateTime createAt) {
        this.id = id;
        this.chatId = chatId;
        this.msg = msg;
        this.senderId = senderId;
        this.sender = sender;
        this.senderImg = senderImg;
        this.receiverId = receiverId;
        this.receiver = receiver;
        this.receiverImg = receiverImg;
        this.readCnt = readCnt;
        this.createAt = createAt;
    }
}
