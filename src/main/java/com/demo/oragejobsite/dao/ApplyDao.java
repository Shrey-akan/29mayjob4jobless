package com.demo.oragejobsite.dao;



import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.demo.oragejobsite.entity.ApplyJob;

@Repository
public interface ApplyDao extends MongoRepository<ApplyJob, String>{
	 // Define a custom method to find an ApplyJob entity by juid
    ApplyJob findByJuid(String juid);

    List<ApplyJob> findByEmpidAndJobid(String empid, String jobid);

	List<ApplyJob> findByEmpid(String empid);

	List<ApplyJob> findByUid(String uid);

	ApplyJob findByJobidAndUid(String jobid, String uid);
	
	 Page<ApplyJob> findByUid(String uid, Pageable pageable);
	 
	 Page<ApplyJob> findByEmpid(String empid, Pageable pageable);
	 
	 Page<ApplyJob> findByEmpidAndJutitle(String empid, String jutitle, Pageable pageable);
	 
	 @Query("{ 'empid': ?0, 'jutitle': { $regex: ?1, $options: 'i' } }")
	 Page<ApplyJob> findByEmpidAndJutitleContainingIgnoreCase(String empid, String jutitle, Pageable pageable);
	 
//	 @Query("{ 'empid': ?0, 'jutitle': { $regex: ?1, $options: 'i' }, 'profileupdate': 'Waiting' }")
//	 Page<ApplyJob> findByEmpidAndJobStatusWaiting(String empid, String jobTitle, Pageable pageable);
	 
	 @Query("{ 'empid': ?0, 'profileupdate': 'Waiting' }")
	 Page<ApplyJob> findByEmpidAndProfileupdateWaiting(String empid, Pageable pageable);
	 
	 @Query("{ 'empid': ?0, 'profileupdate': { $regex: ?1, $options: 'i' } }")
	 Page<ApplyJob> findByEmpidAndProfileupdateIgnoreCase(String empid, String jobStatus, Pageable pageable);
	 
	 @Query("{ 'uid': ?0, 'jutitle': { $regex: ?1, $options: 'i' } }")
	 Page<ApplyJob> findByUidAndJutitleContainingIgnoreCase(String uid, String jutitle, Pageable pageable);
	 
	 @Query("{ 'uid': ?0, 'profileupdate': { $regex: ?1, $options: 'i' } }")
	 Page<ApplyJob> findByUidAndProfileupdateIgnoreCase(String uid, String jobStatus, Pageable pageable);

}

