package com.mark.sign.utils;


public class StringUtils {
    public static boolean isEmpty(CharSequence str) {
        return str == null || str.length() == 0;
    }

    public static boolean notEmpty(CharSequence str) {
        return !isEmpty(str);
    }

}
