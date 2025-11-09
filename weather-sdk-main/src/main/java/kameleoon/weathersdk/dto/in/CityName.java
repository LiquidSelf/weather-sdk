package kameleoon.weathersdk.dto.in;

import lombok.NonNull;

public record CityName(@NonNull String nameLike) {
    public static CityName like(@NonNull String nameLike) {
        return new CityName(nameLike);
    }
}
