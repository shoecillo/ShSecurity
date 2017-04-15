package com.sh.auth.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.sh.auth.entity.UserEntity;


/**
 * MongoDb Repository for User collection
 * @author shoe011
 *
 */
public interface UsersRepository extends MongoRepository<UserEntity, String>  
{
	/**
	 * Find user entry by user
	 * @param user - String
	 * @return UserEntity
	 */
	public UserEntity findByUser(String user);
	
} 
