package everycoding.nalseebackend.weather.caller.dto;

import lombok.Getter;

import java.util.List;

@Getter
public class WeatherApiAirPollutionDto {

    private Coord coord;
    private List<Air> list;

    static class Coord {
        private double lon;
        private double lat;
    }

    @Getter
    static public class Air {
        private Long dt;
        private Main main;
        private Components components;

        static class Main {
            private Integer aqi;
        }

        @Getter
        static public class Components{
            private Double co;
            private Double no;
            private Double no2;
            private Double o3;
            private Double so2;
            private Double pm2_5;
            private Double pm10;
            private Double nh3;
        }
    }
}
