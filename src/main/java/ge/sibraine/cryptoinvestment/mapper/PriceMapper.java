package ge.sibraine.cryptoinvestment.mapper;

import ge.sibraine.cryptoinvestment.model.PriceInput;
import ge.sibraine.cryptoinvestment.model.PriceStamp;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.math.BigDecimal;

@Mapper(componentModel = "spring")
public interface PriceMapper {

    PriceMapper INSTANCE = Mappers.getMapper(PriceMapper.class);

    @Mapping(source = "input.timeMillis", target = "timeMillis")
    @Mapping(source = "input.symbol", target = "symbol")
    @Mapping(source = "input.price", target = "price", qualifiedByName = "convertPrice")
    PriceStamp inputToStamp(PriceInput input);

    @Named("convertPrice")
    default BigDecimal convertPrice(String price) {
        return new BigDecimal(price);
    }

}
