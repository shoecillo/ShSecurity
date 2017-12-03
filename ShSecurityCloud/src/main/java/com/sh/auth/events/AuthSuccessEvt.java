package com.sh.auth.events;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.stereotype.Component;

import com.sh.auth.service.AttempsService;

@Component
public class AuthSuccessEvt implements ApplicationListener<AuthenticationSuccessEvent> 
{
	
	@Autowired
	private AttempsService srv;
	
	private final static Logger LOGGER = LoggerFactory.getLogger(AuthSuccessEvt.class);

	@Override
	public void onApplicationEvent(AuthenticationSuccessEvent evt) {
		
		LOGGER.debug(evt.getAuthentication().getName().concat(" Success"));
		Blacklist usu = srv.getUserStatus(evt.getAuthentication().getName());
		
		usu = srv.periodCheckLock(usu);
		
		if(!usu.isLocked())
		{
			usu.setLocked(false);
			usu.setAtt(0);
			usu.setTimestamp(null);
			srv.updateUserSuccess(usu);
		}
		else
		{
			evt.getAuthentication().setAuthenticated(false);
		}
		
	}
	
}
