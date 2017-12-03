package com.sh.auth.events;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.stereotype.Component;

import com.sh.auth.service.AttempsService;



@Component
public class AuthFailureEvt implements ApplicationListener<AuthenticationFailureBadCredentialsEvent>
{
	
	@Autowired
	private AttempsService srv;
	
	private final static Logger LOGGER = LoggerFactory.getLogger(AuthFailureEvt.class);
	
	@Override
	public void onApplicationEvent(AuthenticationFailureBadCredentialsEvent evt) {
		
		LOGGER.debug(evt.getAuthentication().getName().concat(" Fail"));
		Blacklist usu = srv.attempt(evt.getAuthentication().getName());
		if(usu != null && usu.getUser().equals(evt.getAuthentication().getName()))
		{
			if(usu.isLocked())
			{
				evt.getAuthentication().setAuthenticated(false);
			}
		}
		
	}
	
}
