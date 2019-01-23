package com.sxkj.annotation;

import java.lang.annotation.*;

/**
 * @Description Autowired注解
 * @Author lss0555
 * @Date 2019/1/22/022 16:54
 **/
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CustomAutowired {
    String value() default "";
}
