package com.presonal.jw.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(java.lang.annotation.ElementType.TYPE)//���÷�Χ�����ڽӿڻ�����
@Retention(RetentionPolicy.RUNTIME)//ע�����calss�ֽ����ļ��д��ڡ�������ʱ����ͨ������ȡ��
@Documented //˵����ע�⽫��������javadoc��  @Inherited :˵����ע����Լ̳и����еĸ�ע��
public @interface CustomController {
	String value() default "";//value������ȡע���ϵ�ֵ��default��Ĭ��ֵ
}
