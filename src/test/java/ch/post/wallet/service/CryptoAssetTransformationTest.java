package ch.post.wallet.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ch.post.wallet.entity.CryptoAsset;

public class CryptoAssetTransformationTest {

    private CryptoAssetTransformation transformation;

    @BeforeEach
    public void setUp() {
        transformation = new CryptoAssetTransformation();
    }

    @Test
    void testValidCryptoAsset() {
        CryptoAsset asset = new CryptoAsset(1L, "bitcoin", "btc", new BigDecimal("50000"),
                LocalDateTime.now().minusDays(1));
        CryptoAsset result = transformation.transform(asset);

        assertNotNull(result);
        assertEquals(1L, result.getKey());
        assertEquals("BTC", result.getSymbol());
        assertEquals(new BigDecimal("50000"), result.getPriceUsd());
        assertEquals(asset.getTimestamp(), result.getTimestamp());
    }

    @Test
    void testNullSymbol() {
        CryptoAsset asset = new CryptoAsset(2L, "bitcoin", null, new BigDecimal("50000"),
                LocalDateTime.now().minusDays(1));
        CryptoAsset result = transformation.transform(asset);

        assertNull(result);
    }

    @Test
    void testEmptySymbol() {
        CryptoAsset asset = new CryptoAsset(3L, "bitcoin", "", new BigDecimal("50000"),
                LocalDateTime.now().minusDays(1));
        CryptoAsset result = transformation.transform(asset);

        assertNull(result);
    }

    @Test
    void testNullPrice() {
        CryptoAsset asset = new CryptoAsset(4L, "bitcoin", "btc", null, LocalDateTime.now().minusDays(1));
        CryptoAsset result = transformation.transform(asset);

        assertNull(result);
    }

    @Test
    void testZeroPrice() {
        CryptoAsset asset = new CryptoAsset(5L, "bitcoin", "btc", BigDecimal.ZERO, LocalDateTime.now().minusDays(1));
        CryptoAsset result = transformation.transform(asset);

        assertNull(result);
    }

    @Test
    void testNegativePrice() {
        CryptoAsset asset = new CryptoAsset(6L, "bitcoin", "btc", new BigDecimal("-100"),
                LocalDateTime.now().minusDays(1));
        CryptoAsset result = transformation.transform(asset);

        assertNull(result);
    }

    @Test
    void testNullTimestamp() {
        CryptoAsset asset = new CryptoAsset(7L, "bitcoin", "btc", new BigDecimal("50000"), null);
        CryptoAsset result = transformation.transform(asset);

        assertNotNull(result);
        assertEquals(7L, result.getKey());
        assertEquals("BTC", result.getSymbol());
        assertEquals(new BigDecimal("50000"), result.getPriceUsd());
    }

}
