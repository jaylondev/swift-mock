package io.github.jaylondev.swiftmock.annotations;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface ExcludeClasses {
    /**
     * 忽略被标记类，不生成spy替身
     */
    Class<?>[] classes() default {};
}
