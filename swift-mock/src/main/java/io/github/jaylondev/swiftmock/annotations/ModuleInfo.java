package io.github.jaylondev.swiftmock.annotations;

import java.lang.annotation.*;


@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface ModuleInfo {

    /**
     * 测试类所在的module名，当relateModules不为空时必填
     * @return module name
     */
    String testModule() default "";

    /**
     * 关联module名称，多个以英文逗号分隔
     * 用于收集关联module的target目录下class列表，当测试目标类注入接口字段时可在列表找到接口的实现类
     * @return relate module name
     */
    String[] relatedModules() default {};
}
