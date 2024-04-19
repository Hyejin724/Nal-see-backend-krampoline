package everycoding.nalseebackend.user.service.info;

import everycoding.nalseebackend.user.domain.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
public class UserDetailInfo {
    private String username;
    private Double height;
    private Double weight;
    private String constitution;
    private List<String> style;
    private String gender;

    public static UserDetailInfo createUserDetailInfo(User user) {
        return UserDetailInfo.builder()
                .username(user.getUsername())
                .height(user.getUserDetail().getHeight())
                .weight(user.getUserDetail().getWeight())
                .constitution(String.valueOf(user.getUserDetail().getConstitution()))
                .style(user.getUserDetail().getStyle().stream().map(String::valueOf).toList())
                .gender(String.valueOf(user.getUserDetail().getGender()))
                .build();
    }
}
