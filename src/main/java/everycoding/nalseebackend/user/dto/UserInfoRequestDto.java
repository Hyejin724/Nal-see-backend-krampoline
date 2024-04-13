package everycoding.nalseebackend.user.dto;

import everycoding.nalseebackend.user.domain.Constitution;
import everycoding.nalseebackend.user.domain.FashionStyle;
import everycoding.nalseebackend.user.domain.Gender;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class UserInfoRequestDto {
    private String username;
    private Double height;
    private Double weight;
    private Constitution constitution;
    private List<FashionStyle> style;
    private Gender gender;
}
