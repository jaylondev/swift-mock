package io.github.jaylondev.swiftmock.container;

import lombok.extern.slf4j.Slf4j;
import org.mockito.Mockito;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class MockRegistry {

    private final static Map<Class<?>, Object> MOCK_MAP = new ConcurrentHashMap<>();

    public <T> T findMock(Class<T> clazz) {
        if (clazz == null) {
            return null;
        }
        Class<T> implClass = (Class<T>) TargetFilesClassesContainer.getInstance().findImplementationClass(clazz);
        return (T) MOCK_MAP.get(implClass);
    }

    public void addContainer(Object obj) {
        if (obj == null) {
            return;
        }
        Class<?> implClass = TargetFilesClassesContainer.getInstance().findImplementationClass(obj.getClass());
        MOCK_MAP.put(implClass, obj);
    }

    public <T> T getOrCreateSpy(Class<T> clazz) {
        if (clazz == null) {
            throw new IllegalArgumentException("Cannot create spy for null class");
        }
        Class<T> implClass = (Class<T>) TargetFilesClassesContainer.getInstance().findImplementationClass(clazz);
        return (T) MOCK_MAP.computeIfAbsent(implClass, c -> generateSpy(c));
    }

    private static Object generateSpy(Class<?> c) {
        try {
            return Mockito.spy(c);
        } catch (Exception e) {
            log.error("create {} spybean throw a exception", c.getName(), e);
            return null;
        }
    }


    public void clear() {
        MOCK_MAP.clear();
    }

}
