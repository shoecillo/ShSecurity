package com.sh.auth.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.sh.auth.dto.User;
import com.sh.auth.service.SignInService;

/**
 * SignIn RestController for add new users
 * @author shoe011
 *
 */
@RestController
public class AuthCtrl {

	@Autowired
	private SignInService service;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(AuthCtrl.class);
	
	/**
	 * Method for sign in a new user
	 * @param user
	 * @return String - operation result
	 * @throws Exception
	 */
	@RequestMapping(path="/signIn",method=RequestMethod.POST)
	public String signIn(@RequestBody User user) throws Exception
	{
		if(service.createUser(user))
		{
			LOGGER.info("User Signed in Correctly");
			return "Sucessfull";
		}
		else
		{
			LOGGER.info("Something were wrong creating user");
			return "error";
		}
			
		 
	}
	
}
