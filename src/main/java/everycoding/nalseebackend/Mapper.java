package everycoding.nalseebackend;

import everycoding.nalseebackend.comment.controller.dto.CommentResponseDto;
import everycoding.nalseebackend.comment.service.info.CommentInfo;
import everycoding.nalseebackend.map.controller.dto.MapResponseDto;
import everycoding.nalseebackend.map.service.info.PostsInMapInfo;
import everycoding.nalseebackend.post.controller.dto.PostForDetailResponseDto;
import everycoding.nalseebackend.post.controller.dto.PostForUserFeedResponseDto;
import everycoding.nalseebackend.post.controller.dto.PostResponseDto;
import everycoding.nalseebackend.post.controller.dto.PostScoreDto;
import everycoding.nalseebackend.post.service.info.PostForDetailInfo;
import everycoding.nalseebackend.post.service.info.PostForUserFeedInfo;
import everycoding.nalseebackend.post.service.info.PostInfo;
import everycoding.nalseebackend.post.service.info.PostScoreInfo;
import everycoding.nalseebackend.user.controller.dto.FollowUserDto;
import everycoding.nalseebackend.user.controller.dto.UserDetailDto;
import everycoding.nalseebackend.user.controller.dto.UserFeedResponseDto;
import everycoding.nalseebackend.user.domain.UserDetail;
import everycoding.nalseebackend.user.service.info.FollowUserInfo;
import everycoding.nalseebackend.user.service.info.UserDetailInfo;
import everycoding.nalseebackend.user.service.info.UserFeedInfo;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@org.mapstruct.Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface Mapper {

    // 포스트
    MapResponseDto toDto(PostsInMapInfo info);

    PostResponseDto toDto(PostInfo info);

    @Mapping(target = "postResponseDto", source = "postInfo")
    PostScoreDto toDto(PostScoreInfo info);

    @Mapping(target = "postResponseDto", source = "postInfo")
    @Mapping(target = "userDetailDto", source = "userDetailInfo")
    PostForDetailResponseDto toDto(PostForDetailInfo info);

    PostForUserFeedResponseDto toDto(PostForUserFeedInfo infos);

    // 댓글
    CommentResponseDto toDto(CommentInfo info);

    // 유저
    UserDetailDto toDto(UserDetailInfo info);

    UserFeedResponseDto toDto(UserFeedInfo info);

    FollowUserDto toDto(FollowUserInfo info);

    UserDetailInfo toInfo(UserDetail userDetail);

    UserDetailInfo toInfo(UserDetailDto dto);

    UserDetail toUserDetail(UserDetailInfo info);
}
