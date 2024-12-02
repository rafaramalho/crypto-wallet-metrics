package ch.post.wallet.controller;

import java.math.BigDecimal;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import ch.post.wallet.dto.WalletInput;
import ch.post.wallet.dto.WalletOutput;
import ch.post.wallet.service.WalletService;

@Controller
@RequestMapping("/api/wallet")
public class WalletController {

    private static final Logger logger = LoggerFactory.getLogger(WalletController.class);

    private final WalletService walletService;

    public WalletController(WalletService walletService) {
        this.walletService = walletService;
    }

    @PostMapping("/metrics")
    public ResponseEntity<?> getWalletMetrics(@RequestBody List<WalletInput> walletInputs) {
        try {
            logger.info("Calculating wallet metrics for {} inputs", walletInputs.size());

            validateWalletInputs(walletInputs);

            WalletOutput calculateWalletMetrics = walletService.calculateWalletMetrics(walletInputs);

            logger.info("Calculated wallet metrics: {}", calculateWalletMetrics);
            return ResponseEntity.ok(calculateWalletMetrics);

        } catch (IllegalArgumentException e) {
            logger.error("Validation error: {}", e.getMessage());
            return ResponseEntity.badRequest().body(e);

        } catch (Exception e) {
            logger.error("An unexpected error occurred while calculating wallet metrics", e);
            return ResponseEntity.internalServerError().body(e);
        }
    }

    private void validateWalletInputs(List<WalletInput> walletInputs) {
        if (walletInputs == null || walletInputs.isEmpty()) {
            throw new IllegalArgumentException("Wallet inputs cannot be null or empty");
        }

        for (WalletInput input : walletInputs) {
            if (input.getSymbol() == null || input.getSymbol().isEmpty()) {
                throw new IllegalArgumentException("Asset symbol cannot be null or empty");
            }
            if (input.getQuantity() == null || input.getQuantity().compareTo(BigDecimal.ZERO) <= 0) {
                throw new IllegalArgumentException("Asset quantity must be greater than zero");
            }
            if (input.getPrice() == null || input.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
                throw new IllegalArgumentException("Asset price must be greater than zero");
            }
        }
    }
}
