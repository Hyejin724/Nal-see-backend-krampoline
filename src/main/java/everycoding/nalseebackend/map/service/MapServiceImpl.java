package everycoding.nalseebackend.map.service;

import everycoding.nalseebackend.api.exception.BaseException;
import everycoding.nalseebackend.map.service.info.PostListInfo;
import everycoding.nalseebackend.map.service.info.PostsInMapInfo;
import everycoding.nalseebackend.post.PostRepository;
import everycoding.nalseebackend.post.domain.Post;
import everycoding.nalseebackend.user.UserRepository;
import everycoding.nalseebackend.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MapServiceImpl implements MapService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public List<PostsInMapInfo> getPostsInMap(
            double bottomLeftLat, double bottomLeftLong, double topRightLat, double topRightLong
    ) {
        List<Post> posts = postRepository.findByLocationWithin(bottomLeftLat, bottomLeftLong, topRightLat, topRightLong);
        List<PostsInMapInfo> postsInMapInfos = new ArrayList<>();

        double h = (topRightLat - bottomLeftLat) / 8;
        double w = (topRightLong - bottomLeftLong) / 4;

        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 4; j++) {
                int count = 0;
                String picture = null;

                for (int k = 0; k < posts.size(); k++) {
                    Post post = posts.get(k);
                    double lat = post.getLatitude();
                    double lon = post.getLongitude();

                    if (
                            lat >= bottomLeftLat + h * i && lat <= bottomLeftLat + h * (i + 1) &&
                                    lon >= bottomLeftLong + w * j && lon <= bottomLeftLong + w * (j + 1)
                    ) {
                        count++;
                        picture = post.getPictureList().get(0);

                        posts.remove(post);
                        k--;
                    }
                }
                if (count != 0) {
                    postsInMapInfos.add(
                            PostsInMapInfo.builder()
                                    .bottomLeftLat(bottomLeftLat + h * i)
                                    .bottomLeftLong(bottomLeftLong + w * j)
                                    .topRightLat(bottomLeftLat + h * (i + 1))
                                    .topRightLong(bottomLeftLong + w * (j + 1))
                                    .picture(picture)
                                    .count(count)
                                    .build()
                    );
                }

            }
        }

        return postsInMapInfos;
    }

    @Override
    @Transactional(readOnly = true)
    public List<PostListInfo> getPostListInMap(
            Long userId, double bottomLeftLat, double bottomLeftLong, double topRightLat, double topRightLong
    ) {
        List<Post> posts = postRepository.findByLocationWithin(bottomLeftLat, bottomLeftLong, topRightLat, topRightLong);

        return posts.stream()
                .map(post -> PostListInfo.createPostListInfo(post, isLiked(userId, post.getId())))
                .collect(Collectors.toList());
    }

    private boolean isLiked(long userId, long postId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new BaseException("wrong userId"));
        return user.getPostLikeList().contains(postId);
    }
}
