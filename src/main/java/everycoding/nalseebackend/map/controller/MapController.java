package everycoding.nalseebackend.map.controller;

import everycoding.nalseebackend.Mapper;
import everycoding.nalseebackend.api.ApiResponse;
import everycoding.nalseebackend.auth.customUser.CustomUserDetails;
import everycoding.nalseebackend.map.controller.dto.MapResponseDto;
import everycoding.nalseebackend.map.service.MapService;
import everycoding.nalseebackend.map.service.info.PostListInfo;
import everycoding.nalseebackend.map.service.info.PostsInMapInfo;
import everycoding.nalseebackend.post.dto.PostResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class MapController {

    private final MapService mapService;
    private final Mapper mapper;

    @GetMapping("/api/map")
    public ApiResponse<List<MapResponseDto>> getPostsInMap(
            @RequestParam Double bottomLeftLat, @RequestParam Double bottomLeftLong,
            @RequestParam Double topRightLat, @RequestParam Double topRightLong
    ) {
        List<PostsInMapInfo> infos = mapService.getPostsInMap(bottomLeftLat, bottomLeftLong, topRightLat, topRightLong);

        return ApiResponse.ok(
                infos.stream()
                        .map(mapper::toDto)
                        .collect(Collectors.toList())
        );
    }

    @GetMapping("/api/map/postList")
    public ApiResponse<List<PostResponseDto>> getPostListInMap(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @RequestParam Double bottomLeftLat, @RequestParam Double bottomLeftLong,
            @RequestParam Double topRightLat, @RequestParam Double topRightLong
    ) {
        List<PostListInfo> infos = mapService.getPostListInMap(
                customUserDetails.getId(), bottomLeftLat, bottomLeftLong, topRightLat, topRightLong
        );

        return ApiResponse.ok(
                infos.stream()
                        .map(mapper::toDto)
                        .collect(Collectors.toList())
        );
    }
}
