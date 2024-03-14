package everycoding.nalseebackend.auth.dto.response;

import lombok.Data;

@Data
public class UserDto {
    private Long userId;

    private String accessToken;

    private String refreshToken;

//    private Integer expTime = 3600;

    private Integer expTime = 120;
}