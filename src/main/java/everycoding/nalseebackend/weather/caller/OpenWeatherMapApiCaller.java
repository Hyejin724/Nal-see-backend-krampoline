package everycoding.nalseebackend.weather.caller;

import everycoding.nalseebackend.api.exception.BaseException;
import everycoding.nalseebackend.weather.caller.dto.WeatherApiAirPollutionDto;
import everycoding.nalseebackend.weather.caller.dto.WeatherApiCurrentWeatherDto;
import everycoding.nalseebackend.weather.caller.info.AirPollutionInfo;
import everycoding.nalseebackend.weather.caller.info.CurrentWeatherInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
public class OpenWeatherMapApiCaller implements WeatherApiCaller {

    private final RestTemplate restTemplate;

    @Override
    public CurrentWeatherInfo getCurrentWeather(Double latitude, Double longitude) {
        String url = "https://api.openweathermap.org/data/2.5/weather?lat=" + latitude + "&lon=" + longitude + "&appid=7b9d8977d2c3d10d5ae6e4b4b4907c10";
        WeatherApiCurrentWeatherDto result = restTemplate.getForObject(url, WeatherApiCurrentWeatherDto.class);
        return new CurrentWeatherInfo(
                Weather.valueOf(result.getWeather().get(0).getMain()),
                Math.ceil((result.getMain().getTemp() - 273.15) * 10) / 10.0,
                Math.ceil((result.getMain().getFeels_like() - 273.15) * 10) / 10.0,
                result.getMain().getHumidity()
        );
    }

    @Override
    public AirPollutionInfo getApiPollution(Double latitude, Double longitude) {
        String url = "http://api.openweathermap.org/data/2.5/air_pollution?lat=" + latitude + "&lon=" + longitude + "&appid=7b9d8977d2c3d10d5ae6e4b4b4907c10";
        WeatherApiAirPollutionDto result = restTemplate.getForObject(url, WeatherApiAirPollutionDto.class);
        return new AirPollutionInfo(
                result.getList().get(0).getComponents().getPm10(),
                result.getList().get(0).getComponents().getPm2_5()
        );
    }

}
