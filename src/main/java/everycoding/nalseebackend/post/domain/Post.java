package everycoding.nalseebackend.post.domain;

import everycoding.nalseebackend.BaseEntity;
import everycoding.nalseebackend.comment.domain.Comment;
import everycoding.nalseebackend.user.domain.*;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
@Table(name = "posts")
@NoArgsConstructor
public class Post extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    @ElementCollection
    private List<String> pictureList;

    @Setter
    @Column(columnDefinition = "LONGTEXT")
    private String content;

    private int likeCNT;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @OneToMany(mappedBy = "post", cascade = {CascadeType.ALL})
    private List<Comment> comments;

    @Setter
    private String address;
    @Setter
    private double longitude;
    @Setter
    private double latitude;

    private String weather;
    private double temperature;

    @Setter
    @Embedded
    private UserInfo userInfo;
//    private String link;
//    private String brand;
//    private String productName;

    @Builder
    public Post(List<String> pictureList, String content, User user, String weather, double temperature, String address, double longitude, double latitude, UserInfo userInfo) {
        this.pictureList = pictureList;
        this.content = content;
        likeCNT = 0;
        this.user = user;
        comments = new ArrayList<>();
        this.weather = weather;
        this.temperature = temperature;
        this.address = address;
        this.longitude = longitude;
        this.latitude = latitude;
        this.userInfo = userInfo;
    }

    public void increaseLikeCNT() {
        likeCNT++;
    }

    public void decreaseLikeCNT() {
        likeCNT--;
    }
}
