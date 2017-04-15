package com.sh.secure.crypto.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.sh.auth.crypto.RSARepository;
import com.sh.auth.dto.KeyInfo;
import com.sh.auth.entity.UserEntity;
import com.sh.auth.repository.UsersRepository;

/**
 * RSARepository Implementation for a MongoDB based repository.
 * @author shoe011
 *
 */
public class RSARepositoryMongo implements RSARepository {
	
	@Autowired
	private UsersRepository dao;
	
	/**
	 * Create a new entry in users collection
	 */
	@Override
	public void writeCert(KeyInfo info) throws JsonGenerationException, JsonMappingException, IOException {
		
		UserEntity usu = new UserEntity(info.getUser(), info.getKey(), info.getChipher(), info.getTimestamp());
		dao.save(usu);
		
	}
	
	/**
	 * Read all keys from MongoDB repository
	 */
	@Override
	public List<KeyInfo> readKeys() throws JsonParseException, JsonMappingException, IOException {
		
		List<UserEntity> lsMongo = dao.findAll();
		List<KeyInfo> ls = new ArrayList<KeyInfo>();
		
		for(UserEntity ent : lsMongo )
		{
			KeyInfo info = new KeyInfo();
			info.setUser(ent.user);
			info.setKey(ent.key);
			info.setChipher(ent.chipher);
			info.setTimestamp(ent.timestamp);
			ls.add(info);
		}
		return ls;
	}
	
	/**
	 * Read a key based on user from MongoDB repository
	 */
	@Override
	public KeyInfo readKeys(String user) throws JsonParseException, JsonMappingException, IOException {
		
		UserEntity ent = dao.findByUser(user);
		KeyInfo info = new KeyInfo();
		info.setUser(ent.user);
		info.setKey(ent.key);
		info.setChipher(ent.chipher);
		info.setTimestamp(ent.timestamp);
		
		return info;
	}

}
