package com.presonal.jw.servlet;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.presonal.jw.annotation.CustomController;
import com.presonal.jw.annotation.CustomQualifier;
import com.presonal.jw.annotation.CustomRequestMapping;
import com.presonal.jw.annotation.CustomService;
import com.presonal.jw.tools.HandlerAdapterServiceImpl;

/*
 * �Զ���dispatcherServlet������ע�⡢����ע���
 */
public class DispatcherServlet extends HttpServlet  {
	
	//ɨ�赽�����е�����
	private List<String> classNames=new ArrayList<String>();
	//�������bean������
	private Map<String,Object> beans=new HashMap<String,Object>();
	//������еķ���������
	private Map<String,Object> methodMap=new HashMap<String,Object>();
	private static final long serialVersionUID = 1L;
	public DispatcherServlet(){}
	
	//init ������ִ��ʱ����ʵ�� servlet �������йأ�
	//���Կ���web.xml��load-on-startup��㣬�������ֵ���ڵ��� 0��
	//���� Servlet ʵ������ʱ��ִ�У����ʱ���ɾ����ֵ������ֵԽ����Խ��ִ�С����С�� 0 ����û�����ã����ڵ�һ�������ʱ���ͬ��ִ�� �� 
	//ע�� init ����ִֻ��һ��
	public void init(ServletConfig config){
		//1.ɨ�����еİ��µ�class�ļ�
		doScanPackage("com.personal.jw");
		classNames.forEach(n->System.out.println(n));
		//2.�����е�ɨ���������ʵ����
		doInstance();
		//3.����ע�룬��service���ʵ��ע�뵽controller
		ioc();
		//4.����һ��url path��method��ӳ���ϵ
		handlerMapping();
		//����һ�·�������������
		methodMap.forEach((k,v)->System.out.println(k+":"+v));
		
	}
	//���ʡ���ˣ���post�ķ���
	protected void doGet(HttpServletRequest request,HttpServletResponse response) throws ServletException, IOException{
		this.doPost(request,response);
	}
	//��������Ҫ������ķ������
	protected void doPost(HttpServletRequest request,HttpServletResponse response){
		//��ȡ����·��(/java-springmvc/demo/select)
		String uri=request.getRequestURI();
		//��ȡ��/java-springmvc(��Ŀ��)
		String context=request.getContextPath();
		//��ȡ��������·�����������method��key
		String path=uri.replace(context, "");
		//�õ�����
		Method method=(Method) methodMap.get(path);
		//�õ�������(�����һ��)
		Object instance=beans.get("/"+path.split("/")[1]);
		
		//�ò���ģʽ�õ�������ע��
		HandlerAdapterServiceImpl hanlerAdapterAserviceImpl=(HandlerAdapterServiceImpl)beans.get("hanlerAdapterAserviceImpl");
		
		//�õ�����
		Object[] args=hanlerAdapterAserviceImpl.hand(request, response, method, beans);
		
		try {
			/*
			 * invoke()����ִ��ĳ���Ķ����Ŀ�귽������ǰд�����õ�����ʱ�����ǻ�ȡ�Ȼ�ȡMethod��Ȼ�����Ӧ��Classʵ������ִ�з���
			 */
			method.invoke(instance, args);
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	private void handlerMapping() {
		if(beans.entrySet().size()<0){
			System.out.println("bean ����Ϊ�գ�û��ʵ������bean");
			return;
		}
		beans.forEach((k,v)->{
			Object instance=v;
			Class<?> clazz=v.getClass();
			if(clazz.isAnnotationPresent(CustomController.class)){
				CustomRequestMapping controllerMapping=clazz.getAnnotation(CustomRequestMapping.class);
				//�õ�controller�ϵ�mapping��"/demo"��
				String classPath=controllerMapping.value();
				//�õ����µ����еķ���
				Method[] methods=clazz.getMethods();
				Arrays.asList(methods).forEach(method->{
					if(method.isAnnotationPresent(CustomController.class)){
						CustomRequestMapping methodMapping=method.getAnnotation(CustomRequestMapping.class);
						String path=methodMapping.value();
						methodMap.put(classPath+path, method);
					}
				});
			}
			
		});
		
		
	}

	private void ioc() {
		if(beans.entrySet().size()<0){
			System.out.println("bean ����Ϊ�գ�û��ʵ������bean");
			return;
		}
		//����ʵ������bean
		beans.forEach((k,v)->{
			Object instance=v;
			//��ȡ�࣬�����ж�������������Щע�⣨��ʵ����ȡ�ࣩ
			Class<?> clazz=instance.getClass();
			if(clazz.isAnnotationPresent(CustomController.class)){
				Field[] fields=clazz.getDeclaredFields();
				//�ж�filed�Ƿ��������Զ�װ���ע�⣬��@autowired������еĻ�����field��set����ע��
				Arrays.asList(fields).forEach(field->{
					if(field.isAnnotationPresent(CustomQualifier.class)){
						CustomQualifier customQualifier=field.getAnnotation(CustomQualifier.class);
						String value=customQualifier.value();
						//�ſ�Ȩ�ޣ�����Ҫ
						field.setAccessible(true);
						try {
							//�������е�beanע�뵽�����ʵ����field��ȥ
							field.set(instance,beans.get(value));
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				});
			}
		});
		
	}

	//ͨ������Ļ��ư����е���ʵ����
	public void doInstance(){
		if(classNames.size()<0){
			System.out.println("doScanPackage is failed.............");
			return;
		}
		//����ɨ���class�ļ�������Ҫʵ�������ࣨ����ע����ࣩ�����з��䴴��������ע��Ͳ���Ҫʵ������
		for(String className : classNames){
			String cn=className.replaceAll(".class", "");
			try {
				//������
				Class<?> clazz=Class.forName(cn);
				if(clazz.isAnnotationPresent(CustomController.class)){
					//����ûɶ��
					CustomController controller=clazz.getAnnotation(CustomController.class);
					//���÷���ʵ����bean
				    Object instance=clazz.newInstance();
				    
				    //�����controller�Ļ��Ͱ�mapping��ֵ��Ϊ��bean���������key
				    CustomRequestMapping requestMapping=clazz.getAnnotation(CustomRequestMapping.class);
				    String key=requestMapping.value();//demo
				    //��ʵ����֮���bean�ŵ�������
				    beans.put(key, instance);
				}else if(clazz.isAnnotationPresent(CustomService.class)){
					CustomService service=clazz.getAnnotation(CustomService.class);
					Object instance=clazz.newInstance();
					beans.put(service.value(), instance);
				}else{
					continue;
				}
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InstantiationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
	}
	
	private void doScanPackage(String basePackage) {

		//ɨ�����õ�·���µ����е���
		URL url=this.getClass().getClassLoader().getResource("/"+replaceTo(basePackage));
		String fileStr=url.getFile();
		File file=new File(fileStr);
		//�õ����е�com.presonal.jw�µ�Ŀ¼
		String[] filesStr=file.list();
		
		for(String path:filesStr){
			File filePath=new File(fileStr+path);
			//�ݹ����ɨ�裬�����·��������ɨ��
			if(filePath.isDirectory()){
				doScanPackage(basePackage+"."+path);
			}else{
				//�����class�ļ������List���ϣ�������bean��
				classNames.add(basePackage+"."+filePath.getName());
			}
		}
		
	}
	private String replaceTo(String basePackage){
		return basePackage.replace("\\.", "/");
	}
	

}
