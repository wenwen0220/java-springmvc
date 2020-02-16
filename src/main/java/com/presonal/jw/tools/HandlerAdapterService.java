package com.presonal.jw.tools;

import java.lang.reflect.Method;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface HandlerAdapterService {

	public Object[] hand(HttpServletRequest request,HttpServletResponse response,Method method,Map<String,Object> beans);
}
