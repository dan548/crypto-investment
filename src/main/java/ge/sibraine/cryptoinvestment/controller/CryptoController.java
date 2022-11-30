package ge.sibraine.cryptoinvestment.controller;

import ge.sibraine.cryptoinvestment.exception.NoSuchCryptoNameException;
import ge.sibraine.cryptoinvestment.model.Crypto;
import ge.sibraine.cryptoinvestment.service.CryptoService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@Api("/cryptos")
@RequestMapping("/cryptos")
public class CryptoController {

    private CryptoService cryptoService;

    public CryptoController(CryptoService cryptoService) {
        this.cryptoService = cryptoService;
    }

    @GetMapping()
    @ApiOperation(value = "Get all cryptos", notes = "Retrieving indicators of all cryptos in descending order of normalized range", response = Crypto[].class)
    @ApiResponses({
            @ApiResponse(code = 200, message = "Success", response = Crypto[].class)
    })
    public ResponseEntity<?> cryptosSortedByRange() {
        return ResponseEntity.ok(cryptoService.cryptosDescendingByNormalizedRange());
    }

    @GetMapping("/{symbol}")
    @ApiOperation(value = "Get crypto indicators", notes = "Retrieving indicators for the requested crypto", response = Crypto.class)
    @ApiResponses({
            @ApiResponse(code = 200, message = "Success", response = Crypto.class),
            @ApiResponse(code = 400, message = "Bad request", response = String.class)
    })
    public ResponseEntity<?> cryptoBaseIndicators(@PathVariable String symbol) {
        try {
            Crypto crypto = cryptoService.cryptoBaseIndicators(symbol);
            return ResponseEntity.ok(crypto);
        } catch (NoSuchCryptoNameException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/top")
    @ApiOperation(value = "Get highest ranged crypto for date", notes = "Retrieving indicators for the crypto of the highest normalized range on the specific day", response = Crypto.class)
    @ApiResponses({
            @ApiResponse(code = 200, message = "Success", response = Crypto.class),
            @ApiResponse(code = 400, message = "Bad request", response = String.class)
    })
    public ResponseEntity<?> highestRangeCryptoByDay(@RequestParam String date) {
        Crypto highestRangedCrypto = cryptoService.highestRange(date);
        if (highestRangedCrypto != null) {
            return ResponseEntity.ok(highestRangedCrypto);
        }
        return ResponseEntity.badRequest().body("No data for the requested day!");
    }
}
