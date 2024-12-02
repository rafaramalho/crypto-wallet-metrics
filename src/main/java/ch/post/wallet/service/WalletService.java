package ch.post.wallet.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import ch.post.wallet.dto.WalletInput;
import ch.post.wallet.dto.WalletOutput;
import ch.post.wallet.entity.CryptoAsset;
import ch.post.wallet.entity.Wallet;
import ch.post.wallet.repository.CryptoAssetRepository;
import ch.post.wallet.repository.WalletRepository;
import jakarta.annotation.PreDestroy;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Service
public class WalletService {

    private static final int THREAD_POOL_SIZE = 3;
    private static final int EXCUTOR_FORCE_SHUTDOWN = 60;

    private static final Logger logger = LoggerFactory.getLogger(WalletService.class);

    private final ExecutorService executorService = Executors.newFixedThreadPool(THREAD_POOL_SIZE);

    private final WalletRepository walletRepository;
    private final CryptoAssetRepository assetRepository;

    public WalletService(WalletRepository walletRepository, CryptoAssetRepository assetRepository) {
        this.walletRepository = walletRepository;
        this.assetRepository = assetRepository;
    }

    @Transactional
    public WalletOutput calculateWalletMetrics(List<WalletInput> walletInputs) {
        List<Future<AssetPerformance>> futures = submitTasks(walletInputs);

        WalletMetrics metrics = aggregateResults(futures);

        saveWallet(metrics);

        return new WalletOutput(
                metrics.totalValue.setScale(2, RoundingMode.HALF_UP),
                metrics.bestAsset,
                metrics.bestPerformance.setScale(2, RoundingMode.HALF_UP),
                metrics.worstAsset,
                metrics.worstPerformance.setScale(2, RoundingMode.HALF_UP));
    }

    private List<Future<AssetPerformance>> submitTasks(List<WalletInput> walletInputs) {
        List<Future<AssetPerformance>> futures = new ArrayList<>();
        for (WalletInput input : walletInputs) {
            futures.add(executorService.submit(() -> {
                logger.info("Submitted request {} at {}", input.getSymbol(), LocalDateTime.now());

                LocalDateTime inputTimestamp = Optional.ofNullable(input.getTimestamp()).orElse(LocalDateTime.now());

                Optional<CryptoAsset> optionalAsset = assetRepository.findBySymbolAndTimestamp(inputTimestamp,
                        input.getSymbol());

                if (optionalAsset.isPresent()) {
                    CryptoAsset asset = optionalAsset.get();
                    BigDecimal currentValue = asset.getPriceUsd().multiply(input.getQuantity());
                    BigDecimal performancePercentage = calculatePerformancePercentage(input, currentValue);
                    return new AssetPerformance(input.getSymbol(), currentValue, performancePercentage);
                } else {
                    logger.warn("Asset not found for symbol: {} at timestamp: {}", input.getSymbol(), inputTimestamp);
                    return new AssetPerformance(input.getSymbol(), BigDecimal.ZERO, BigDecimal.ZERO);
                }
            }));
        }
        return futures;
    }

    private WalletMetrics aggregateResults(List<Future<AssetPerformance>> futures) {
        WalletMetrics metrics = new WalletMetrics();

        for (Future<AssetPerformance> future : futures) {
            try {
                AssetPerformance performance = future.get();
                metrics.totalValue = metrics.totalValue.add(performance.getCurrentValue());

                if (performance.getPerformancePercentage().compareTo(metrics.bestPerformance) > 0
                        || metrics.bestAsset == null) {
                    metrics.bestPerformance = performance.getPerformancePercentage();
                    metrics.bestAsset = performance.getSymbol();
                }

                if (performance.getPerformancePercentage().compareTo(metrics.worstPerformance) < 0
                        || metrics.worstAsset == null) {
                    metrics.worstPerformance = performance.getPerformancePercentage();
                    metrics.worstAsset = performance.getSymbol();
                }
            } catch (InterruptedException | ExecutionException e) {
                logger.error("Error retrieving asset price", e);
            }
        }

        return metrics;
    }

    BigDecimal calculatePerformancePercentage(WalletInput input, BigDecimal currentValue) {
        BigDecimal initialInvestment = input.getPrice().multiply(input.getQuantity());
        return currentValue.subtract(initialInvestment)
                .divide(initialInvestment, 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100));
    }

    private void saveWallet(WalletMetrics metrics) {
        walletRepository.save(new Wallet(null, metrics.totalValue, metrics.bestAsset, metrics.worstAsset,
                metrics.bestPerformance, metrics.worstPerformance));
    }

    @PreDestroy
    public void shutdownExecutorService() {
        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(EXCUTOR_FORCE_SHUTDOWN, TimeUnit.SECONDS)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            executorService.shutdownNow();
        }
    }

    private static class AssetPerformance {
        private final String symbol;
        private final BigDecimal currentValue;
        private final BigDecimal performancePercentage;

        public AssetPerformance(String symbol, BigDecimal currentValue, BigDecimal performancePercentage) {
            this.symbol = symbol;
            this.currentValue = currentValue;
            this.performancePercentage = performancePercentage;
        }

        public String getSymbol() {
            return symbol;
        }

        public BigDecimal getCurrentValue() {
            return currentValue;
        }

        public BigDecimal getPerformancePercentage() {
            return performancePercentage;
        }
    }

    @AllArgsConstructor
    @NoArgsConstructor
    private class WalletMetrics {
        BigDecimal totalValue = BigDecimal.ZERO;
        String bestAsset = null;
        BigDecimal bestPerformance = BigDecimal.ZERO;
        String worstAsset = null;
        BigDecimal worstPerformance = BigDecimal.ZERO;
    }
}
