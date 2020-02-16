package com.presonal.jw.argumentResolver;

import java.lang.reflect.Method;

import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.presonal.jw.annotation.CustomService;

@CustomService("httpServletResponseArgumentResolver")
public class HttpServletResponseArgumentResolver implements ArgumentResolver {

	@Override
	public boolean support(Class<?> type, int paramIndex, Method method) {
		
		return ServletResponse.class.isAssignableFrom(type);//��ǰ���type�Ƿ���ʵ����ServletResponse������
	}

	//������ص���request��ֱ�ӷ���
	@Override
	public Object argumentResolver(HttpServletRequest request,
			HttpServletResponse response, int paramIndex, Method method) {
		// TODO Auto-generated method stub
		return response;
	}

}
