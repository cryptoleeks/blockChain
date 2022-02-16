package cn.loveyx815.blockchain.utils;

import cn.loveyx815.blockchain.pojo.WalletKey;
import org.apache.commons.lang3.StringUtils;
import org.web3j.crypto.ECKeyPair;
import org.web3j.crypto.Keys;

import java.math.BigInteger;

/**
 * @ClassName KeyUtil
 * @Description KeyUtil
 * @Author shiyonggang
 * @Date 2022/2/16 下午7:19
 * @Version 1.0
 */
public class KeyUtil {
    private final static String ADDRESS_PREFIX = "0x";

    /**
     * get key to connect web3j
     * @param privateKey 私钥
     * @return
     */
    public static BigInteger getWalletKey(String privateKey){
        if (StringUtils.startsWith(privateKey, ADDRESS_PREFIX)){
            privateKey = StringUtils.substring(privateKey,ADDRESS_PREFIX.length());
        }
        return process(privateKey).getKey();

    }

    private static WalletKey process(String privateKey){
        WalletKey walletKey = new WalletKey();
        BigInteger bigInteger = new BigInteger(privateKey, 16);

        ECKeyPair ecKeyPair = ECKeyPair.create(bigInteger);

        walletKey.setAddress(ADDRESS_PREFIX+Keys.getAddress(ecKeyPair));
        walletKey.setKey(ecKeyPair.getPrivateKey());

        return walletKey;
    }
}
