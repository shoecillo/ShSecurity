package com.sh.secure.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
	
	private static final Logger LOGGER = LoggerFactory.getLogger(InfoController.class);
	
	@RequestMapping("/getSSOnfo")
	public String example(HttpServletRequest req,HttpServletResponse resp)
	{
		try {
			long res = cookieServ.getExpirationTime(req);
			return String.valueOf(res);
		} catch (Exception e) {
			LOGGER.error("Error: ",e);
			return e.getMessage();
		}
	}
	
}
