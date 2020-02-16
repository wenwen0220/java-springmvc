package com.presonal.jw.argumentResolver;

import java.lang.reflect.Method;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface ArgumentResolver {
	
	//�ж��Ƿ���Ҫ������request����Ҫ������param��Ҫ������
	public boolean support(Class<?> type,int paramIndex,Method method);
	//���������ķ���
	public Object argumentResolver(HttpServletRequest request,HttpServletResponse response,int paramIndex,Method method);

}
