package everycoding.nalseebackend.post.service.info;

import everycoding.nalseebackend.comment.service.info.CommentInfo;
import everycoding.nalseebackend.user.service.info.UserDetailInfo;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;


@Getter
@AllArgsConstructor
public class PostForDetailInfo {
    private PostInfo postInfo;
    private UserDetailInfo userDetailInfo;
    private List<CommentInfo> comments;
}
