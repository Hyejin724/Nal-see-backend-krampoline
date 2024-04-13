package everycoding.nalseebackend.map.service;

import everycoding.nalseebackend.map.service.info.PostListInfo;
import everycoding.nalseebackend.map.service.info.PostsInMapInfo;

import java.util.List;

public interface MapService {

    List<PostsInMapInfo> getPostsInMap(double bottomLeftLat, double bottomLeftLong,
                                       double topRightLat, double topRightLong);

    List<PostListInfo> getPostListInMap(Long userId, double bottomLeftLat, double bottomLeftLong,
                                        double topRightLat, double topRightLong);
}
