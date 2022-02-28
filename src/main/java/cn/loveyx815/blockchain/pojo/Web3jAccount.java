package cn.loveyx815.blockchain.pojo;

import cn.loveyx815.blockchain.utils.KeyUtil;
import lombok.Builder;
import lombok.Data;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.ECKeyPair;

import java.math.BigDecimal;

/**
 * @ClassName Web3jAccount
 * @Description Web3jAccount
 * @Author shiyonggang
 * @Date 2022/2/19 下午1:56
 * @Version 1.0
 */
@Data
@Builder
public class Web3jAccount {

    private String privateKey;

    /**
     * 额度（BNB）
     */
    private BigDecimal presaleAmount;

    public  Credentials getWallet(){
        Credentials wallet = Credentials.create(ECKeyPair.create(KeyUtil.getWalletKey(this.getPrivateKey())));
        return wallet;
    }

}
