package ge.sibraine.cryptoinvestment.model;


import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvNumber;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class Price {

    @CsvBindByName(column = "timestamp")
    @CsvNumber("###.##")
    private long timeMillis;

    @CsvBindByName
    private String symbol;

    @CsvBindByName
    private double price;

    @Override
    public String toString() {
        return "Price{" +
                "timeMillis=" + timeMillis +
                ", symbol='" + symbol + '\'' +
                ", price=" + price +
                '}';
    }
}
