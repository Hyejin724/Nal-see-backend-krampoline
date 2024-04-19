package everycoding.nalseebackend.auth.dto.request;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class DeleteRequestDto {
    private String email;
    private String username;

    public DeleteRequestDto(String email, String username) {
        this.email = email;
        this.username = username;
    }
}
