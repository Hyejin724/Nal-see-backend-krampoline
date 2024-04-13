package everycoding.nalseebackend.weather.caller.dto;

import lombok.Getter;

import java.util.List;

@Getter
public class WeatherApiCurrentWeatherDto {

    private Coord coord;
    private List<Weather> weather;
    private String base;
    private Main main;
    private long visibility;
    private Wind wind;
    private Rain rain;
    private Clouds clouds;
    private long dt;
    private Sys sys;
    private int timezone;
    private long id;
    private String name;
    private int cod;

    static class Coord {
        private double lon;
        private double lat;
    }

    @Getter
    static public class Weather {
        private int id;
        private String main;
        private String description;
        private String icon;

    }

    @Getter
    static public class Main {
        private double temp;
        private double feels_like;
        private double temp_min;
        private double temp_max;
        private long pressure;
        private int humidity;
        private int sea_level;
        private int grnd_level;
    }

    static class Wind {
        private double speed;
        private double deg;
        private double gust;
    }

    static class Rain {
        private double lh;
    }

    static class Clouds{
        private int all;
    }

    static class Sys {
        private int type;
        private long id;
        private String country;
        private long sunrise;
        private long sunset;
    }
}
