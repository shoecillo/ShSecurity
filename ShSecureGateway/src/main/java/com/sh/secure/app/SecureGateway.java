package com.sh.secure.app;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import com.sh.auth.crypto.RSAKeysUtil;
import com.sh.auth.crypto.RSARepository;
import com.sh.auth.provider.CustomSecProvider;
import com.sh.auth.service.SignInService;
import com.sh.secure.crypto.impl.RSARepositoryJson;
import com.sh.secure.crypto.impl.RSARepositoryMongo;
import com.sh.secure.service.impl.SignInServiceImpl;

/**
 * 
 * @author shoe011
 * Custom security configuration based on JKS and cookie
 *
 */
@Configuration
@ComponentScan(basePackages={"com.sh.secure","com.sh.auth"})
@EnableEurekaClient
@EnableZuulProxy
@SpringBootApplication
public class SecureGateway {
	
	@Value("${keys.keystore.path}")
	private  String ksPath;
	
	
	@Value("${keys.keystore.pwd}")
	private String pwd;
	
	@Value("${keys.keystore.alias}")
	private String alias;
	
	@Value("${keys.repo.path}")
	private  String REP_PATH;
	
	@Value("${keys.repo.cookie.name}")
	private String COOKIE_NAME;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(SecureGateway.class);
	
	/**
	 * Main application class
	 * @param args
	 */
	public static void main(String[] args)
	{
		SpringApplication.run(SecureGateway.class, args);
		LOGGER.debug("APPLICATION STARTED");
	}
	
	/**
	 * Key repository based on JSON file
	 * @return RSARepository
	 */
	@Bean(name="RSAJsonRepository")
	public RSARepository configRepoJson()
	{
		return new RSARepositoryJson(REP_PATH);
	}
	
	/**
	 * Key repository based on MongoDB.
	 * Require MongoDb configuration***
	 * @return RSARepository
	 */
	@Bean(name="RSAMongoRepository")
	public RSARepository configRepoMongo()
	{
		return new RSARepositoryMongo();
	}
	
	/**
	 * KeyUtil configuration bean.<BR>
	 * This method configure the keystore utilities.<BR>
	 * Obtain from properties:
	 * <ul>
	 * <li>Keystore path (keys.keystore.path)</li>
	 * <li>Keystore password (keys.keystore.pwd)</li>
	 * <li>Keypair alias in keystore (keys.keystore.alias)</li>
	 * <li>Implementation of RSARepository (configuration bean)</li>
	 * </ul>
	 * @return RSAKeysUtil
	 */
	@Bean
	public RSAKeysUtil configKey()
	{
		return new RSAKeysUtil(ksPath, pwd, alias,configRepoJson());
	}
	
	/**
	 * Service for add a new user.<BR>
	 * Need a implementation of RSARepository
	 * @return SignInService
	 */
	@Bean
	public SignInService signInServ()
	{
		return new SignInServiceImpl(configRepoJson());
	}
	/**
	 * Custom AuthenticationProvider implementation for Spring security.<BR>
	 * Need a implementation of RSARepository
	 * @return CustomSecProvider
	 */
	@Bean
	public CustomSecProvider getSecurityProvider()
	{
		return new CustomSecProvider(configRepoJson());
	}
	
}
