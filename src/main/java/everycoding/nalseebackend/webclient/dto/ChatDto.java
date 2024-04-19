package everycoding.nalseebackend.webclient.dto;

import lombok.Builder;
import lombok.Data;
import lombok.Setter;

import java.time.LocalDateTime;

@Data
public class ChatDto {

    private Chat chat;
    private Boolean isOnline;

    @Builder
    public ChatDto(Chat chat, Boolean isOnline) {
        this.chat = chat;
        this.isOnline = isOnline;
    }
}
