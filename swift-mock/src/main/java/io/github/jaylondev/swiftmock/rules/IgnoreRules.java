package io.github.jaylondev.swiftmock.rules;

import io.github.jaylondev.swiftmock.annotations.ExcludeClasses;
import io.github.jaylondev.swiftmock.annotations.IgnoreSpy;
import io.github.jaylondev.swiftmock.utils.BeanUtils;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
@Slf4j
public class IgnoreRules {

    private final Set<Class<?>> ignoredFieldTypes = new HashSet<>();
    private final Set<Class<?>> ignoredClasses = new HashSet<>();
    private final List<String> ignoredClassSuffixes = new ArrayList<>();
    private final Set<Class<?>> mockedClasses = new HashSet<>();

    public IgnoreRules(Class<?> testClass) {
        Collections.addAll(ignoredFieldTypes, String.class, Integer.class, Date.class, Map.class);
        IgnoreSpy ignoreSpy = testClass.getAnnotation(IgnoreSpy.class);
        if (ignoreSpy != null) {
            Collections.addAll(ignoredFieldTypes, ignoreSpy.fields());
            Collections.addAll(ignoredClasses, ignoreSpy.classes());
            ignoredClassSuffixes.addAll(Arrays.asList(ignoreSpy.classSuffixes()));
        }
        ExcludeClasses excludeClasses = testClass.getAnnotation(ExcludeClasses.class);
        if (excludeClasses != null) {
            Collections.addAll(ignoredClasses, excludeClasses.classes());
        }
    }

    public boolean shouldSkipClass(Class<?> clazz) {
        boolean shouldSkip = clazz == null ||
                BeanUtils.isInterface(clazz) ||
                mockedClasses.contains(clazz) ||
                ignoredClasses.contains(clazz) ||
                ignoredClassSuffixes.stream().anyMatch(clazz.getSimpleName()::endsWith);
        if (!shouldSkip) {
            log.debug("[SwiftMock] Class {} is not skipped", clazz);
        }
        return shouldSkip;
    }

    public boolean isAlreadyMocked(Object obj) {
        if (obj == null) {
            return false;
        }
        return mockedClasses.contains(obj.getClass());
    }


    public boolean isIgnoredType(Class<?> fieldType) {
        return fieldType.isPrimitive() || ignoredFieldTypes.contains(fieldType);
    }

    public void markAsMocked(Class<?> clazz) {
        if (clazz != null) {
            mockedClasses.add(clazz);
        }
    }
}
