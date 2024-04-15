package everycoding.nalseebackend.user.domain;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Embeddable
@NoArgsConstructor
public class UserDetail {

    private Double height;
    private Double weight;
    @Enumerated(EnumType.STRING)
    private Constitution constitution;
    @Enumerated(EnumType.STRING)
    @ElementCollection
    private List<FashionStyle> style;
    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Builder
    public UserDetail(Double height, Double weight, Constitution constitution, List<FashionStyle> style, Gender gender) {
        this.height = height;
        this.weight = weight;
        this.constitution = constitution;
        this.style = style;
        this.gender = gender;
    }
}
