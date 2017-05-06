package com.sh.auth.crypto;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;

import com.sh.auth.dto.DecodedCookie;
import com.sh.auth.dto.KeyInfo;

/**
 * Keystore Utilities.<BR>
 * Needs this properties:
 * <ul>
 * <li>keys.repo.cookie.expiration - cookie expiration in seconds</li>
 * <li>keys.repo.cookie.name - Cookie name</li>
 * <li>keys.repo.cookie.domain - Cookie domain</li>
 * <li>keys.repo.cookie.path - Cookie path</li>
 * <li>keys.keystore.signature.alg - Signature algorithm</li>
 * <li>keys.keystore.cipher.alg - cipher algorithm</li>
 * <li>keys.keystore.charset - Key charset</li>
 * </ul>
 * @author shoe011
 *
 */
public class RSAKeysUtil 
{

	private RSARepository repo;
	
	private String ksPath;
	
	private KeyStore KS = null;
	
	private String pwd;
	
	private String alias;
	
	@Value("${keys.repo.cookie.expiration}")
	private String expire;
	
	@Value("${keys.repo.cookie.name}")
	private String COOKIE_NAME;
	
	@Value("${keys.repo.cookie.domain}")
	private String cookieDomain;
	
	@Value("${keys.repo.cookie.path}")
	private String cookiePath;
	
	@Value("${keys.keystore.signature.alg}")
	private String SIGNATURE_ALG;
	
	@Value("${keys.keystore.cipher.alg}")
	private String CIPHER_ALG;
	
	@Value("${keys.keystore.charset}")
	private String CHARSET_ENC;
	
	private KeyPair keyPair = null;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(RSAKeysUtil.class);
	
	/**
	 * Constructor based on :
	  * Obtain from properties:
	 * <ul>
	 * <li>Keystore path (keys.keystore.path)</li>
	 * <li>Keystore password (keys.keystore.pwd)</li>
	 * <li>Keypair alias in keystore (keys.keystore.alias)</li>
	 * <li>Implementation of RSARepository (configuration bean)</li>
	 * </ul>
	 * @param ksPath
	 * @param pwd
	 * @param alias
	 * @param repo
	 */
	public RSAKeysUtil(String ksPath,String pwd, String alias,RSARepository repo) {
		
		try 
		{
			this.repo = repo;
			this.ksPath = ksPath;
			this.pwd=pwd;
			this.alias = alias;
			
			KS = KeyStore.getInstance("JKS");
			InputStream is = new FileInputStream(this.ksPath);
			KS.load(is, pwd.toCharArray());
			is.close();	
			getKS();
		}
		catch (FileNotFoundException e) {	
			LOGGER.error("Error: ",e);
		} catch (NoSuchAlgorithmException e) {
			
			LOGGER.error("Error: ",e);
		} catch (CertificateException e) {
			
			LOGGER.error("Error: ",e);
		} catch (IOException e) {
			
			LOGGER.error("Error: ",e);
		} catch (GeneralSecurityException e) {
			LOGGER.error("Error: ",e);
		}
		
	}
	/**
	 * Get KeyPair from JKS based on alias
	 * @return KeyPair
	 * @throws GeneralSecurityException
	 * @throws IOException
	 */
	private KeyPair getKS() throws GeneralSecurityException, IOException
	{
		 
		 Key priv = KS.getKey(alias, pwd.toCharArray());
		 if(priv instanceof PrivateKey)
		 {
			 Certificate cert = KS.getCertificate(alias);
			 PublicKey pub =  cert.getPublicKey();
			 keyPair = new KeyPair(pub, (PrivateKey) priv);
		 }
		 
		 return keyPair;
	}
	
	/**
	 * Sign data with JKS Keys
	 * @param content
	 * @return String - signed data encoded on Base64
	 * @throws NoSuchAlgorithmException
	 * @throws InvalidKeyException
	 * @throws SignatureException
	 */
	public String signContent(byte[] content) throws NoSuchAlgorithmException, InvalidKeyException, SignatureException
	{
		
		final Signature firma = Signature.getInstance(SIGNATURE_ALG);
		firma.initSign(keyPair.getPrivate());
		firma.update(content);
		final byte[] signed = firma.sign();
		LOGGER.debug("[SH-SEC]-Signed content");
		return Base64.encodeBase64String(signed);
		
	}
	/**
	 * Method for authenticate signed content
	 * @param datagram - signed content
	 * @param content - original data
	 * @return boolean - true or false if operation success or not
	 * @throws GeneralSecurityException
	 */
	public boolean authenticate(String datagram,String content) throws  GeneralSecurityException 
	{
		final Signature firma = Signature.getInstance(SIGNATURE_ALG);
		firma.initVerify(keyPair.getPublic());
		byte[] ct = Base64.decodeBase64(datagram.getBytes());
		firma.update(content.getBytes());
		if(firma.verify(ct))
		{
			LOGGER.debug("[SH-SEC]-User authenticated correctly");
			return true;
		}
		else
		{
			LOGGER.debug("[SH-SEC]-Wrong password");
			return false;
		}
		
	}
	/**
	 * Cipher based on KeyPair obtained from JKS
	 * @param text - text to cipher
	 * @return String - Base64 ciphered data
	 * @throws NoSuchAlgorithmException
	 * @throws NoSuchPaddingException
	 * @throws InvalidKeyException
	 * @throws IllegalBlockSizeException
	 * @throws BadPaddingException
	 * @throws UnsupportedEncodingException
	 */
	public String chipher(String text) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, UnsupportedEncodingException
	{
		Cipher encryptCipher = Cipher.getInstance(CIPHER_ALG);
		encryptCipher.init(Cipher.ENCRYPT_MODE, keyPair.getPublic());

	    byte[] cipherText = encryptCipher.doFinal(text.getBytes(CHARSET_ENC));
	    LOGGER.debug("[SH-SEC]-Text chipered");
	    return Base64.encodeBase64String(cipherText);
		
		
	}
	/**
	 * Decipher ciphered data 
	 * @param encVal - ciphered and Base64 encoded data
	 * @return Deciphered data
	 * @throws NoSuchAlgorithmException
	 * @throws NoSuchPaddingException
	 * @throws InvalidKeyException
	 * @throws UnsupportedEncodingException
	 * @throws IllegalBlockSizeException
	 * @throws BadPaddingException
	 */
	public String dechipher(String encVal) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, UnsupportedEncodingException, IllegalBlockSizeException, BadPaddingException
	{
		byte[] bytes = Base64.decodeBase64(encVal);
		Cipher decriptCipher = Cipher.getInstance(CIPHER_ALG);
	    decriptCipher.init(Cipher.DECRYPT_MODE, keyPair.getPrivate());
	    LOGGER.debug("[SH-SEC]-Text Deciphered");
	    return new String(decriptCipher.doFinal(bytes), CHARSET_ENC);
	}
	/**
	 * Validate the cookie info against Keystore and the expire time
	 * @param coo - Cookie
	 * @return boolean - true if is valid,false if not valid.
	 * @throws Exception
	 */
	public boolean validateCookie(Cookie coo) throws Exception
	{
		DecodedCookie mapa = decodeCookie(coo);
		KeyInfo k = repo.readKeys(mapa.getUser());
		final String chiph = dechipher(k.getChipher());
		
		long mills = (Integer.parseInt(expire))*1000L;
		long crea = Long.parseLong(mapa.getCreation());
		
		long res = crea + mills;
		Date d = new Date(res);
		SimpleDateFormat fmt = new SimpleDateFormat("YYYY-MM-DD hh:mm:ss");
		String txtDate = fmt.format(d);
		LOGGER.debug("[SH-SEC]-Cookie fo user "+k.getUser()+" Valid until "+txtDate);
		
		if(System.currentTimeMillis() < res)
		{
			return authenticate(mapa.getCert(), chiph);
		}
		else
		{
			coo.setMaxAge(0);
			return false;
		}
		
	}
	
	/**
	 * Decode the recived cookie and return DTO
	 * @param coo - Cookie
	 * @return DecodedCookie
	 * @throws Exception
	 */
	public DecodedCookie decodeCookie(Cookie coo) throws Exception
	{
		
		String raw = coo.getValue();
		String[] s = raw.split("#");
		if(s.length == 3)
		{
			String usu = new String(Base64.decodeBase64(s[0]), CHARSET_ENC);
			DecodedCookie res = new DecodedCookie();
			res.setUser(usu);
			res.setCert(s[1]);
			res.setCreation(s[2]);
			return res;
		}
		else
		{
			LOGGER.debug("[SH-SEC]-Malformed Cookie");
			throw new BadCredentialsException("Bad Cookie credentials");
		}
	}
	
	
	/**
	 * Create a new cookie with KeyInfo data
	 * @param key - KeyInfo
	 * @return Cookie
	 * @throws Exception
	 */
	public Cookie createCookie(KeyInfo key) throws Exception
	{

		Cookie cookie = new Cookie(COOKIE_NAME, Base64.encodeBase64String(key.getUser().getBytes())+"#"+key.getKey()+"#"+System.currentTimeMillis());
		cookie.setDomain(cookieDomain);
		cookie.setPath(cookiePath);
		cookie.setMaxAge(Integer.parseInt(expire));
		LOGGER.debug("[SH-SEC]-Cookie created");
		return cookie;
	}
	
	/**
	 * Check if cookie exists and return it.
	 * @param req - HttpServletRequest
	 * @return Cookie - Cookie if exists, null if not exists
	 */
	public Cookie isCookiePresent(HttpServletRequest req)
	{
		Cookie[] cookies = req.getCookies();
		if(cookies != null)
		{
			for(Cookie c : cookies)
			{
				if(COOKIE_NAME.equals(c.getName()))
				{
					return c;
				}
			}
		}
		LOGGER.debug("[SH-SEC]-No Cookie present");
		return null;
	}
	
	
}

