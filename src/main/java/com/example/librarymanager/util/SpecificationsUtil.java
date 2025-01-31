package com.example.librarymanager.util;

import com.example.librarymanager.constant.ErrorMessage;
import com.example.librarymanager.exception.BadRequestException;

import java.util.ArrayList;
import java.util.List;

public class SpecificationsUtil {

    public static Object castToRequiredType(Class<?> fieldType, String value) {
        try {
            if (fieldType.isAssignableFrom(Double.class)) {
                return Double.valueOf(value);
            } else if (fieldType.isAssignableFrom(Float.class)) {
                return Float.valueOf(value);
            } else if (fieldType.isAssignableFrom(Long.class)) {
                return Long.valueOf(value);
            } else if (fieldType.isAssignableFrom(Integer.class)) {
                return Integer.valueOf(value);
            } else if (fieldType.isAssignableFrom(Short.class)) {
                return Short.valueOf(value);
            } else if (fieldType.isAssignableFrom(Byte.class)) {
                return Byte.valueOf(value);
            } else if (fieldType.isAssignableFrom(Boolean.class)) {
                return Boolean.valueOf(value);
            }
        } catch (NumberFormatException e) {
            throw new BadRequestException(ErrorMessage.INVALID_NUMBER_FORMAT);
        }
        return null;
    }

    public static Object castToRequiredType(Class<?> fieldType, List<String> value) {
        List<Object> lists = new ArrayList<>();
        for (String s : value) {
            lists.add(castToRequiredType(fieldType, s));
        }
        return lists;
    }
}
