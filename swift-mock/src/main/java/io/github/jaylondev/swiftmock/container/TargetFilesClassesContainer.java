package io.github.jaylondev.swiftmock.container;

import io.github.jaylondev.swiftmock.annotations.ModuleInfo;
import io.github.jaylondev.swiftmock.utils.BeanUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;

@Slf4j
public class TargetFilesClassesContainer {

    private final List<Class<?>> collectedClasses = new ArrayList<>();
    private static volatile TargetFilesClassesContainer instance;

    private TargetFilesClassesContainer() {}

    public static TargetFilesClassesContainer getInstance() {
        if (instance == null) {
            synchronized (TargetFilesClassesContainer.class) {
                if (instance == null) {
                    instance = new TargetFilesClassesContainer();
                }
            }
        }
        return instance;
    }

    /**
     * 扫描 /target 目录下的所有 class 文件，收集所有类信息
     */
    public void scanTargetClasses(Class<?> testClass) {
        List<String> modulePaths = resolveModulePaths(testClass);
        for (String modulePath : modulePaths) {
            File targetDir = new File(modulePath).getParentFile();
            File classesDir = new File(targetDir, "classes");

            if (!classesDir.exists()) {
                continue;
            }

            String classRootPath = classesDir.toString();
            try {
                Files.walkFileTree(classesDir.toPath(), new SimpleFileVisitor<Path>() {
                    @Override
                    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
                        if (file.getFileName().toString().endsWith(".class")) {
                            String className = file.toString()
                                    .substring(classRootPath.length() + 1)
                                    .replace(File.separatorChar, '.')
                                    .replace(".class", "");

                            try {
                                Class<?> loadedClass = Class.forName(className);
                                collectedClasses.add(loadedClass);
                            } catch (ClassNotFoundException | NoClassDefFoundError e) {
                                log.warn("[SwiftMock] Unable to load class {} due to missing dependencies: {}", className, e.getMessage());
                            }
                        }
                        return FileVisitResult.CONTINUE;
                    }
                });
            } catch (Exception e) {
                log.warn("[SwiftMock] Error reading modules:", e);
            }
        }
    }

    /**
     * 解析所有 module 的全路径
     */
    private List<String> resolveModulePaths(Class<?> testClass) {
        ModuleInfo moduleInfo = testClass.getAnnotation(ModuleInfo.class);
        String testModule = moduleInfo != null ? moduleInfo.testModule() : null;
        String[] relatedModules = moduleInfo != null ? moduleInfo.relatedModules() : new String[0];

        String testClassPath = Objects.requireNonNull(testClass.getClassLoader().getResource("")).getFile();
        Set<String> modulePaths = new HashSet<>();
        modulePaths.add(testClassPath);

        if (!StringUtils.isEmpty(testModule)) {
            for (String relatedModule : relatedModules) {
                modulePaths.add(testClassPath.replaceFirst(testModule, relatedModule));
            }
        }

        return new ArrayList<>(modulePaths);
    }

    /**
     * 查找某个接口的具体实现类
     */
    public Class<?> findImplementationClass(Class<?> clazz) {
        if (clazz == null || !BeanUtils.isInterface(clazz)) {
            return clazz;
        }
        return collectedClasses.isEmpty() ? clazz :
                collectedClasses.stream()
                        .filter(c -> clazz.isAssignableFrom(c) && !c.isInterface())
                        .findFirst()
                        .orElse(clazz);
    }

}
