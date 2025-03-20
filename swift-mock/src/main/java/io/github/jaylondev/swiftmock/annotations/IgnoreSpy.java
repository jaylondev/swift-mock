package io.github.jaylondev.swiftmock.annotations;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface IgnoreSpy {
    /**
     * 忽略被标记的类的成员变量，不生成其spy替身
     */
    Class<?>[] fields() default {};

    /**
     * 忽略被标记类，不生成spy替身
     */
    Class<?>[] classes() default {};

    /**
     * 不生成spy替身类的后缀名
     */
    String[] classSuffixes() default {};

}
