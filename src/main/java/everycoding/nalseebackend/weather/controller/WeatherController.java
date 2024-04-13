package everycoding.nalseebackend.weather.controller;

import everycoding.nalseebackend.api.ApiResponse;
import everycoding.nalseebackend.weather.caller.WeatherApiCaller;
import everycoding.nalseebackend.weather.caller.info.AirPollutionInfo;
import everycoding.nalseebackend.weather.caller.info.CurrentWeatherInfo;
import everycoding.nalseebackend.weather.controller.dto.CurrentWeatherRequestDto;
import everycoding.nalseebackend.weather.controller.dto.CurrentWeatherResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class WeatherController {

    private final WeatherApiCaller weatherApiCaller;

    @GetMapping("/api/weather/current")
    public ApiResponse<CurrentWeatherResponseDto> getCurrentWeather(
            @RequestParam Double latitude,
            @RequestParam Double longitude
    ) {
        CurrentWeatherInfo currentWeatherInfo = weatherApiCaller.getCurrentWeather(latitude, longitude);
        AirPollutionInfo airPollutionInfo = weatherApiCaller.getApiPollution(latitude, longitude);

        return ApiResponse.ok(
                CurrentWeatherResponseDto.builder()
                        .weather(currentWeatherInfo.getWeather().toString())
                        .temperature(currentWeatherInfo.getTemperature())
                        .feelsLike(currentWeatherInfo.getFeelsLike())
                        .humidity(currentWeatherInfo.getHumidity())
                        .pm10(airPollutionInfo.getPm10())
                        .pm25(airPollutionInfo.getPm2_5())
                        .build()
        );
    }

}
