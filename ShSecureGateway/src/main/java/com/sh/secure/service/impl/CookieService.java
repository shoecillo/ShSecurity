package com.sh.secure.service.impl;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.sh.auth.crypto.RSAKeysUtil;
import com.sh.auth.dto.DecodedCookie;

/**
 * Service for cookie operations
 * @author shoe011
 *
 */
@Service
public class CookieService {

	@Autowired
	private RSAKeysUtil rsaKeys;
	
	
	@Value("${keys.repo.cookie.expiration}")
	private String expire;
	
	/**
	 * Get expiration cookie in milliseconds
	 * @param req - HttpServletRequest
	 * @return Long
	 * @throws Exception
	 */
	public Long getExpirationTime(HttpServletRequest req) throws Exception
	{
		
		Cookie coo = rsaKeys.isCookiePresent(req);
		if(coo != null)
		{
			DecodedCookie mapa = rsaKeys.decodeCookie(coo);
			String s = mapa.getCreation();
			long creation = Long.parseLong(s);
			long exp = Long.parseLong(expire)*1000;
			return creation + exp;	
		}
		else
		{
			throw new Exception("An Error Ocurred");
		}
		
	}
	
	
	
}
