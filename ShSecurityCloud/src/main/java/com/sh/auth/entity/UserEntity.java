package com.sh.auth.entity;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
/**
 * Entity representation of user collection
 * @author shoe011
 *
 */
@Document(collection="keystore")
public class UserEntity 
{
	
	
	@Id
	public String id;
	
	public String user;
	
	public String key;
	
	public String chipher;
	
	public Long timestamp;

	public UserEntity() {
		
	}
	
	
	
	 public UserEntity(String user, String key, String chipher, Long timestamp) {
		super();
		this.user = user;
		this.key = key;
		this.chipher = chipher;
		this.timestamp = timestamp;
	}



	@Override
	    public String toString() {
			SimpleDateFormat frmt = new SimpleDateFormat("YYYY-MM-DD hh:mm:ss");
	        return String.format(
	                "Customer[id=%s, user='%s',key='%s', chipher='%s, creation=%s']",
	                id,user, key, chipher,frmt.format(new Date(timestamp)));
	    }
}
