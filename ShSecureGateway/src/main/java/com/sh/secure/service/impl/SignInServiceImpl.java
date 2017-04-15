package com.sh.secure.service.impl;

import org.springframework.beans.factory.annotation.Autowired;

import com.sh.auth.crypto.RSAKeysUtil;
import com.sh.auth.crypto.RSARepository;
import com.sh.auth.dto.KeyInfo;
import com.sh.auth.dto.User;
import com.sh.auth.service.SignInService;

/**
 * Implementation of SignInService
 * @author shoe011
 *
 */
public class SignInServiceImpl implements SignInService {

	@Autowired
	private RSAKeysUtil rsaKeys;
	
	private RSARepository repo;
	
	
	
	/**
	 * Constructor that need a implementation of RSARepository
	 * @param repo - RSARepository
	 */
	public SignInServiceImpl(RSARepository repo) {
		
		this.repo = repo;
	}
	
	@Override
	public boolean createUser(User user) 
	{
		
		try
		{
			String cert = rsaKeys.signContent(user.getPwd().getBytes());
			KeyInfo key = new KeyInfo();
			key.setUser(user.getName());
			key.setKey(cert);
			key.setTimestamp(System.currentTimeMillis());
			key.setChipher(rsaKeys.chipher(user.getPwd()));
			repo.writeCert(key);
			return true;
		}
		catch(Exception e)
		{
			return false;
		}
	}
	
}
