package ch.post.wallet.wrapper;

import java.util.List;

import ch.post.wallet.entity.CryptoAsset;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CryptoAssetResponse {

    private List<CryptoAsset> data;

}
