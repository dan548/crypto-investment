package ge.sibraine.cryptoinvestment.controller;

import ge.sibraine.cryptoinvestment.exception.NoSuchCryptoNameException;
import ge.sibraine.cryptoinvestment.model.Crypto;
import ge.sibraine.cryptoinvestment.service.CryptoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class CryptoController {

    private CryptoService cryptoService;

    public CryptoController(CryptoService cryptoService) {
        this.cryptoService = cryptoService;
    }

    @GetMapping("/cryptos")
    public ResponseEntity<?> cryptosSortedByRange() {
        return ResponseEntity.ok(cryptoService.cryptosDescendingByNormalizedRange());
    }

    @GetMapping("/cryptos/{symbol}")
    public ResponseEntity<?> cryptoBaseIndicators(@PathVariable String symbol) {
        try {
            Crypto crypto = cryptoService.cryptoBaseIndicators(symbol);
            return ResponseEntity.ok(crypto);
        } catch (NoSuchCryptoNameException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/cryptos/top")
    public ResponseEntity<?> highestRangeCryptoByDay(@RequestParam String date) {
        Crypto highestRangedCrypto = cryptoService.highestRange(date);
        if (highestRangedCrypto != null) {
            return ResponseEntity.ok(highestRangedCrypto);
        }
        return ResponseEntity.badRequest().body("No data for the requested day!");
    }
}
