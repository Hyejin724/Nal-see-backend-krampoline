package everycoding.nalseebackend.post.service;

import everycoding.nalseebackend.Mapper;
import everycoding.nalseebackend.api.exception.BaseException;
import everycoding.nalseebackend.aws.S3Service;
import everycoding.nalseebackend.comment.service.CommentService;
import everycoding.nalseebackend.post.repository.Post;
import everycoding.nalseebackend.post.repository.PostRepository;
import everycoding.nalseebackend.post.service.info.PostForDetailInfo;
import everycoding.nalseebackend.post.service.info.PostForUserFeedInfo;
import everycoding.nalseebackend.post.service.info.PostInfo;
import everycoding.nalseebackend.post.service.info.PostScoreInfo;
import everycoding.nalseebackend.user.UserRepository;
import everycoding.nalseebackend.user.domain.*;
import everycoding.nalseebackend.user.service.info.UserDetailInfo;
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
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final S3Service s3Service;
    private final CommentService commentService;
    private final PostSpecification postSpecification;
    private final WeatherApiCaller weatherApiCaller;
    private final Mapper mapper;

    @Override
    @Transactional(readOnly = true)
    public List<PostScoreInfo> getPosts(Long userId, Long lastPostId, Double nowLatitude, Double nowLongitude ) {
        Pageable pageable = PageRequest.of(0, 10, Sort.by("id").descending());
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        UserDetail userDetail = user.getUserDetail();
        LocalDateTime localDateTime = LocalDateTime.now();
        List<PostScoreInfo> infos = new java.util.ArrayList<>(postRepository.findByIdLessThan(lastPostId != -1 ? lastPostId : Long.MAX_VALUE, pageable)
                .stream()
                .map(post -> {
                    double score = totalScore(post, user, userDetail, localDateTime, nowLatitude, nowLongitude);
                    PostInfo postInfo = PostInfo.createPostInfo(post, isLiked(userId, post.getId()));
                    return new PostScoreInfo(postInfo, score);
                }).toList());


        //점수에 따라 내림차순
        infos.sort((p1, p2) -> Double.compare(p2.getScore(), p1.getScore()));

        //정렬된 리스트에서 PostResponseDto 추출하여 반환
        return infos;

    }
    //가산점 점수
    private double totalScore(Post post, User user, UserDetail userDetail, LocalDateTime localDateTime, Double nowLatitude, Double nowLongitude) {
        double genderScore = genderScore(post, user);
        double timezoneScore = timezoneScore(post, localDateTime);
        double heightScore = heightScore(post, userDetail);
        double weightScore = weightScore(post, userDetail);
        double constitutionScore = constitutionScore(post, userDetail);
        double styleScore = styleScore(post, userDetail);
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
        return post.getUserDetail().getGender() == user.getUserDetail().getGender() ? 1 : 0;
    }

    //동시간대 점수
    double timezoneScore(Post post, LocalDateTime localDateTime) {
        int timezoneDifference = Math.abs(post.getCreateDate().getHour() - localDateTime.getHour());
        return timezoneDifference <= 3 ? 5 : 0 ;
    }


    //키 점수
    double heightScore(Post post, UserDetail userDetail) {
        Double postHeight = post.getUserDetail().getHeight();
        Double userHeight = userDetail.getHeight();
        if(postHeight == null || userHeight == null){ return 0; }
        double heightDifference = Math.abs(postHeight - userHeight);
        return heightDifference <= 5 ? 1 : 0;
    }

    // 몸무게 점수
    double weightScore (Post post, UserDetail userDetail) {
        Double postWeight = post.getUserDetail().getWeight();
        Double userWeight = userDetail.getWeight();
        if(postWeight == null || userWeight == null){return 0;}
        double weightDifference = Math.abs(postWeight - userWeight);
        return weightDifference <= 5 ? 1: 0;
    }

    // 체질 점수
    double constitutionScore(Post post, UserDetail userDetail) {
        return post.getUserDetail().getConstitution() == userDetail.getConstitution() ? 3 : 0;
    }

    // 스타일 점수
    double styleScore(Post post, UserDetail userDetail) {
        return userDetail.getStyle().contains(post.getUserDetail().getStyle()) ? 2 : 0 ;
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
    public PostForDetailInfo getPost(Long userId, Long postId) {
        Post post = postRepository.findById(postId).orElseThrow(() -> new BaseException("wrong postId"));
        return new PostForDetailInfo(
                PostInfo.createPostInfo(post, isLiked(userId, post.getId())),
                mapper.toInfo(post.getUserDetail()),
                commentService.getComments(userId, postId)
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<PostInfo> searchPosts(Long userId, List<String> weathers, Double minTemperature, Double maxTemperature, Double minHeight, Double maxHeight,
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
                .map(post -> PostInfo.createPostInfo(post, isLiked(userId, post.getId())))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<PostForUserFeedInfo> getPostsForUserFeed(Long userId, Long lastPostId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new BaseException("wrong userId"));

        Pageable pageable = PageRequest.of(0, 12, Sort.by("id").descending());

        return postRepository.findByUserAndIdLessThan(user, lastPostId!=-1 ? lastPostId : Long.MAX_VALUE, pageable)
                .stream()
                .map(post -> PostForUserFeedInfo.builder()
                        .postId(post.getId())
                        .postPicture(post.getPictureList().get(0))
                        .isMany(post.getPictureList().size()>1)
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    public void post(
            Long userId, String content, String address, Double latitude, Double longitude,
            UserDetailInfo userDetailInfo, HttpServletRequest request
    ) throws IOException {
        MultipartHttpServletRequest multipartHttpServletRequest = (MultipartHttpServletRequest) request;
        List<MultipartFile> files = multipartHttpServletRequest.getFiles("photos");

        User user = userRepository.findById(userId).orElseThrow(() -> new BaseException("wrong userId"));

        List<String> photos = s3Service.uploadS3(files);

        CurrentWeatherInfo currentWeatherInfo = weatherApiCaller.getCurrentWeather(latitude, longitude);

        UserDetail userDetail = UserDetail.builder()
                .height(userDetailInfo.getHeight())
                .weight(userDetailInfo.getWeight())
                .constitution(Constitution.valueOf(userDetailInfo.getConstitution()))
                .style(userDetailInfo.getStyle().stream().map(FashionStyle::valueOf).collect(Collectors.toList()))
                .gender(Gender.valueOf(userDetailInfo.getGender()))
                .build();

        postRepository.save(
                Post.builder()
                        .pictureList(photos)
                        .content(content)
                        .user(user)
                        .weather(currentWeatherInfo.getWeather().toString())
                        .temperature(currentWeatherInfo.getTemperature())
                        .address(address)
                        .latitude(latitude)
                        .longitude(longitude)
                        .userDetail(userDetail)
                        .build()
        );
    }

    @Override
    public void updatePost(Long userId, Long postId, String content, UserDetailInfo userDetailInfo) {
        Post post = postRepository.findById(postId).orElseThrow(() -> new BaseException("wrong postId"));

        if (!post.getUser().getId().equals(userId)) {
            throw new BaseException("수정할 수 있는 권한이 없습니다.");
        }

        if (content != null) {
            post.setContent(content);
        }
        if (userDetailInfo != null) {
            post.setUserDetail(mapper.toUserDetail(userDetailInfo));
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
