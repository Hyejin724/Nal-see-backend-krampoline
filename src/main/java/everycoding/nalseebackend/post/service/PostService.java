package everycoding.nalseebackend.post.service;

import everycoding.nalseebackend.post.service.info.PostForDetailInfo;
import everycoding.nalseebackend.post.service.info.PostForUserFeedInfo;
import everycoding.nalseebackend.post.service.info.PostInfo;
import everycoding.nalseebackend.post.service.info.PostScoreInfo;
import everycoding.nalseebackend.user.service.info.UserDetailInfo;
import jakarta.servlet.http.HttpServletRequest;

import java.io.IOException;
import java.util.List;

public interface PostService {

    List<PostScoreInfo> getPosts(Long userId, Long lastPostId, Double nowLatitude, Double nowLongitude);

    PostForDetailInfo getPost(Long userId, Long postId);

    List<PostInfo> searchPosts(
            Long userId, List<String> weathers, Double minTemperature, Double maxTemperature, Double minHeight, Double maxHeight,
            Double minWeight, Double maxWeight, String constitution, List<String> styles, String gender
    );

    List<PostForUserFeedInfo> getPostsForUserFeed(Long userId, Long lastPostId);

    void post(
            Long userId, String content, String address, Double latitude, Double longitude,
            UserDetailInfo userDetailInfo, HttpServletRequest request
    ) throws IOException;

    void updatePost(Long userId, Long postId, String content, UserDetailInfo userDetailInfo);

    void deletePost(Long userId, Long postId);

    void likePost(Long userId, Long postId);

    void cancelLikePost(Long userId, Long postId);
}
