package everycoding.nalseebackend.weather.controller.dto;

import lombok.Getter;

@Getter
public class CurrentWeatherRequestDto {
    private Double latitude;
    private Double longitude;
}
