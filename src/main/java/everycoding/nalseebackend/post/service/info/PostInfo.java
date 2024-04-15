package everycoding.nalseebackend.post.service.info;

import everycoding.nalseebackend.post.repository.Post;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class PostInfo {

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

    static public PostInfo createPostInfo(Post post, Boolean liked) {
        return PostInfo.builder()
                .id(post.getId())
                .pictureList(post.getPictureList())
                .content(post.getContent())
                .likeCnt(post.getLikeCNT())
                .liked(liked)
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
