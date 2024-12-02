package ch.post.wallet.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WalletOutput {

    private BigDecimal total;
    private String bestAsset;
    private BigDecimal bestPerformance;
    private String worstAsset;
    private BigDecimal worstPerformance;

}
