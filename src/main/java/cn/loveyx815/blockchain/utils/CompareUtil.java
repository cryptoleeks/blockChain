package cn.loveyx815.blockchain.utils;

import java.math.BigDecimal;

/**
 * @ClassName CompareUtil
 * @Description CompareUtil
 * @Author shiyonggang
 * @Date 2021/4/28 下午7:30
 * @Version 1.0
 */
public class CompareUtil {

    public static   boolean compareBigDecimal(BigDecimal var1, BigDecimal var2){

        return var1.compareTo(var2) > 0;
    }

}
