package com.sxkj.annotation;

import java.lang.annotation.*;

/**
 * @Description controller注解
 * @Author lss0555
 * @Date 2019/1/22/022 16:54
 **/
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CustomController {
    String value() default "";
}
