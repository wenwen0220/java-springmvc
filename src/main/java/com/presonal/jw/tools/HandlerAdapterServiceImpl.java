package com.presonal.jw.tools;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.presonal.jw.annotation.CustomRequestParam;
import com.presonal.jw.annotation.CustomService;
import com.presonal.jw.argumentResolver.ArgumentResolver;

@CustomService("handlerAdapterServiceImpl")
public class HandlerAdapterServiceImpl implements HandlerAdapterService {

	@Override
	public Object[] hand(HttpServletRequest request,
			HttpServletResponse response, Method method,
			Map<String, Object> beans) {
		// TODO Auto-generated method stub
		Class<?>[] params= method.getParameterTypes();
		
		Object[] args=new Object[params.length];
		
		Map<String,Object> argResolvers=getArgResolvers(beans,ArgumentResolver.class);
		
		int index=0;
		int i=0;
		for(Class<?> param:params){
			//�ò���ģʽ���õ��ĸ�������Ӧ�ĸ�������
//			argResolvers.forEach((k,v)->{
//				ArgumentResolver argumentResolver=(ArgumentResolver) v;
//				if(argumentResolver.support(param,index,method)){
//					args[i++]=argumentResolver.argumentResolver(request, response, index, method);
//				}
//			});
			for(Map.Entry<String, Object> entry:argResolvers.entrySet()){
				ArgumentResolver argumentResolver=(ArgumentResolver) entry.getValue();
				if(argumentResolver.support(param,index,method)){
					args[i++]=argumentResolver.argumentResolver(request, response, index, method);
				}
			}
			index++;
		}
		
		return args;
	}
	
	//�����beans��Ľ���param��ע���ó����ŵ��µ�map��
	public Map<String,Object> getArgResolvers(Map<String,Object> beans,Class<?> type){
		Map<String,Object> argResolvers=new HashMap<String,Object>();
		beans.forEach((k,v)->{
			Class<?> [] infs=v.getClass().getInterfaces();
			if(infs!=null && infs.length>0){
				for(Class<?> inf:infs){
					if(inf.isAssignableFrom(type)){
						argResolvers.put(k, v);
					}
				}
			}
		});
		return argResolvers;
	}

}
