package everycoding.nalseebackend.weather.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class CurrentWeatherResponseDto {
    private String weather;
    private Double temperature;
    private Double feelsLike;
    private Integer humidity;
    private Double pm10;
    private Double pm25;
}
