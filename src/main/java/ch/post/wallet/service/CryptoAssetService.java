package ch.post.wallet.service;

import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import ch.post.wallet.entity.CryptoAsset;
import ch.post.wallet.repository.CryptoAssetRepository;
import jakarta.transaction.Transactional;

@Service
public class CryptoAssetService {

    private static final Logger logger = LoggerFactory.getLogger(CryptoAssetService.class);

    @Autowired
    private CoincapApi coincap;

    @Autowired
    private CryptoAssetRepository cryptoAssetRepository;

    @Autowired
    private CryptoAssetTransformation assetTransformation;

    @Scheduled(fixedRateString = "${scheduler.interval}")
    @Transactional
    public void updateCryptoAssets() {
        List<CryptoAsset> assets = coincap.fetchCryptoAssets().stream()
                .map(assetTransformation::transform)
                .filter(asset -> asset != null).toList();

        cryptoAssetRepository.saveAll(assets);
        logger.info("Saved {} assets to the database.", assets.size());
    }
}
