package ch.post.wallet.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import ch.post.wallet.dto.WalletInput;
import ch.post.wallet.dto.WalletOutput;
import ch.post.wallet.entity.CryptoAsset;
import ch.post.wallet.repository.CryptoAssetRepository;
import ch.post.wallet.repository.WalletRepository;

public class WalletServiceTest {

    @Mock
    private WalletRepository walletRepository;

    @Mock
    private CryptoAssetRepository assetRepository;

    @InjectMocks
    private WalletService walletService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testCalculateWalletMetrics_withValidInputs() throws ExecutionException, InterruptedException {

        WalletInput input1 = new WalletInput("BTC", BigDecimal.valueOf(2), BigDecimal.valueOf(50000),
                LocalDateTime.now());
        WalletInput input2 = new WalletInput("ETH", BigDecimal.valueOf(5), BigDecimal.valueOf(3000),
                LocalDateTime.now());

        CryptoAsset asset1 = new CryptoAsset(1L, "bitcoin", "BTC", BigDecimal.valueOf(60000), LocalDateTime.now());
        CryptoAsset asset2 = new CryptoAsset(2L, "ethereum", "ETH", BigDecimal.valueOf(3500), LocalDateTime.now());

        when(assetRepository.findBySymbolAndTimestamp(any(), eq("BTC"))).thenReturn(Optional.of(asset1));
        when(assetRepository.findBySymbolAndTimestamp(any(), eq("ETH"))).thenReturn(Optional.of(asset2));

        List<WalletInput> inputs = Arrays.asList(input1, input2);

        WalletOutput output = walletService.calculateWalletMetrics(inputs);

        assertNotNull(output);
        assertEquals(BigDecimal.valueOf(137500).setScale(2), output.getTotal());
        assertEquals("BTC", output.getBestAsset());
        assertEquals(BigDecimal.valueOf(20.00).setScale(2), output.getBestPerformance());
        assertEquals("ETH", output.getWorstAsset());
        assertEquals(BigDecimal.valueOf(16.67).setScale(2), output.getWorstPerformance());
    }

    @Test
    public void testCalculateWalletMetrics_withMissingAsset() throws ExecutionException, InterruptedException {

        WalletInput input = new WalletInput("XRP", BigDecimal.valueOf(1000), BigDecimal.valueOf(1),
                LocalDateTime.now());

        when(assetRepository.findBySymbolAndTimestamp(any(), eq("XRP"))).thenReturn(Optional.empty());

        List<WalletInput> inputs = Arrays.asList(input);

        WalletOutput output = walletService.calculateWalletMetrics(inputs);

        assertNotNull(output);
        assertEquals(BigDecimal.ZERO.setScale(2), output.getTotal());
        assertEquals("XRP", output.getBestAsset());
        assertEquals("XRP", output.getWorstAsset());
    }

    @Test
    public void testCalculateWalletMetrics_withEmptyInputs() {

        List<WalletInput> inputs = Arrays.asList();

        WalletOutput output = walletService.calculateWalletMetrics(inputs);

        assertNotNull(output);
        assertEquals(BigDecimal.ZERO.setScale(2), output.getTotal());
        assertNull(output.getBestAsset());
        assertNull(output.getWorstAsset());
    }

    @Test
    public void testCalculatePerformancePercentage() {

        WalletInput input = new WalletInput("BTC", BigDecimal.valueOf(2), BigDecimal.valueOf(50000),
                LocalDateTime.now());
        BigDecimal currentValue = BigDecimal.valueOf(120000);

        BigDecimal performancePercentage = walletService.calculatePerformancePercentage(input, currentValue);

        assertEquals(BigDecimal.valueOf(20.00).setScale(2), performancePercentage.setScale(2));
    }
}
