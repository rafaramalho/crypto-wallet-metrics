package ch.post.wallet.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Calendar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import ch.post.wallet.entity.CryptoAsset;

@Component
public class CryptoAssetTransformation {

    private static final Logger logger = LoggerFactory.getLogger(CryptoAssetTransformation.class);

    public CryptoAsset transform(CryptoAsset asset) {

        if (!StringUtils.hasText(asset.getSymbol())) {
            logger.error("Symbol for asset is not valid, it will not be updated");
            return null;
        }
        // Normalizing symbol to uppercase
        asset.setSymbol(asset.getSymbol().toUpperCase());

        if (asset.getPriceUsd() == null || asset.getPriceUsd().compareTo(BigDecimal.ZERO) <= 0) {
            logger.error("Price for asset {} is not valid, it will not be updated", asset.getSymbol());
            return null;
        }

        // Set timestamp to now
        asset.setTimestamp(LocalDateTime.now());

        return asset;
    }
}
