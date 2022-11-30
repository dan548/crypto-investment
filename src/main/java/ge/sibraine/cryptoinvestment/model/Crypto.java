package ge.sibraine.cryptoinvestment.model;

import lombok.Data;

@Data
public class Crypto {

    private String name;
    private Double oldest;
    private Double newest;
    private Double min;
    private Double max;
    private Double normalizedRange;

}
