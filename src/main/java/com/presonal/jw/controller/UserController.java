package com.presonal.jw.controller;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.presonal.jw.annotation.CustomController;
import com.presonal.jw.annotation.CustomRequestMapping;
import com.presonal.jw.annotation.CustomQualifier;
import com.presonal.jw.annotation.CustomRequestParam;
import com.presonal.jw.service.impl.UserServiceImpl;

@CustomController
@CustomRequestMapping("/demo")
public class UserController {
	@CustomQualifier("userServiceImpl")
	private UserServiceImpl userServiceImpl;
	@CustomRequestMapping("/select")
	public void select(HttpServletRequest request,
			HttpServletResponse response,
			@CustomRequestParam("name") String name,
			@CustomRequestParam("age") String age){
		
		try {
			//response·µ»Ø
			response.getWriter().write(userServiceImpl.getUserByNameAndAge(name,age));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
