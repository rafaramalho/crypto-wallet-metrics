package ch.post.wallet.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import ch.post.wallet.entity.CryptoAsset;
import ch.post.wallet.wrapper.CryptoAssetResponse;

public class CoincapTest {

    @Value("${coincap.api.url}")
    private String coincapApiUrl;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private CoincapApi coincap;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testFetchCryptoAssetsSuccess() {
        CryptoAssetResponse mockResponse = new CryptoAssetResponse();
        mockResponse.setData(
                List.of(new CryptoAsset(1L, "bitcoin", "BTC", new BigDecimal("50000"), LocalDateTime.now())));

        when(restTemplate.getForObject(eq(coincapApiUrl), eq(CryptoAssetResponse.class)))
                .thenReturn(mockResponse);

        List<CryptoAsset> assets = coincap.fetchCryptoAssets();
        assertNotNull(assets);
        assertEquals(1, assets.size());
        assertEquals("BTC", assets.get(0).getSymbol());
    }

    @Test
    public void testFetchCryptoAssetsError() {
        when(restTemplate.getForObject(anyString(), eq(CryptoAssetResponse.class)))
                .thenThrow(new RestClientException("API connection error"));

        List<CryptoAsset> assets = coincap.fetchCryptoAssets();
        assertNotNull(assets);
        assertTrue(assets.isEmpty());
    }
}
