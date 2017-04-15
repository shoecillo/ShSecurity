package com.sh.secure.app;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoDbFactory;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import com.mongodb.MongoClient;

/**
 * MongoDB Spring-data configuration
 * @author shoe011
 *
 */
@Configuration
@EnableMongoRepositories("com.sh.auth.repository")
public class DBConfig {

	@Value("${mongo.db}")
	private String db;
	
	@Value("${mongo.host}")
	private String host;
	
	@Value("${mongo.port}")
	private String port;
	
	
	/**
	 * MongoDbFactory configuration
	 * @return MongoDbFactory
	 * @throws Exception
	 */
	public @Bean
	MongoDbFactory mongoDbFactory() throws Exception {
		
		return new SimpleMongoDbFactory(new MongoClient(host, Integer.parseInt(port)), db);
	}
	/**
	 * MongoTemplate configuration
	 * @return MongoTemplate
	 * @throws Exception
	 */
	public @Bean
	MongoTemplate mongoTemplate() throws Exception {

		MongoTemplate mongoTemplate = new MongoTemplate(mongoDbFactory());

		return mongoTemplate;

	}
	
}
