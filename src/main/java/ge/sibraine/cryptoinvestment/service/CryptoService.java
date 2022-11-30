package ge.sibraine.cryptoinvestment.service;

import com.opencsv.bean.CsvToBeanBuilder;
import ge.sibraine.cryptoinvestment.exception.NoSuchCryptoNameException;
import ge.sibraine.cryptoinvestment.mapper.PriceMapper;
import ge.sibraine.cryptoinvestment.model.Crypto;
import ge.sibraine.cryptoinvestment.model.PriceInput;
import ge.sibraine.cryptoinvestment.model.PriceStamp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class CryptoService {

    public final static Logger logger = LoggerFactory.getLogger(CryptoService.class);

    @Autowired
    private PriceMapper priceMapper;

    @Value("classpath:prices/*")
    private Resource[] resources;

    public static final Pattern pricePattern = Pattern.compile("(.+)_values.csv");

    private Crypto calculateBaseIndicators(List<PriceStamp> prices, String name) {
        Crypto crypto = null;

        if (prices != null && !prices.isEmpty()) {
            crypto = new Crypto();

            crypto.setName(name.toUpperCase());

            crypto.setMin(
                    prices.stream()
                            .min(Comparator.comparing(PriceStamp::getPrice))
                            .map(PriceStamp::getPrice)
                            .orElse(null)
            );

            crypto.setMax(
                    prices.stream()
                            .max(Comparator.comparing(PriceStamp::getPrice))
                            .map(PriceStamp::getPrice)
                            .orElse(null)
            );

            crypto.setOldest(
                    prices.stream()
                            .min(Comparator.comparingLong(PriceStamp::getTimeMillis))
                            .map(PriceStamp::getPrice)
                            .orElse(null)
            );

            crypto.setNewest(
                    prices.stream()
                            .max(Comparator.comparingLong(PriceStamp::getTimeMillis))
                            .map(PriceStamp::getPrice)
                            .orElse(null)
            );

            crypto.setNormalizedRange(
                    crypto.getMax().subtract(crypto.getMin()).divide(crypto.getMin(), 10, RoundingMode.HALF_DOWN)
                            .doubleValue()
            );
        }

        return crypto;
    }

    public List<Crypto> cryptosDescendingByNormalizedRange() {
        var priceMap = getAllPrices();
        return priceMap.entrySet().stream()
                .map(entry -> calculateBaseIndicators(entry.getValue(), entry.getKey()))
                .sorted((a, b) -> b.getNormalizedRange().compareTo(a.getNormalizedRange()))
                .collect(Collectors.toList());
    }

    public Crypto cryptoBaseIndicators(String name) throws NoSuchCryptoNameException {
        List<PriceStamp> priceList = getPrices(name);
        return calculateBaseIndicators(priceList, name);
    }

    public Crypto highestRange(String dateString) {
        LocalDate date = LocalDate.parse(dateString);
        LocalDate nextDay = date.plusDays(1);
        long startMs = date.atStartOfDay().toInstant(ZoneOffset.UTC).toEpochMilli();
        long endMs = nextDay.atStartOfDay().toInstant(ZoneOffset.UTC).toEpochMilli();
        var priceMap = getAllPrices();
        return priceMap.entrySet().stream()
                .map(entry -> calculateBaseIndicators(
                        entry.getValue().stream().filter(price -> price.getTimeMillis() >= startMs && price.getTimeMillis() < endMs).collect(Collectors.toList()), entry.getKey())
                )
                .filter(Objects::nonNull)
                .max(Comparator.comparingDouble(Crypto::getNormalizedRange))
                .orElse(null);
    }

    public List<PriceStamp> getPrices(String prefixSymbol) throws NoSuchCryptoNameException {
        InputStream resourceStream = null;
        try {
            resourceStream = new ClassPathResource(
                    String.format("prices/%s_values.csv", prefixSymbol.toUpperCase())).getInputStream();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(resourceStream))) {
                return new CsvToBeanBuilder<PriceInput>(reader)
                        .withType(PriceInput.class)
                        .build()
                        .parse()
                        .stream()
                        .map(priceMapper::inputToStamp)
                        .collect(Collectors.toList());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } catch (IOException e) {
            throw new NoSuchCryptoNameException("We don't have prices for this symbol yet!", e);
        }
    }

    public Map<String, List<PriceStamp>> getAllPrices() {
        return Arrays.stream(resources)
                .filter(resource -> resource.getFilename() != null && resource.getFilename().matches(".+_values.csv"))
                .collect(Collectors.toMap(
                        res -> {
                            Matcher m = pricePattern.matcher(res.getFilename());
                            return m.find() ? m.group(1) : res.getFilename();
                        }, res -> getPrices(res).stream()
                                .map(inputPrice -> priceMapper.inputToStamp(inputPrice))
                                .collect(Collectors.toList())
                ));
    }

    private List<PriceInput> getPrices(Resource res) {
        InputStream resourceStream = null;
        try {
            resourceStream = res.getInputStream();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(resourceStream))) {
                return new CsvToBeanBuilder<PriceInput>(reader)
                        .withType(PriceInput.class)
                        .build()
                        .parse();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
