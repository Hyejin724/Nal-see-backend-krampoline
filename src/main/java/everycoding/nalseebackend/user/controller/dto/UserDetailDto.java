package everycoding.nalseebackend.user.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class UserDetailDto {
    private String username;
    private Double height;
    private Double weight;
    private String constitution;
    private List<String> style;
    private String gender;
}
