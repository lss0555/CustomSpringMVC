package com.sxkj.annotation;

import java.lang.annotation.*;

/**
 * @Description RequstParam 注解
 * @Author lss0555
 * @Date 2019/1/22/022 16:54
 **/
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CustomRequstParam {
    //表示参数的别名，必填
    String value() ;
}
