package com.presonal.jw.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(java.lang.annotation.ElementType.TYPE)//作用范围，用在接口或类上
@Retention(RetentionPolicy.RUNTIME)//注解会在calss字节码文件中存在。在运行时可以通过反射取到
@Documented //说明该注解将被包含在javadoc中  @Inherited :说明该注解可以继承父类中的该注解
public @interface CustomController {
	String value() default "";//value方法获取注解上的值，default是默认值
}
