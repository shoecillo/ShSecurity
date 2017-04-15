package com.sh.auth.provider;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;

import com.sh.auth.crypto.RSAKeysUtil;
import com.sh.auth.crypto.RSARepository;
import com.sh.auth.dto.KeyInfo;
import com.sh.auth.dto.Role;

/**
 * Spring security custom provider for authenticate the user
 * @author shoe011
 *
 */
public class CustomSecProvider implements AuthenticationProvider  {
	
	@Autowired
	private RSAKeysUtil keys;
	
	@Autowired
	private HttpServletRequest req;
	
	@Autowired
	private HttpServletResponse resp;
	
	private RSARepository repo;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(CustomSecProvider.class);
	
	/**
	 * Constructor that need a RSARepository implementation
	 * @param repo - RSARepository
	 */
	public CustomSecProvider(RSARepository repo) {
		this.repo = repo;
	}
	/**
	 * Method for authenticate user in spring security when LogIn
	 */
	@Override
	public Authentication authenticate(Authentication auth) throws AuthenticationException 
	{
		
		final String pwd = (String) auth.getCredentials();
		final String user = auth.getName();
		
		LOGGER.debug("[SH-SEC]-Authentication URL: "+req.getRequestURI());
		
		Collection<? extends GrantedAuthority> authorities = buildAuthorities();
		
			
			try 
			{
				final List<KeyInfo> lsKeys = repo.readKeys();
				
				Cookie coo = keys.isCookiePresent(req);
				if(coo != null)
				{
					if(keys.validateCookie(coo))
					{
						LOGGER.debug("[SH-SEC]-COOKIE CREDENTIALS ACCEPTED");
						KeyInfo inf = repo.readKeys(keys.decodeCookie(coo).getUser());
						return new UsernamePasswordAuthenticationToken(inf.getUser(), inf.getChipher(), authorities);
					}
				}
				else
				{
					for(KeyInfo k : lsKeys)
					{
						if(k.getUser().equals(user))
						{
							if(keys.authenticate(k.getKey(), pwd))
							{
								resp.addCookie(keys.createCookie(k));
								LOGGER.debug("[SH-SEC]-LOGIN CREDENTIALS ACCEPTED, CREATE COOKIE");
								return new UsernamePasswordAuthenticationToken(user, pwd, authorities);
							}
							else
							{
								LOGGER.debug("[SH-SEC]-LOGIN CREDENTIALS REJECTED");
								throw new BadCredentialsException("Wrong credentials");
							}
						}
					}
				}
			} catch (Exception e) {
				LOGGER.error("Error: ",e);
				throw new BadCredentialsException("Wrong credentials");
			}
			return null;
	}
	
	/**
	 * Method for restrict authentication class to UsernamePasswordAuthenticationToken 
	 */
	@Override
	public boolean supports(Class<?> authentication) {
	
		return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
	}
	
	/**
	 * Build authorities with ROLE_USER grant
	 * @return List&lt;Role&gt;
	 */
	private List<Role> buildAuthorities()
	{
		 Role r = new Role();
         r.setName("ROLE_USER");
         List<Role> roles = new ArrayList<Role>();
         roles.add(r);
         return roles;
	}
	
	

}
