package com.sh.auth.dto;

/**
 * Represents a user used for signIn controller as parameter
 * @author shoe011
 *
 */
public class User {
	
	private String name;
	
	private String pwd;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPwd() {
		return pwd;
	}

	public void setPwd(String pwd) {
		this.pwd = pwd;
	}
	
	
	
}
