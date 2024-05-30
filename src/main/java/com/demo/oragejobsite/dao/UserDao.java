package com.demo.oragejobsite.dao;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import com.demo.oragejobsite.entity.User;
@Repository
public interface UserDao extends MongoRepository<User, String>{
	 Optional<User> findByUid(String uid);
	 
//	 @Query("{ 'userFirstName': { $regex: ?0, $options: 'i' } }")
//	 @Query("$or: [{'userFirstName': { $regex: ?0, $options: 'i' },{'userLastName': { $regex: ?0, $options: 'i' } }] }")
	 @Query("{ $or: [ { 'userFirstName': { $regex: ?0, $options: 'i' } }, { 'userLastName': { $regex: ?0, $options: 'i' } } ] }")
	    Page<User> findByUserFirstNameOrUserLastNameRegexIgnoreCase(String name, Pageable pageable);
	
	 
	User findByUserName(String userName);
	void save(Optional<User> user);
}


