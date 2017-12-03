package com.sh.auth.events;

import java.io.Serializable;

public class Blacklist implements Serializable {

	
	private static final long serialVersionUID = -2951903447185706612L;

	private String user;
	
	private Integer att = 0;
	
	private Long timestamp;
	
	
	
	private boolean locked = false;

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public Integer getAtt() {
		return att;
	}

	public void setAtt(Integer att) {
		this.att = att;
	}

	public boolean isLocked() {
		return locked;
	}

	public void setLocked(boolean locked) {
		this.locked = locked;
	}

	public Long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Long timestamp) {
		this.timestamp = timestamp;
	}
	
	
	
}
