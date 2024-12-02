package ch.post.wallet.repository;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import ch.post.wallet.entity.CryptoAsset;

@Repository
public interface CryptoAssetRepository extends JpaRepository<CryptoAsset, Long> {

    // Custom query to find the asset with the closest timestamp for a specific
    // symbol
    @Query(value = "SELECT * FROM crypto_asset WHERE symbol = :symbol AND timestamp <= :timestamp ORDER BY timestamp DESC LIMIT 1", nativeQuery = true)
    Optional<CryptoAsset> findBySymbolAndTimestamp(LocalDateTime timestamp, String symbol);

}
