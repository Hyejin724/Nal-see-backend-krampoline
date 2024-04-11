package everycoding.nalseebackend;

import everycoding.nalseebackend.map.controller.dto.MapResponseDto;
import everycoding.nalseebackend.map.service.info.PostListInfo;
import everycoding.nalseebackend.map.service.info.PostsInMapInfo;
import everycoding.nalseebackend.post.domain.Post;
import everycoding.nalseebackend.post.dto.PostResponseDto;
import org.mapstruct.ReportingPolicy;

@org.mapstruct.Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface Mapper {

    MapResponseDto toDto(PostsInMapInfo info);

    PostResponseDto toDto(PostListInfo info);

}
