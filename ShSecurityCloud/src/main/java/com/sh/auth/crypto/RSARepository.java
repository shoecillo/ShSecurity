package com.sh.auth.crypto;

import java.io.IOException;
import java.util.List;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.sh.auth.dto.KeyInfo;

/**
 * Interface that represents the key repository implementation.<BR>
 * @author shoe011
 *
 */
public interface RSARepository {
	
	/**
	 * Writes new certificate in repository
	 * @param info - KeyInfo
	 * @throws JsonGenerationException
	 * @throws JsonMappingException
	 * @throws IOException
	 */
	public void writeCert(KeyInfo info) throws JsonGenerationException, JsonMappingException, IOException;
	
	/**
	 * Read all repository keys
	 * @return List&lt;KeyInfo&gt;
	 * @throws JsonParseException
	 * @throws JsonMappingException
	 * @throws IOException
	 */
	public List<KeyInfo> readKeys() throws JsonParseException, JsonMappingException, IOException;
	
	/**
	 * Read key based on user
	 * @param user - String
	 * @return KeyInfo
	 * @throws JsonParseException
	 * @throws JsonMappingException
	 * @throws IOException
	 */
	public KeyInfo readKeys(String user) throws JsonParseException, JsonMappingException, IOException;

}