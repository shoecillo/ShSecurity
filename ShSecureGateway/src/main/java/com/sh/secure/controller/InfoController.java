package com.sh.secure.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sh.secure.service.impl.CookieService;


/**
 * SignIn RestController for add new users
 * @author shoe011
 *
 */
@RestController
public class InfoController {

	
	@Autowired
	private CookieService cookieServ;
	
	
	@RequestMapping("/exam")
	public String example(HttpServletRequest req,HttpServletResponse resp)
	{
		try {
			long res = cookieServ.getExpirationTime(req);
			return String.valueOf(res);
		} catch (Exception e) {
			e.printStackTrace();
			return e.getMessage();
		}
	}
	
}
