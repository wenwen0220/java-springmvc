package com.presonal.jw.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({java.lang.annotation.ElementType.TYPE,java.lang.annotation.ElementType.METHOD})//���÷�Χ�����ڽӿڻ����ϣ��򷽷���
@Retention(RetentionPolicy.RUNTIME)//ע�����calss�ֽ����ļ��д��ڡ�������ʱ����ͨ������ȡ��
@Documented //˵����ע�⽫��������javadoc��  @Inherited :˵����ע����Լ̳и����еĸ�ע��
public @interface CustomRequestMapping {
	String value() default "";//value������ȡע���ϵ�ֵ��default��Ĭ��ֵ
}