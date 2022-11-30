package ge.sibraine.cryptoinvestment.model;


import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvNumber;
import lombok.Data;


@Data
public class PriceInput {

    @CsvBindByName(column = "timestamp")
    @CsvNumber("###.##")
    private long timeMillis;

    @CsvBindByName
    private String symbol;

    @CsvBindByName
    private String price;

    @Override
    public String toString() {
        return "Price{" +
                "timeMillis=" + timeMillis +
                ", symbol='" + symbol + '\'' +
                ", price=" + price +
                '}';
    }
}
