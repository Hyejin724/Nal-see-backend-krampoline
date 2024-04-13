package everycoding.nalseebackend.webclient.dto;

import lombok.Builder;
import lombok.Data;

@Data
public class UserInfo {

    private Long userId;
    private String userName;
    private String userImg;

    @Builder
    public UserInfo(Long userId, String userName, String userImg) {
        this.userId = userId;
        this.userName = userName;
        this.userImg = userImg;
    }
}
