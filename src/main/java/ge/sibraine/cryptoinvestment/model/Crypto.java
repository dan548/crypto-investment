package ge.sibraine.cryptoinvestment.model;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class Crypto {

    private String name;
    private BigDecimal oldest;
    private BigDecimal newest;
    private BigDecimal min;
    private BigDecimal max;
    private Double normalizedRange;

}
