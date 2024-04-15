package everycoding.nalseebackend.map.service;

import everycoding.nalseebackend.map.service.info.PostsInMapInfo;
import everycoding.nalseebackend.post.service.info.PostInfo;

import java.util.List;

public interface MapService {

    List<PostsInMapInfo> getPostsInMap(double bottomLeftLat, double bottomLeftLong,
                                       double topRightLat, double topRightLong);

    List<PostInfo> getPostListInMap(Long userId, double bottomLeftLat, double bottomLeftLong,
                                    double topRightLat, double topRightLong);
}
