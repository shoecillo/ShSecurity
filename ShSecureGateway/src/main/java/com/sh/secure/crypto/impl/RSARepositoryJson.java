package com.sh.secure.crypto.impl;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sh.auth.crypto.RSARepository;
import com.sh.auth.dto.KeyInfo;

/**
 * RSARepository Implementation for a JSON file repository.
 * @author shoe011
 *
 */
public class RSARepositoryJson implements RSARepository {

	private  String REP_PATH;
	
	
	@Value("${keys.repo.cookie.expiration}")
	private String expire;

	public RSARepositoryJson(String rEP_PATH) {
		
		this.REP_PATH = rEP_PATH;
		
	}
	
	/**
	 * Writes Key entry into JSON file
	 */
	@Override
	public void writeCert(KeyInfo info) throws JsonGenerationException, JsonMappingException, IOException
	{
		ObjectMapper mapper = new ObjectMapper();
		File json = new File(REP_PATH);
		List<KeyInfo> lsKeys = new ArrayList<KeyInfo>();
		if(json.exists())
		{
			lsKeys = readKeys();
			
		}
		lsKeys.add(info);
		mapper.writeValue(json, lsKeys);
		
	}
	/**
	 * Read all keys in JSON file
	 */
	@Override
	public  List<KeyInfo> readKeys() throws JsonParseException, JsonMappingException, IOException
	{
		File json = new File(REP_PATH);
		List<KeyInfo> lsKeys = null;
		ObjectMapper mapper = new ObjectMapper();
		if(json.exists())
		{
			lsKeys = mapper.readValue(json, new TypeReference<List<KeyInfo>>() {});
		}
		return lsKeys;
	}
	
	/**
	 * Read a key based on user in JSON file
	 */
	@Override
	public  KeyInfo readKeys(String user) throws JsonParseException, JsonMappingException, IOException
	{
		File json = new File(REP_PATH);
		List<KeyInfo> lsKeys = null;
		ObjectMapper mapper = new ObjectMapper();
		KeyInfo res = null;
		if(json.exists())
		{
			lsKeys = mapper.readValue(json, new TypeReference<List<KeyInfo>>() {});
			for(KeyInfo k : lsKeys)
			{
				if(user.equals(k.getUser()))
				{
					res = k;
					break;
				}
			}
		}
		return res;
	}
	
}
