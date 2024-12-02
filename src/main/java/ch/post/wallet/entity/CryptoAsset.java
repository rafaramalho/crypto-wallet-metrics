package ch.post.wallet.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "CRYPTO_ASSET", indexes = {
        @Index(name = "idx_symbol", columnList = "symbol"),
        @Index(name = "idx_timestamp", columnList = "timestamp")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CryptoAsset {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long key;

    private String id;
    private String symbol;

    @Column(precision = 36, scale = 16)
    private BigDecimal priceUsd;

    private LocalDateTime timestamp;

}