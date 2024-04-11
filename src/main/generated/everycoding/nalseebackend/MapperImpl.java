package everycoding.nalseebackend;

import everycoding.nalseebackend.map.controller.dto.MapResponseDto;
import everycoding.nalseebackend.map.service.info.PostListInfo;
import everycoding.nalseebackend.map.service.info.PostsInMapInfo;
import everycoding.nalseebackend.post.dto.PostResponseDto;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2024-04-03T14:53:12+0900",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.2 (Oracle Corporation)"
)
@Component
public class MapperImpl implements Mapper {

    @Override
    public MapResponseDto toDto(PostsInMapInfo info) {
        if ( info == null ) {
            return null;
        }

        Double bottomLeftLat = null;
        Double bottomLeftLong = null;
        Double topRightLat = null;
        Double topRightLong = null;
        String picture = null;
        Integer count = null;

        bottomLeftLat = info.getBottomLeftLat();
        bottomLeftLong = info.getBottomLeftLong();
        topRightLat = info.getTopRightLat();
        topRightLong = info.getTopRightLong();
        picture = info.getPicture();
        count = info.getCount();

        MapResponseDto mapResponseDto = new MapResponseDto( bottomLeftLat, bottomLeftLong, topRightLat, topRightLong, picture, count );

        return mapResponseDto;
    }

    @Override
    public PostResponseDto toDto(PostListInfo info) {
        if ( info == null ) {
            return null;
        }

        PostResponseDto.PostResponseDtoBuilder postResponseDto = PostResponseDto.builder();

        postResponseDto.id( info.getId() );
        List<String> list = info.getPictureList();
        if ( list != null ) {
            postResponseDto.pictureList( new ArrayList<String>( list ) );
        }
        postResponseDto.content( info.getContent() );
        postResponseDto.likeCnt( info.getLikeCnt() );
        postResponseDto.isLiked( info.getIsLiked() );
        postResponseDto.createDate( info.getCreateDate() );
        postResponseDto.address( info.getAddress() );
        postResponseDto.weather( info.getWeather() );
        postResponseDto.temperature( info.getTemperature() );
        postResponseDto.userId( info.getUserId() );
        postResponseDto.username( info.getUsername() );
        postResponseDto.userImage( info.getUserImage() );

        return postResponseDto.build();
    }
}
