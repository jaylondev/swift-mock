package io.github.jaylondev.swiftmock.init;

import io.github.jaylondev.swiftmock.container.MockRegistry;
import io.github.jaylondev.swiftmock.container.TargetFilesClassesContainer;
import io.github.jaylondev.swiftmock.rules.IgnoreRules;
import io.github.jaylondev.swiftmock.utils.BeanUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public class MockInitializer {

    private final MockRegistry mockRegistry;
    private final IgnoreRules ignoreRules;

    public MockInitializer(MockRegistry mockRegistry, IgnoreRules ignoreRules) {
        this.mockRegistry = mockRegistry;
        this.ignoreRules = ignoreRules;
    }

    public void initializeMocksRecursively(Object instance) throws Exception {
        if (instance == null) return;

        Class<?> clazz = TargetFilesClassesContainer.getInstance().findImplementationClass(instance.getClass());
        if (ignoreRules.shouldSkipClass(clazz)) return;

        ignoreRules.markAsMocked(clazz);
        for (Field field : BeanUtils.getFieldsIncludeSuperNonNull(clazz)) {
            field.setAccessible(true);
            if (shouldSkipField(instance, field)) continue;

            Object mockInstance = mockRegistry.getOrCreateSpy(field.getType());
            this.setFieldValue(instance, field, mockInstance);

            if (!ignoreRules.isAlreadyMocked(mockInstance)) {
                initializeMocksRecursively(mockInstance);
            }
        }
    }

    private void setFieldValue(Object instance, Field field, Object mockInstance) throws IllegalAccessException {
        if (mockInstance == null) {
            return;
        }
        field.set(instance, mockInstance);
    }


    private boolean shouldSkipField(Object instance, Field field) throws IllegalAccessException {
        return isFieldAlreadySet(instance, field) ||
                isStaticField(field) ||
                ignoreRules.isIgnoredType(field.getType());
    }

    private boolean isFieldAlreadySet(Object instance, Field field) throws IllegalAccessException {
        return field.get(instance) != null;
    }

    private boolean isStaticField(Field field) {
        return Modifier.isStatic(field.getModifiers());
    }
}
