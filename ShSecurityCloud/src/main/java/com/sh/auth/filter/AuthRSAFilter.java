package com.sh.auth.filter;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;

import com.sh.auth.crypto.RSAKeysUtil;

/**
 * Spring filter for check security in each request.<BR>
 * Need 2 properties for configure it:
 * <ul>
 * <li>keys.filter.permit.regex : whitelist regex for dont filter resources</li>
 * <li>keys.filter.redirect.path : path to redirect when invalid authentication</li>
 * </ul>
 * @author shoe011
 *
 */
@Component
public class AuthRSAFilter extends GenericFilterBean {
	
	@Autowired
	private RSAKeysUtil keys;
	
	@Value("${keys.filter.permit.regex}")
	private String regEx;
	
	@Value("${keys.filter.redirect.path}")
	private String redirectPath;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(AuthRSAFilter.class);

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException 
	{
		
		HttpServletRequest req =(HttpServletRequest) request;
		HttpServletResponse resp = (HttpServletResponse) response;
		
		Pattern pat = Pattern.compile(regEx);
		Matcher match = pat.matcher(req.getRequestURI());
		
		if(!match.matches())
		{
			LOGGER.debug("[SH-SEC]-REQUESTED URL: "+req.getRequestURI());
			try 
			{
				Cookie coo = keys.isCookiePresent(req);
				if(coo != null)
				{
					boolean isValid = keys.validateCookie(coo);
					if(!isValid)
					{
						resp.setStatus(HttpServletResponse.SC_MOVED_PERMANENTLY);
						resp.setHeader("Location", redirectPath);
					}
				}
				else
				{
					resp.setStatus(HttpServletResponse.SC_MOVED_PERMANENTLY);
					resp.setHeader("Location", redirectPath);
				}
				
			} 
			catch (Exception e) {
				
				LOGGER.error("Error: ",e);
			}
		}

		chain.doFilter(req, resp);
		
	}
	
	
	
	
	
}
