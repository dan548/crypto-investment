package ge.sibraine.cryptoinvestment.model;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class PriceStamp {

    private long timeMillis;

    private String symbol;

    private BigDecimal price;

}
