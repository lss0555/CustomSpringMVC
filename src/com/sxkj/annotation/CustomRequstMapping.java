package com.sxkj.annotation;

import java.lang.annotation.*;

/**
 * @Description RequstMapping注解
 * @Author lss0555
 * @Date 2019/1/22/022 16:54
 **/
@Target({ElementType.TYPE,ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CustomRequstMapping {
    //表示访问该方法的url
    String value() default "";
}
