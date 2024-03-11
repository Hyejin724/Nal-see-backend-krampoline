package everycoding.nalseebackend.post.domain;

import everycoding.nalseebackend.BaseEntity;
import everycoding.nalseebackend.comment.domain.Comment;
import everycoding.nalseebackend.user.domain.User;
import everycoding.nalseebackend.weather.domain.Weather;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Entity
@Table(name = "posts")
@NoArgsConstructor
public class Post extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ElementCollection
    private List<String> picture_list;

    private Long content;

    private Integer likeCNT;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @OneToOne(fetch = FetchType.LAZY)
    private Weather weather;

    @OneToMany(mappedBy = "post")
    private List<Comment> comments;

    @OneToOne(fetch = FetchType.LAZY)
    private PostINF postINF;

    @OneToOne(fetch = FetchType.LAZY)
    private ProductINF productINF;

    @Builder
    public Post(List<String> picture_list, Long content, User user, Weather weather, PostINF postINF, List<ProductINF> productINF) {
        this.picture_list = picture_list;
        this.content = content;
        this.user = user;
        this.weather = weather;
        this.postINF = postINF;
        this.productINF = productINF;
    }
}
