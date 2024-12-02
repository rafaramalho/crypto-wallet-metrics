package ch.post.wallet.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import ch.post.wallet.util.MillisToLocalDateTimeDeserializer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WalletInput {

    private String symbol;
    private BigDecimal quantity;
    private BigDecimal price;

    @JsonDeserialize(using = MillisToLocalDateTimeDeserializer.class)
    private LocalDateTime timestamp; // Optional, can be null

}
