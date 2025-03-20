package io.github.jaylondev.swiftmock.container;

import org.mockito.Mockito;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MockRegistry {

    private final static Map<Class<?>, Object> MOCK_MAP = new ConcurrentHashMap<>();

    public <T> T findMock(Class<T> clazz) {
        if (clazz == null) {
            return null;
        }
        Class<T> implClass = (Class<T>) TargetFilesClassesContainer.getInstance().findImplementationClass(clazz);
        return (T) MOCK_MAP.get(implClass);
    }

    public <T> T getOrCreateSpy(Class<T> clazz) {
        if (clazz == null) {
            throw new IllegalArgumentException("Cannot create spy for null class");
        }
        Class<T> implClass = (Class<T>) TargetFilesClassesContainer.getInstance().findImplementationClass(clazz);
        return (T) MOCK_MAP.computeIfAbsent(implClass, c -> Mockito.spy(c));
    }


}
