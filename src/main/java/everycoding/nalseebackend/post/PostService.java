package everycoding.nalseebackend.post;

import everycoding.nalseebackend.post.dto.*;
import jakarta.servlet.http.HttpServletRequest;

import java.io.IOException;
import java.util.List;

public interface PostService {

    List<PostScoreDto> getPosts(Long userId, Long lastPostId, Double nowLatitude, Double nowLongitude);

    PostForDetailResponseDto getPost(Long userId, Long postId);

    List<PostResponseDto> searchPosts(Long userId, List<String> weathers, Double minTemperature, Double maxTemperature, Double minHeight, Double maxHeight,
                                      Double minWeight, Double maxWeight, String constitution, List<String> styles, String gender);

    List<PostForUserFeedResponseDto> getPostsForUserFeed(Long userId, Long lastPostId);

    void post(PostRequestDto postRequestDto, HttpServletRequest request) throws IOException;

    void updatePost(Long userId, Long postId, PostUpdateRequestDto postRequestDto);

    void deletePost(Long userId, Long postId);

    void likePost(Long userId, Long postId);

    void cancelLikePost(Long userId, Long postId);
}
