package everycoding.nalseebackend.post.controller.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class PostResponseDto {
    private Long id;
    private List<String> pictureList;
    private String content;
    private Integer likeCnt;
    private Boolean liked;
    private LocalDateTime createDate;

    private String address;

    private String weather;
    private Double temperature;

    private Long userId;
    private String username;
    private String userImage;
}
