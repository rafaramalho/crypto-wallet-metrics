package ch.post.wallet.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import ch.post.wallet.entity.CryptoAsset;
import ch.post.wallet.wrapper.CryptoAssetResponse;

@Component
public class CoincapApi {

    private static final Logger logger = LoggerFactory.getLogger(CoincapApi.class);

    @Value("${coincap.api.url}")
    private String coincapApiUrl;

    @Autowired
    private RestTemplate restTemplate;

    public List<CryptoAsset> fetchCryptoAssets() {
        logger.info("Calling Coincap API to fetch crypto assets {}", coincapApiUrl);
        try {
            CryptoAssetResponse response = restTemplate.getForObject(coincapApiUrl, CryptoAssetResponse.class);
            return response != null ? response.getData() : List.of();
        } catch (RestClientException e) {
            logger.error("Error fetching data from Coincap API: {}", e.getMessage(), e);
            return List.of();
        }
    }
}
