package cn.loveyx815.blockchain.utils;

import java.math.BigDecimal;
import java.math.BigInteger;


public class ConvertUtils {

    /**
     * 转为入库格式
     * @param number
     * @param factor
     * @return
     */
    public static BigDecimal dbFormat(BigDecimal number, Integer factor) {
        return number.divide(BigDecimal.TEN.pow(factor));
    }

    public static BigDecimal dbFormat(String number, Integer factor) {
        return dbFormat(new BigDecimal(number), factor);
    }

    /**
     * 转为上链格式
     * @param number
     * @param factor
     * @return
     */
    public static BigInteger onChainFormat(BigDecimal number, Integer factor) {
        return number.multiply(BigDecimal.TEN.pow(factor)).toBigInteger();
    }

    public static BigInteger onChainFormat(String number, Integer factor) {
        return onChainFormat(new BigDecimal(number), factor);
    }
}
