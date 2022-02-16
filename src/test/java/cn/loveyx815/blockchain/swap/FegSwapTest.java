package cn.loveyx815.blockchain.swap;

import cn.loveyx815.blockchain.contract.FegPresale;
import cn.loveyx815.blockchain.utils.ConvertUtils;
import cn.loveyx815.blockchain.utils.KeyUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.ECKeyPair;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.RawTransactionManager;
import org.web3j.tx.TransactionManager;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Scanner;

/**
 * @ClassName FegSwapTest
 * @Description FegSwapTest
 * @Author shiyonggang
 * @Date 2022/2/16 下午3:40
 * @Version 1.0
 */
@Slf4j
public class FegSwapTest {
    //todo 钱包私钥替换
    private String privateKey = "";
    public static Web3j web3j = Web3j.build(new HttpService("https://bsc-dataseed1.binance.org"));
    public Credentials wallet = Credentials.create(ECKeyPair.create(KeyUtil.getWalletKey(privateKey)));


    /**
     * Feg预售抢购
     * @throws Exception
     */
    @Test
    public void testFegPresale() throws Exception {
        TransactionManager transactionManager = new RawTransactionManager(
                web3j, wallet, Byte.parseByte("56"));

        //bnb支付金额
        BigDecimal amount = BigDecimal.valueOf(1);
        //转成链上数据
        BigInteger amountDecimal = ConvertUtils.onChainFormat(amount, 18);

        BigInteger gasPrice = ConvertUtils.onChainFormat(BigDecimal.valueOf(7),9);
        BigInteger gasLimit = BigInteger.valueOf(810266L);
        Scanner sc = new Scanner(System.in);
        System.out.println("print pair contract: ");
        //预售合约地址（非token合约地址）
        String contact = null;
        if (sc.hasNext()){
            contact = sc.next();
        }
        try {
            FegPresale token = FegPresale.load(contact,web3j,transactionManager,gasPrice,gasLimit);
            TransactionReceipt send = token.JOIN_PRESALE(amountDecimal).send();
            System.out.println("send.toString() = " + send.toString());
        }catch (Exception e){
            FegPresale token = FegPresale.load(contact,web3j,transactionManager,gasPrice,gasLimit);
            TransactionReceipt send = token.JOIN_PRESALE(amountDecimal).send();
            System.out.println("send.toString() = " + send.toString());
        }

    }


    @Test
    public void testKey(){
        System.out.println(KeyUtil.getWalletKey(privateKey));
    }
}
