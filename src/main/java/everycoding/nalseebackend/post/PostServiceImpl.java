package everycoding.nalseebackend.post;

import everycoding.nalseebackend.api.exception.BaseException;
import everycoding.nalseebackend.aws.S3Service;
import everycoding.nalseebackend.comment.CommentService;
import everycoding.nalseebackend.post.domain.Post;
import everycoding.nalseebackend.post.dto.*;
import everycoding.nalseebackend.user.UserRepository;
import everycoding.nalseebackend.user.domain.*;
import everycoding.nalseebackend.weather.caller.WeatherApiCaller;
import everycoding.nalseebackend.weather.caller.info.CurrentWeatherInfo;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class PostServiceImpl implements PostService{

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final S3Service s3Service;
    private final CommentService commentService;
    private final PostSpecification postSpecification;
    private final WeatherApiCaller weatherApiCaller;

    @Override
    @Transactional(readOnly = true)
    public List<PostScoreDto> getPosts(Long userId, Long lastPostId, Double nowLatitude, Double nowLongitude ) {
        Pageable pageable = PageRequest.of(0, 10, Sort.by("id").descending());
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        UserInfo userInfo = user.getUserInfo();
        LocalDateTime localDateTime = LocalDateTime.now();
        List<PostScoreDto> postScoreDto = postRepository.findByIdLessThan(lastPostId != -1 ? lastPostId : Long.MAX_VALUE,pageable)
                .stream()
                .map(post -> {
                    double score = totalScore(post, user, userInfo, localDateTime, nowLatitude, nowLongitude);
                    PostResponseDto postResponseDto = PostResponseDto.createPostResponseDto(post, isLiked(userId, post.getId()));
                    return new PostScoreDto(postResponseDto, score);
                }).collect(Collectors.toList());


        //점수에 따라 내림차순
        postScoreDto.sort((p1, p2) -> Double.compare(p2.getScore(), p1.getScore()));

        //정렬된 리스트에서 PostResponseDto 추출하여 반환
        return postScoreDto;

    }
    //가산점 점수
    private double totalScore(Post post, User user, UserInfo userInfo, LocalDateTime localDateTime, Double nowLatitude, Double nowLongitude) {
        double genderScore = genderScore(post, user);
        double timezoneScore = timezoneScore(post, localDateTime);
        double heightScore = heightScore(post, userInfo);
        double weightScore = weightScore(post, userInfo);
        double constitutionScore = constitutionScore(post, userInfo);
        double styleScore = styleScore(post, userInfo);
        double likeScore = likeScore(post);
        double followingScore = followingScore(post, user);
        double distanceScore = distanceScore(post.getLatitude(), post.getLongitude(), nowLatitude, nowLongitude);
        return distanceScore + genderScore + timezoneScore + heightScore + weightScore + constitutionScore + styleScore + likeScore  + followingScore;
    }

    private double distanceScore(Double postLat, Double postLon, Double userLat, Double userLon) {
        double distance = calculateDistance(postLat, postLon, userLat, userLon);

        // 여기에서 거리에 따른 점수를 계산합니다. 예를 들어:
        if (distance <= 3) { // 1km 이내
            return 5;
        } else  { // 10km 초과
            return 0;
        }
    }

    private double calculateDistance(Double lat1, Double lon1, Double lat2, Double lon2) {
        // 헤버사인 공식을 사용하여 거리를 계산
        int R = 6371; // 지구 반지름 (km)
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = R * c;

        return distance;
    }

    //성별 점수
    double genderScore(Post post, User user) {
        return post.getUserInfo().getGender() == user.getUserInfo().getGender() ? 1 : 0;
    }

    //동시간대 점수
    double timezoneScore(Post post, LocalDateTime localDateTime) {
        int timezoneDifference = Math.abs(post.getCreateDate().getHour() - localDateTime.getHour());
        return timezoneDifference <= 3 ? 5 : 0 ;
    }


    //키 점수
    double heightScore(Post post, UserInfo userInfo) {
        Double postHeight = post.getUserInfo().getHeight();
        Double userHeight = userInfo.getHeight();
        if(postHeight == null || userHeight == null){ return 0; }
        double heightDifference = Math.abs(postHeight - userHeight);
        return heightDifference <= 5 ? 1 : 0;
    }

    // 몸무게 점수
    double weightScore (Post post, UserInfo userInfo) {
        Double postWeight = post.getUserInfo().getWeight();
        Double userWeight = userInfo.getWeight();
        if(postWeight == null || userWeight == null){return 0;}
        double weightDifference = Math.abs(postWeight - userWeight);
        return weightDifference <= 5 ? 1: 0;
    }

    // 체질 점수
    double constitutionScore(Post post, UserInfo userInfo) {
        return post.getUserInfo().getConstitution() == userInfo.getConstitution() ? 3 : 0;
    }

    // 스타일 점수
    double styleScore(Post post, UserInfo userInfo) {
        return userInfo.getStyle().contains(post.getUserInfo().getStyle()) ? 2 : 0 ;
    }

    // 좋아요 점수
    double likeScore(Post post) {
        return post.getLikeCNT()*0.01 ;
    }

    double followingScore(Post post, User user) {
        // 현재 사용자가 게시물 작성자를 팔로우하고 있는지 확인

        boolean isFollowing = user.getFollowings().contains(post.getUser());
        return isFollowing ? 2 : 0; // 팔로우하고 있다면 가산점 2점 부여
    }

    @Override
    @Transactional(readOnly = true)
    public PostForDetailResponseDto getPost(Long userId, Long postId) {
        Post post = postRepository.findById(postId).orElseThrow(() -> new BaseException("wrong postId"));
        return new PostForDetailResponseDto(
                PostResponseDto.createPostResponseDto(post, isLiked(userId, post.getId())),
                post.getUserInfo(),
                commentService.getComments(userId, postId)
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<PostResponseDto> searchPosts(Long userId, List<String> weathers, Double minTemperature, Double maxTemperature, Double minHeight, Double maxHeight,
                                             Double minWeight, Double maxWeight, String constitution, List<String> styles, String gender) {
        Specification<Post> spec = Specification.where(null);

        if (weathers != null) {
            spec = spec.and(postSpecification.hasWeatherIn(weathers));
        }
        spec = spec.and(postSpecification.isTemperatureBetween(minTemperature, maxTemperature));
        spec = spec.and(postSpecification.isHeightBetween(minHeight, maxHeight));
        spec = spec.and(postSpecification.isWeightBetween(minWeight, maxWeight));
        if (constitution != null) {
            spec = spec.and(postSpecification.hasConstitution(constitution));
        }
        if (styles != null) {
            spec = spec.and(postSpecification.hasStyleIn(styles));
        }
        if (gender != null) {
            spec = spec.and(postSpecification.hasGender(gender));
        }

        return postRepository.findAll(spec)
                .stream()
                .map(post -> PostResponseDto.createPostResponseDto(post, isLiked(userId, post.getId())))
                .collect(Collectors.toList());
    }

    @Override
    public List<PostForUserFeedResponseDto> getPostsForUserFeed(Long userId, Long lastPostId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new BaseException("wrong userId"));

        Pageable pageable = PageRequest.of(0, 12, Sort.by("id").descending());

        return postRepository.findByUserAndIdLessThan(user, lastPostId!=-1 ? lastPostId : Long.MAX_VALUE, pageable)
                .stream()
                .map(post -> PostForUserFeedResponseDto.builder()
                        .postId(post.getId())
                        .postPicture(post.getPictureList().get(0))
                        .isMany(post.getPictureList().size()>1)
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    public void post(PostRequestDto postRequestDto, HttpServletRequest request) throws IOException {
        MultipartHttpServletRequest multipartHttpServletRequest = (MultipartHttpServletRequest) request;
        List<MultipartFile> files = multipartHttpServletRequest.getFiles("photos");

        User user = userRepository.findById(postRequestDto.getUserId()).orElseThrow(() -> new BaseException("wrong userId"));

        List<String> photos = s3Service.uploadS3(files);

        CurrentWeatherInfo currentWeatherInfo = weatherApiCaller.getCurrentWeather(postRequestDto.getLatitude(), postRequestDto.getLongitude());

        UserInfo userInfo = UserInfo.builder()
                .height(postRequestDto.getUserInfo().getHeight())
                .weight(postRequestDto.getUserInfo().getWeight())
                .constitution(postRequestDto.getUserInfo().getConstitution())
                .style(postRequestDto.getUserInfo().getStyle())
                .gender(postRequestDto.getUserInfo().getGender())
                .build();

        postRepository.save(
                Post.builder()
                        .pictureList(photos)
                        .content(postRequestDto.getContent())
                        .user(user)
                        .weather(currentWeatherInfo.getWeather().toString())
                        .temperature(currentWeatherInfo.getTemperature())
                        .address(postRequestDto.getAddress())
                        .latitude(postRequestDto.getLatitude())
                        .longitude(postRequestDto.getLongitude())
                        .userInfo(userInfo)
                        .build()
        );
    }

    @Override
    public void updatePost(Long userId, Long postId, PostUpdateRequestDto updateRequestDto) {
        Post post = postRepository.findById(postId).orElseThrow(() -> new BaseException("wrong postId"));

        if (!post.getUser().getId().equals(userId)) {
            throw new BaseException("수정할 수 있는 권한이 없습니다.");
        }

        if (updateRequestDto.getContent() != null) {
            post.setContent(updateRequestDto.getContent());
        }
        if (updateRequestDto.getUserInfo() != null) {
            post.setUserInfo(updateRequestDto.getUserInfo());
        }

        postRepository.save(post);
    }

    @Override
    public void deletePost(Long userId, Long postId) {
        Post post = postRepository.findById(postId).orElseThrow(() -> new BaseException("wrong postId"));
        if (!post.getUser().getId().equals(userId)) {
            throw new BaseException("삭제할 수 있는 권한이 없습니다.");
        }
        post.getPictureList().forEach(s3Service::deleteS3);
        postRepository.delete(post);
    }

    @Override
    public void likePost(Long userId, Long postId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new BaseException("wrong userId"));
        Post post = postRepository.findById(postId).orElseThrow(() -> new BaseException("wrong postId"));
        System.out.println("userId:" + user.getId());
        System.out.println("postId:" + post.getId());

        user.addPostLike(postId);
        post.increaseLikeCNT();

        userRepository.save(user);
        postRepository.save(post);
    }

    @Override
    public void cancelLikePost(Long userId, Long postId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new BaseException("wrong userId"));
        Post post = postRepository.findById(postId).orElseThrow(() -> new BaseException("wrong postId"));

        user.cancelPostLike(postId);
        post.decreaseLikeCNT();

        userRepository.save(user);
        postRepository.save(post);
    }

    private boolean isLiked(long userId, long postId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new BaseException("wrong userId"));
        return user.getPostLikeList().contains(postId);
    }
}
