package io.github.jaylondev.swiftmock.utils;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author jaylon 2023/9/12 22:49
 */
public class BeanUtils {

    public static List<Field> getFieldIncludeSuper(Class<?> clz) {
        List<Field> list = new ArrayList<>();
        while (clz != null && clz != Object.class) {
            list.addAll(Arrays.asList(clz.getDeclaredFields()));
            clz = clz.getSuperclass();
        }
        return list;
    }


    public static boolean isInterface(Class<?> clazz) {
        return clazz.isInterface() || Modifier.isAbstract(clazz.getModifiers());
    }

    public static List<Field> getFieldsIncludeSuperNonNull(Class<?> clazz) {
        return getFieldIncludeSuper(clazz).stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

}
