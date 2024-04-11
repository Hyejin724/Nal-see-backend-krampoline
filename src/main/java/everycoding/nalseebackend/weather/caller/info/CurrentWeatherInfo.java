package everycoding.nalseebackend.weather.caller.info;

import everycoding.nalseebackend.weather.caller.Weather;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CurrentWeatherInfo {
    private Weather weather;
    private Double temperature;
    private Double feelsLike;
    private Integer humidity;
}
