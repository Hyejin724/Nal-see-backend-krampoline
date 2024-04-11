package everycoding.nalseebackend.weather.caller;

import everycoding.nalseebackend.weather.caller.info.AirPollutionInfo;
import everycoding.nalseebackend.weather.caller.info.CurrentWeatherInfo;

public interface WeatherApiCaller {

    CurrentWeatherInfo getCurrentWeather(Double latitude, Double longitude);

    AirPollutionInfo getApiPollution(Double latitude, Double longitude);
}
