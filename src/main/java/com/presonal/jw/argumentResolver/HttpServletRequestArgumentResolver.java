package com.presonal.jw.argumentResolver;

import java.lang.reflect.Method;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.presonal.jw.annotation.CustomService;

@CustomService("httpServletRequestArgumentResolver")
public class HttpServletRequestArgumentResolver implements ArgumentResolver {

	@Override
	public boolean support(Class<?> type, int paramIndex, Method method) {
		
		return ServletRequest.class.isAssignableFrom(type);//��ǰ���type�Ƿ���ʵ����ServletRequest������
	}

	//������ص���request��ֱ�ӷ���
	@Override
	public Object argumentResolver(HttpServletRequest request,
			HttpServletResponse response, int paramIndex, Method method) {
		// TODO Auto-generated method stub
		return request;
	}

}
