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
 * 自定义dispatcherServlet，解析注解、依赖注入等
 */
public class DispatcherServlet extends HttpServlet  {
	
	//扫描到的所有的类名
	private List<String> classNames=new ArrayList<String>();
	//存放所有bean的容器
	private Map<String,Object> beans=new HashMap<String,Object>();
	//存放所有的方法的容器
	private Map<String,Object> methodMap=new HashMap<String,Object>();
	private static final long serialVersionUID = 1L;
	public DispatcherServlet(){}
	
	//init 方法的执行时刻其实与 servlet 的配置有关，
	//可以看到web.xml的load-on-startup结点，如果结点的值大于等于 0，
	//则在 Servlet 实例化的时候执行，间隔时间由具体的值决定，值越大，则越迟执行。如果小于 0 或者没有配置，则在第一次请求的时候才同步执行 ， 
	//注意 init 方法只执行一次
	public void init(ServletConfig config){
		//1.扫描所有的包下的class文件
		doScanPackage("com.personal.jw");
		classNames.forEach(n->System.out.println(n));
		//2.把所有的扫描出来的类实例化
		doInstance();
		//3.依赖注入，把service层的实例注入到controller
		ioc();
		//4.建立一个url path与method的映射关系
		handlerMapping();
		//遍历一下方法的容器看看
		methodMap.forEach((k,v)->System.out.println(k+":"+v));
		
	}
	//这个省略了，调post的方法
	protected void doGet(HttpServletRequest request,HttpServletResponse response) throws ServletException, IOException{
		this.doPost(request,response);
	}
	//这里是主要的请求的访问入口
	protected void doPost(HttpServletRequest request,HttpServletResponse response){
		//获取请求路径(/java-springmvc/demo/select)
		String uri=request.getRequestURI();
		//获取到/java-springmvc(项目名)
		String context=request.getContextPath();
		//获取到了请求路径，这个就是method的key
		String path=uri.replace(context, "");
		//拿到方法
		Method method=(Method) methodMap.get(path);
		//拿到控制类(这个不一样)
		Object instance=beans.get("/"+path.split("/")[1]);
		
		//用策略模式拿到参数的注解
		HandlerAdapterServiceImpl hanlerAdapterAserviceImpl=(HandlerAdapterServiceImpl)beans.get("hanlerAdapterAserviceImpl");
		
		//拿到参数
		Object[] args=hanlerAdapterAserviceImpl.hand(request, response, method, beans);
		
		try {
			/*
			 * invoke()用来执行某个的对象的目标方法。以前写代码用到反射时，总是获取先获取Method，然后传入对应的Class实例对象执行方法
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
			System.out.println("bean 容器为空，没有实例化的bean");
			return;
		}
		beans.forEach((k,v)->{
			Object instance=v;
			Class<?> clazz=v.getClass();
			if(clazz.isAnnotationPresent(CustomController.class)){
				CustomRequestMapping controllerMapping=clazz.getAnnotation(CustomRequestMapping.class);
				//拿到controller上的mapping（"/demo"）
				String classPath=controllerMapping.value();
				//拿到类下的所有的方法
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
			System.out.println("bean 容器为空，没有实例化的bean");
			return;
		}
		//遍历实例化的bean
		beans.forEach((k,v)->{
			Object instance=v;
			//获取类，用来判断类里声明了哪些注解（用实例获取类）
			Class<?> clazz=instance.getClass();
			if(clazz.isAnnotationPresent(CustomController.class)){
				Field[] fields=clazz.getDeclaredFields();
				//判断filed是否声明了自动装配的注解，如@autowired，如果有的话调用field的set方法注入
				Arrays.asList(fields).forEach(field->{
					if(field.isAnnotationPresent(CustomQualifier.class)){
						CustomQualifier customQualifier=field.getAnnotation(CustomQualifier.class);
						String value=customQualifier.value();
						//放开权限，必须要
						field.setAccessible(true);
						try {
							//把容器中的bean注入到，这个实例的field中去
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

	//通过反射的机制把所有的类实例化
	public void doInstance(){
		if(classNames.size()<0){
			System.out.println("doScanPackage is failed.............");
			return;
		}
		//遍历扫描的class文件，将需要实例化的类（加了注解的类），进行反射创建对象（像注解就不需要实例化）
		for(String className : classNames){
			String cn=className.replaceAll(".class", "");
			try {
				//加载类
				Class<?> clazz=Class.forName(cn);
				if(clazz.isAnnotationPresent(CustomController.class)){
					//这里没啥用
					CustomController controller=clazz.getAnnotation(CustomController.class);
					//利用反射实例化bean
				    Object instance=clazz.newInstance();
				    
				    //如果是controller的话就把mapping的值作为改bean在容器里的key
				    CustomRequestMapping requestMapping=clazz.getAnnotation(CustomRequestMapping.class);
				    String key=requestMapping.value();//demo
				    //把实例化之后的bean放到容器里
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

		//扫描编译好的路径下的所有的类
		URL url=this.getClass().getClassLoader().getResource("/"+replaceTo(basePackage));
		String fileStr=url.getFile();
		File file=new File(fileStr);
		//拿到所有的com.presonal.jw下的目录
		String[] filesStr=file.list();
		
		for(String path:filesStr){
			File filePath=new File(fileStr+path);
			//递归调用扫描，如果是路径，继续扫描
			if(filePath.isDirectory()){
				doScanPackage(basePackage+"."+path);
			}else{
				//如果是class文件则假如List集合（待生成bean）
				classNames.add(basePackage+"."+filePath.getName());
			}
		}
		
	}
	private String replaceTo(String basePackage){
		return basePackage.replace("\\.", "/");
	}
	

}
