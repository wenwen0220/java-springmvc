package com.presonal.jw.service.impl;

import com.presonal.jw.annotation.CustomService;
import com.presonal.jw.service.UserService;

@CustomService("userServiceImpl")
public class UserServiceImpl implements UserService {

	@Override
	public String getUserByNameAndAge(String name,String age) {
		// TODO Auto-generated method stub
		return name+" is "+age+" years old";
	}

}
