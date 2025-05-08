package io.github.jaylondev.swiftmock.base;

import io.github.jaylondev.swiftmock.container.MockRegistry;
import io.github.jaylondev.swiftmock.container.TargetFilesClassesContainer;
import io.github.jaylondev.swiftmock.init.MockInitializer;
import io.github.jaylondev.swiftmock.rules.IgnoreRules;

public abstract class SpyMockTest {

    private final MockRegistry mockRegistry = new MockRegistry();
    private final IgnoreRules ignoreRules;

    public abstract Object targetTestBean();

    public SpyMockTest() {
        try {
            Object testObject = targetTestBean();
            this.ignoreRules = new IgnoreRules(this.getClass());
            TargetFilesClassesContainer.getInstance().scanTargetClasses(this.getClass());
            Object spyTestBean = mockRegistry.getOrCreateSpy(testObject.getClass());
            new MockInitializer(mockRegistry, ignoreRules).initializeMocksRecursively(spyTestBean);
        } catch (Exception e) {
            throw new IllegalStateException("SpyMockTest initialization failed due to: " + e.getMessage(), e);
        }
    }

    public <T> T getSpyInstance(Class<T> clazz) {
        return mockRegistry.findMock(clazz);
    }

}
