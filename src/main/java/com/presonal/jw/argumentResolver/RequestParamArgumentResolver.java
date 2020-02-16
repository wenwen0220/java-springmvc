package com.presonal.jw.argumentResolver;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.presonal.jw.annotation.CustomRequestParam;
import com.presonal.jw.annotation.CustomService;

@CustomService("requestParamArgumentResolver")
public class RequestParamArgumentResolver implements ArgumentResolver {

	@Override
	public boolean support(Class<?> type, int paramIndex, Method method) {
		Annotation[][] an=method.getParameterAnnotations();
		Annotation[] paramsAns=an[paramIndex];
		for(Annotation paramsAn:paramsAns){
			if(CustomRequestParam.class.isAssignableFrom(paramsAn.getClass())){
				return true;
			}
		}
		return false;
	}

	@Override
	public Object argumentResolver(HttpServletRequest request,
			HttpServletResponse response, int paramIndex, Method method) {
		// TODO Auto-generated method stub
		Annotation[][] an=method.getParameterAnnotations();
		Annotation[] paramsAns=an[paramIndex];
		for(Annotation paramsAn:paramsAns){
			if(CustomRequestParam.class.isAssignableFrom(paramsAn.getClass())){
				CustomRequestParam rp=(CustomRequestParam) paramsAn;
				String value=rp.value();
				return request.getParameter(value);//这个value需要与name的相同
			}
		}
		return null;
	}

}
