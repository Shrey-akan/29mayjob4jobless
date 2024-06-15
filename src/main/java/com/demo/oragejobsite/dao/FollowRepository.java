package com.demo.oragejobsite.dao;

import org.springframework.data.mongodb.repository.MongoRepository;
import com.demo.oragejobsite.entity.Follow;
import java.util.List;

public interface FollowRepository extends MongoRepository<Follow, String> {
    List<Follow> findByUid(String uid);
    List<Follow> findByEmpid(String empid); // Updated to use empid
    List<Follow> findByUidAndEmpid(String uid, String empid); // Updated to use empid
}
