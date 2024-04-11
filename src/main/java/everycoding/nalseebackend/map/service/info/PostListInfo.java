package everycoding.nalseebackend.map.service.info;

import everycoding.nalseebackend.post.domain.Post;
import everycoding.nalseebackend.post.dto.PostResponseDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
public class PostListInfo {
    private Long id;
    private List<String> pictureList;
    private String content;
    private Integer likeCnt;
    private Boolean isLiked;
    private LocalDateTime createDate;

    private String address;

    private String weather;
    private Double temperature;

    private Long userId;
    private String username;
    private String userImage;

    static public PostListInfo createPostListInfo(Post post, Boolean isLiked) {
        return PostListInfo.builder()
                .id(post.getId())
                .pictureList(post.getPictureList())
                .content(post.getContent())
                .likeCnt(post.getLikeCNT())
                .isLiked(isLiked)
                .createDate(post.getCreateDate())
                .address(post.getAddress())
                .weather(post.getWeather())
                .temperature(post.getTemperature())
                .userId(post.getUser().getId())
                .username(post.getUser().getUsername())
                .userImage(post.getUser().getPicture())
                .build();
    }
}
