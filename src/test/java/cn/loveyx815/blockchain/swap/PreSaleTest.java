package cn.loveyx815.blockchain.swap;

import cn.loveyx815.blockchain.contract.FegPresale;
import cn.loveyx815.blockchain.contract.PinkPresale;
import cn.loveyx815.blockchain.pojo.Web3jAccount;
import cn.loveyx815.blockchain.utils.ConvertUtils;
import cn.loveyx815.blockchain.utils.KeyUtil;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
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
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import static cn.loveyx815.blockchain.common.Constants.PRIVATE_KEY;

/**
 * @ClassName FegSwapTest
 * @Description FegSwapTest
 * @Author shiyonggang
 * @Date 2022/2/16 下午3:40
 * @Version 1.0
 */
@Slf4j
public class PreSaleTest {
    //todo 钱包私钥替换
    private String privateKey = PRIVATE_KEY;
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
    /**
     * Feg预售抢购
     * @throws Exception
     */
    @Test
    public void testFivePresale() throws Exception {
        TransactionManager transactionManager = new RawTransactionManager(
                web3j, wallet, Byte.parseByte("56"));

        //bnb支付金额
        BigDecimal amount = BigDecimal.valueOf(0.001);
        //转成链上数据
        BigInteger amountDecimal = ConvertUtils.onChainFormat(amount, 18);

        BigInteger gasPrice = ConvertUtils.onChainFormat(BigDecimal.valueOf(7),9);
        BigInteger gasLimit = BigInteger.valueOf(510266L);
        Scanner sc = new Scanner(System.in);
        System.out.println("print pair contract: ");
        //预售合约地址（非token合约地址）
        String contact = null;
        if (sc.hasNext()){
            contact = sc.next();
        }
        try {
            PinkPresale token = PinkPresale.load(contact,web3j,transactionManager,gasPrice,gasLimit);
            TransactionReceipt send = token.INVEST_IN(amountDecimal).send();
            System.out.println("send.toString() = " + send.toString());
        }catch (Exception e){
//            FegPresale token = FegPresale.load(contact,web3j,transactionManager,gasPrice,gasLimit);
//            TransactionReceipt send = token.JOIN_PRESALE(amountDecimal).send();
//            System.out.println("send.toString() = " + send.toString());
        }

    }
    volatile Boolean success = false;

    /**
     * pink预售抢购
     * @throws Exception
     */
    @Test
    public void testPinkPresale() throws Exception {
        int threads = 5;
        String contract = "0xB534301b615B25eeb952F6735954cCA40370e316";
        DateTimeFormatter format = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
        DateTime startDateTime = DateTime.parse("2022-02-17 06:00:00",format);
        //提前5s开始启动线程
        int deBuffTimeLimit = 5;
        ExecutorService executorService = Executors.newFixedThreadPool(threads);
        TransactionManager transactionManager = new RawTransactionManager(
                web3j, wallet, Byte.parseByte("56"));

        //bnb支付金额
        BigDecimal amount = BigDecimal.valueOf(0.3);
        //转成链上数据
        BigInteger amountDecimal = ConvertUtils.onChainFormat(amount, 18);

        BigInteger gasPrice = ConvertUtils.onChainFormat(BigDecimal.valueOf(7),9);
        BigInteger gasLimit = BigInteger.valueOf(610266L);
        PinkPresale token = PinkPresale.load(contract,web3j,transactionManager,gasPrice,gasLimit);

        for (int i =0 ; i < threads ; i++){
            Date deBuffDate = deBuffTimeLimit > 0 ? startDateTime.minusSeconds(deBuffTimeLimit--).toDate() : startDateTime.toDate();
            executorService.submit(()->{
                log.info("thread  submit");
               // deBuffTime 开始后，1s一个线程，到开始时间后不用限制
                //cas
                while (DateTime.now().toDate().getTime() < deBuffDate.getTime()  ){
                    //wait
                }
                log.warn("deBuffDate join by {}",deBuffDate);
                TransactionReceipt receipt = preSale(contract, token, amountDecimal);
                if (Objects.nonNull(receipt)){
                    success = true;
                }
            });
        }

        while (!success){
            TimeUnit.MILLISECONDS.sleep(5);
        }
        log.info("预售抢购成功！");

    }


    /**
     * 预售抢购多线程版
     * @throws Exception
     */
    @Test
    public void testPinkPresaleByMulitThread() throws Exception {
        List<Web3jAccount> accountList = new ArrayList<>();
        // 钱包私钥
        String privateKey1 = "";
        String privateKey2 = "";
        //账号添加

        //todo 多钱包额度确认 & 钱包确认
        accountList.add(Web3jAccount.builder().privateKey(privateKey1).presaleAmount(BigDecimal.valueOf(0.1)).build());
        accountList.add(Web3jAccount.builder().privateKey(privateKey2).presaleAmount(BigDecimal.valueOf(0.1)).build());

//        accountList.add(Web3jAccount.builder().privateKey(wangPrivateKey1).presaleAmount(BigDecimal.valueOf(0.6)).build());
//        accountList.add(Web3jAccount.builder().privateKey(wangPrivateKey2).presaleAmount(BigDecimal.valueOf(0.6)).build());

        //开启多线程
        ExecutorService executorService = Executors.newFixedThreadPool(accountList.size());
        AtomicReference<Integer> success = new AtomicReference<>(0);
        DateTimeFormatter format = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
        //TODO 开始时间修改 utc8
        DateTime startDateTime = DateTime.parse("2022-02-19 14:47:00",format);
        //TODO 预售合约修改
        String presaleContract = "0x072D40db9227Ddc350a740aEd7122B3A8A150Ed6";
        for (Web3jAccount web3jAccount : accountList) {
            Credentials wallet = web3jAccount.getWallet();
            TransactionManager transactionManager = new RawTransactionManager(
                    web3j, wallet, Byte.parseByte("56"));

            //bnb支付金额
            BigDecimal amount = web3jAccount.getPresaleAmount();
            //转成链上数据
            BigInteger amountDecimal = ConvertUtils.onChainFormat(amount, 18);

            BigInteger gasPrice = ConvertUtils.onChainFormat(BigDecimal.valueOf(7),9);
            BigInteger gasLimit = BigInteger.valueOf(510266L);

            executorService.submit(()->{
                log.info("thread  submit");
                //cas
                while (DateTime.now().toDate().getTime() < startDateTime.toDate().getTime()  ){
                    //wait
                }
                PinkPresale token = PinkPresale.load(presaleContract,web3j,transactionManager,gasPrice,gasLimit);
                TransactionReceipt send = null;
                try {

                    send = token.CONTRIBUTE(amountDecimal).send();
                    System.out.println("send.toString() = " + send.toString());
                }catch (Exception e){
                    log.error("error:",e);
                    try {
                        send = token.CONTRIBUTE(amountDecimal).send();
                    } catch (Exception exception) {
                        exception.printStackTrace();
                    }
                    System.out.println("send.toString() = " + send.toString());
                }
                if (Objects.nonNull(send)) {
                    success.getAndSet(success.get() + 1);
                }
            });
        }

        while (success.get() < accountList.size()){
            TimeUnit.MINUTES.sleep(3);
        }
        log.info("批量抢购成功！退出");



    }

    private TransactionReceipt preSale(String contract, PinkPresale token, BigInteger amountDecimal)  {

        if (success){
            log.info("已经有成功记录，本次将取消！");
            return null;
        }

        log.warn("success");
        TransactionReceipt receipt = new TransactionReceipt();

        try {
            receipt = token.CONTRIBUTE(amountDecimal).send();
            log.error("send.toString() = " + receipt.toString());
        }catch (Exception e){
            //todo remove test
            receipt = preSale(contract,token,amountDecimal);
        }
        return receipt;
    }


    @Test
    public void testKey(){
        System.out.println(KeyUtil.getWalletKey(privateKey));
    }
}
