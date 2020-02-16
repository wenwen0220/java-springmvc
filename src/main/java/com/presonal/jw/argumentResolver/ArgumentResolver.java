package com.presonal.jw.argumentResolver;

import java.lang.reflect.Method;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface ArgumentResolver {
	
	//判断是否需要解析（request不需要解析，param需要解析）
	public boolean support(Class<?> type,int paramIndex,Method method);
	//参数解析的方法
	public Object argumentResolver(HttpServletRequest request,HttpServletResponse response,int paramIndex,Method method);

}
