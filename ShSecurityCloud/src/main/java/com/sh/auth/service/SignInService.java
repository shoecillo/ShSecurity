package com.sh.auth.service;

import com.sh.auth.dto.User;

/**
 * Create a new user interface 
 * @author shoe011
 *
 */
public interface SignInService {
	
	/**
	 * Create a new user
	 * @param user - User
	 * @return boolean
	 */
	public boolean createUser(User user);

}