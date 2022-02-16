/**
 * 存信信息技术有限公司 www.treefinance.com.cn Copyright (c) 2005-2016 All Rights Reserved.
 */
package cn.loveyx815.blockchain.utils;

import com.google.common.collect.Lists;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class DataConverterUtils {
    private static final Pattern PATTERN = Pattern.compile("1[3578]\\d-?\\d{4}-?\\d{4}");

    private DataConverterUtils() {

    }

    /**
     * @param sourceList
     * @return
     */
    public static <F, T> List<T> convert(List<F> sourceList, Class<T> targetClz) {
        if (CollectionUtils.isNotEmpty(sourceList)) {
            List<T> ret = Lists.newArrayListWithExpectedSize(sourceList.size());
            for (F source : sourceList) {
                ret.add(convert(source, targetClz));
            }
            return ret;
        }
        return Lists.newArrayList();
    }

    public static <F, T> T convert(F source, Class<T> targetClz) {
        if(null == source){
            return null;
        }
        try {
            T target = targetClz.newInstance();
            BeanUtils.copyProperties(source, target);
            return target;
        } catch (IllegalAccessException | InstantiationException | ExceptionInInitializerError
                | SecurityException e) {
            throw new RuntimeException(
                    "failed to create instance of " + targetClz.getName() + " - " + e.getMessage(), e);
        }
    }

    public static String matchMobile(String title) {
        Matcher matcher = PATTERN.matcher(title);
        if (matcher.find()) {
            return matcher.group().replaceAll("-", StringUtils.EMPTY);
        }
        return null;
    }
}
