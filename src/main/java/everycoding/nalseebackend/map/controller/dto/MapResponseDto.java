package everycoding.nalseebackend.map.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MapResponseDto {
    private Double bottomLeftLat;
    private Double bottomLeftLong;
    private Double topRightLat;
    private Double topRightLong;

    private String picture;
    private Integer count;
}
