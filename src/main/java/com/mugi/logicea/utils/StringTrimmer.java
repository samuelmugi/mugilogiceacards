package com.mugi.logicea.utils;

import org.apache.commons.lang3.ObjectUtils;

public class StringTrimmer {

    public static String trimString(String value) {
        if(ObjectUtils.isEmpty(value)) return "";
        return value.trim();
    }
}
