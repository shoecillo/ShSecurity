package com.sh.auth.dto;

/**
 * Represents a user-key entry
 * @author shoe011
 *
 */
public class KeyInfo {

	private String user;
	
	private String key;
	
	private String chipher;
	
	private Long timestamp;

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public Long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Long timestamp) {
		this.timestamp = timestamp;
	}

	public String getChipher() {
		return chipher;
	}

	public void setChipher(String chipher) {
		this.chipher = chipher;
	}
	
	
	
}
