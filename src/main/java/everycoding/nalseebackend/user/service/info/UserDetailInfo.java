package everycoding.nalseebackend.user.service.info;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class UserDetailInfo {
    private Double height;
    private Double weight;
    private String constitution;
    private List<String> style;
    private String gender;
}
