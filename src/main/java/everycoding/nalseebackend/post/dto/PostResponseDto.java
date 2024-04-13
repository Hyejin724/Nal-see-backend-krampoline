package everycoding.nalseebackend.post.dto;

import everycoding.nalseebackend.post.domain.Post;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@NoArgsConstructor
@Builder
@AllArgsConstructor
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

    static public PostResponseDto createPostResponseDto(Post post, Boolean liked) {
        return PostResponseDto.builder()
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
