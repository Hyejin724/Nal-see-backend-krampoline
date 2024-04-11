package everycoding.nalseebackend.map.service.info;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;

@Data
@Builder
@AllArgsConstructor
public class PostsInMapInfo {
    private Double bottomLeftLat;
    private Double bottomLeftLong;
    private Double topRightLat;
    private Double topRightLong;

    private String picture;
    private Integer count;
}
