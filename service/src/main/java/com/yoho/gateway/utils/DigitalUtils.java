package com.yoho.gateway.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by sailing on 2015/12/3.
 */
public final class DigitalUtils {
    public static final String symbol_point = ".";
    public static String appendPrice2Digit(String price) {

        String result = price;
        if(StringUtils.isNotBlank(price) && price.contains(symbol_point)){
            String smallNum = price.substring(price.lastIndexOf(symbol_point) + 1);
            Pattern pattern = Pattern.compile("\\d{1}");
            Matcher matcher = pattern.matcher(smallNum);
            if(matcher.matches()){
                result = price + "0";
            }
        }
        return result;
    }
}
