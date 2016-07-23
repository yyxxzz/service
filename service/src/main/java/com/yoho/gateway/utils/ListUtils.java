package com.yoho.gateway.utils;

import java.util.List;

public final class ListUtils {
	
	/**
     * 截取列表
     */
    public static <T> List<T> getSubList(List<T> list, int begin,
            int end) {
        if (list == null || list.isEmpty()) {
            return null;
        }
        int startIndex = begin;
        int endIndex = begin + end;
        if (startIndex > endIndex || startIndex > list.size()) {
            return null;
        }
        if (endIndex > list.size()) {
            endIndex = list.size();
        }
        return list.subList(startIndex, endIndex);
    }
}
