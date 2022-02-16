package cn.loveyx815.blockchain.utils;

import java.math.BigDecimal;
import java.text.DecimalFormat;


public class ScaleUtils {


    public static BigDecimal setScale(BigDecimal amount) {
        return setScale(amount, BigDecimal.ROUND_HALF_UP);
    }

    public static BigDecimal setScale(BigDecimal amount, Integer roundingMode) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        BigDecimal absAmount = amount.abs();
        if (absAmount.compareTo(BigDecimal.ONE) <= 0) {
            return amount.setScale(6, roundingMode);
        }
        if (absAmount.compareTo(BigDecimal.ONE) > 0 && absAmount.compareTo(BigDecimal.valueOf(100)) <= 0) {
            return amount.setScale(4, roundingMode);
        }
        if (absAmount.compareTo(BigDecimal.valueOf(100)) > 0 && absAmount.compareTo(BigDecimal.valueOf(10000)) <= 0) {
            return amount.setScale(2, roundingMode);
        }
        return amount.setScale(0, roundingMode);
    }

    /**
     * null 默认返回 0
     * @param price
     * @return
     */
//    public static String getPriceScale(BigDecimal price) {
//        if (price == null || price.compareTo(BigDecimal.ZERO) == 0) {
//            return BigDecimal.ZERO.toPlainString();
//        }
//        BigDecimal absAmount = price.abs();
//        if (absAmount.compareTo(BigDecimal.valueOf(0.000001))<0){
//            //科学计数
//            return NumberFormatUtils.format(price,2);
//        }
//        int roundingMode = BigDecimal.ROUND_HALF_UP;
//        if (absAmount.compareTo(BigDecimal.ONE) <= 0) {
//            return price.setScale(6, roundingMode).stripTrailingZeros().toPlainString();
//        }
//        if (absAmount.compareTo(BigDecimal.ONE) > 0 && absAmount.compareTo(BigDecimal.valueOf(100)) <= 0) {
//            return price.setScale(4, roundingMode).stripTrailingZeros().toPlainString();
//        }
//        if (absAmount.compareTo(BigDecimal.valueOf(100)) > 0 && absAmount.compareTo(BigDecimal.valueOf(10000)) <= 0) {
//            return price.setScale(2, roundingMode).stripTrailingZeros().toPlainString();
//        }
//        return price.setScale(0, roundingMode).stripTrailingZeros().toPlainString();
//    }
//
//    /**
//     * null直接返回null
//     * @param price
//     * @return
//     */
//    public static String getPriceScaleV2(BigDecimal price) {
//        if (price == null ){
//            return null;
//        }
//        if(price.compareTo(BigDecimal.ZERO) == 0) {
//            return BigDecimal.ZERO.toPlainString();
//        }
//        BigDecimal absAmount = price.abs();
//        if (absAmount.compareTo(BigDecimal.valueOf(0.000001))<0){
//            //科学计数
//            return NumberFormatUtils.format(price,2);
//        }
//        int roundingMode = BigDecimal.ROUND_HALF_UP;
//        if (absAmount.compareTo(BigDecimal.ONE) <= 0) {
//            return price.setScale(6, roundingMode).stripTrailingZeros().toPlainString();
//        }
//        if (absAmount.compareTo(BigDecimal.ONE) > 0 && absAmount.compareTo(BigDecimal.valueOf(100)) <= 0) {
//            return price.setScale(4, roundingMode).stripTrailingZeros().toPlainString();
//        }
//        if (absAmount.compareTo(BigDecimal.valueOf(100)) > 0 && absAmount.compareTo(BigDecimal.valueOf(10000)) <= 0) {
//            return price.setScale(2, roundingMode).stripTrailingZeros().toPlainString();
//        }
//        return price.setScale(0, roundingMode).stripTrailingZeros().toPlainString();
//    }

    /**
     * 设置百分比
     * @param amount 计算后的百分数
     * @return
     */
    public static String setPercentScale(BigDecimal amount) {
        int roundingMode = BigDecimal.ROUND_HALF_UP;
        if (amount == null) {
            return "0.00";
        }
        BigDecimal bigDecimal = amount.setScale(2, BigDecimal.ROUND_HALF_UP);
        // 不足两位小数补0
        DecimalFormat decimalFormat = new DecimalFormat("0.00#");
        return decimalFormat.format(bigDecimal);
    }

    /**
     * 设置百分比
     * @param amount 小数
     * @return
     */
    public static String setPercentScaleV2(BigDecimal amount) {
        int roundingMode = BigDecimal.ROUND_HALF_UP;
        if (amount == null) {
            return null;
        }
        // amount * 100
        BigDecimal bigDecimal = amount.multiply(BigDecimal.TEN.multiply(BigDecimal.TEN)).setScale(2, BigDecimal.ROUND_HALF_UP);
        // 不足两位小数补0
        DecimalFormat decimalFormat = new DecimalFormat("0.00#");
        return decimalFormat.format(bigDecimal);
    }

    public static void main(String[] args) {
//        System.out.println(setPercentScale(BigDecimal.valueOf(1.2)));
//        System.out.println(setPercentScale(BigDecimal.valueOf(0.0)));
//        System.out.println(setPercentScale(BigDecimal.valueOf(0.1)));
//        System.out.println(setPercentScale(BigDecimal.valueOf(200)));
//        System.out.println(setPercentScale(BigDecimal.valueOf(200.4)));
//        System.out.println(getPriceScale(BigDecimal.valueOf(0)));
    }


}
