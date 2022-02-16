package cn.loveyx815.blockchain.pojo;

import lombok.Data;

import java.math.BigInteger;

/**
 * @ClassName WalletKey
 * @Description WalletKey
 * @Author shiyonggang
 * @Date 2022/2/16 下午7:28
 * @Version 1.0
 */
@Data
public class WalletKey {

    /**
     * 钱包地址
     */
    private String address;

    /**
     * web3j连接的key
     */
    private BigInteger key;

}
