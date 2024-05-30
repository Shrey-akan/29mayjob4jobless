
package com.demo.oragejobsite.dao;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import com.demo.oragejobsite.entity.Employer;


@Repository
public interface EmployerDao extends MongoRepository<Employer, String>{

	Employer findByEmpmailid(String empmailid);

	Optional<Employer> findByEmpid(String empid);
	
	@Query("{ $and: [ { $or: [ { 'empfname': { $regex: ?0, $options: 'i' } }, { 'emplname': { $regex: ?0, $options: 'i' } } ] }, { 'empcompany': { $regex: ?1, $options: 'i' } } ] }")
    Page<Employer> findByEmpNameAndCompanyName(String name, String companyName, Pageable pageable);
}
