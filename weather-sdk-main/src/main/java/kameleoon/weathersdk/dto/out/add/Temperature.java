package kameleoon.weathersdk.dto.out.add;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Temperature {
    private double temp;
    private double feelsLike;
}
