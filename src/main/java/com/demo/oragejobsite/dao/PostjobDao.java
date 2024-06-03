package com.demo.oragejobsite.dao;



import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;


import com.demo.oragejobsite.entity.PostJob;


@Repository
public interface PostjobDao extends MongoRepository<PostJob, String>{

	PostJob findByJobid(String jobid);

	List<PostJob> findByEmpid(String empid);

	List<PostJob> findByEmpidAndArchiveTrue(String empid);

	List<PostJob> findByArchiveTrue();

//	List<PostJob> findByApprovejob(boolean b);


//	List<PostJob> findByEmpidAndApprovejob(String empid, boolean b);
	Page<PostJob> findByEmpidAndApprovejob(String empid, boolean b, Pageable pageable);

//	@Query("{ 'empid': ?0, 'approvejob': ?1, 'jobtitle': { $regex: ?2, $options: 'i' }, 'locationjob': { $regex: ?3, $options: 'i' } }")
//	Page<PostJob> findByEmpidAndApprovejobAndJobTitleAndLocation(String empid, boolean approvejob, String jobTitleRegex, String locationRegex, Pageable pageable);
	@Query("{ 'empid': ?0, 'approvejob': ?1, 'jobtitle': { $regex: ?2, $options: 'i' }, 'locationjob': { $regex: ?3, $options: 'i' },'companyforthisjob': { $regex: ?4, $options: 'i' } }")
	Page<PostJob> findByEmpidAndApprovejobAndJobTitleAndLocationAndCompany(String empid, boolean approvejob, String jobTitleRegex, String locationRegex, String companyRegex, Pageable pageable);
	
	@Query("{ 'approvejob': ?0, 'jobtitle': { $regex: ?1, $options: 'i' }, 'locationjob': { $regex: ?2, $options: 'i' },'companyforthisjob': { $regex: ?3, $options: 'i' } }")
	Page<PostJob> findByApprovejobAndJobTitleAndLocationAndCompany(boolean approvejob, String jobTitleRegex, String locationRegex,String companyRegex, Pageable pageable);

	
	 @Query("{ 'approvejob': true, 'archive': false, $and: [ { 'jobtitle': { $regex: ?0, $options: 'i' } }, { 'locationjob': { $regex: ?1, $options: 'i' } } ] }")
	 Page<PostJob> findByJobTitleAndLocation(String jobTitleRegex, String locationRegex, Pageable pageable);

	List<PostJob> findByJobtitleContainingIgnoreCase(String title);

	List<PostJob> findByJobtitleContainingIgnoreCaseAndCompanyforthisjobContainingIgnoreCase(String title,
			String company);

	List<PostJob> findByCompanyforthisjobContainingIgnoreCase(String company);
	
	@Query("{ 'empid': ?0, 'approvejob': false }")
    Page<PostJob> findByEmpidAndApprovejobFalse(String empid, Pageable pageable);

    @Query("{ 'jobtitle': { $regex: ?0, $options: 'i' }, 'approvejob': false }")
    Page<PostJob> findByJobtitleRegexAndApprovejobFalse(String jobTitleRegex, Pageable pageable);

    @Query("{ 'empid': ?0, 'jobtitle': { $regex: ?1, $options: 'i' }, 'approvejob': false }")
    Page<PostJob> findByEmpidAndJobtitleRegexAndApprovejobFalse(String empid, String jobTitleRegex, Pageable pageable);

    @Query("{ 'approvejob': false }")
    Page<PostJob> findByApprovejobFalse(Pageable pageable);
    
    Page<PostJob> findByEmpid(String empid, boolean b, Pageable pageable);
    Page<PostJob> findByEmpid(String empid, Pageable pageable);
    @Query("{'approvejob': false}")
	Page<PostJob> findAllWithoutEmpid(boolean b,Pageable pageable);
    
    Page<PostJob> findByApprovejobTrueAndArchiveFalse(Pageable pageable);

    @Query("{ 'approvejob': true, 'archive': false, 'jobid': { $in: ?0 } }")
    Page<PostJob> findApprovedAndArchivedWithUserStatus(List<String> jobIds, Pageable pageable);
    
    @Query("{ 'approvejob': true, 'archive': false, 'jobtitle': { $regex: ?0, $options: 'i' }, 'locationjob': { $regex: ?1, $options: 'i' } }")
    Page<PostJob> findByApprovejobTrueAndArchiveFalseAndJobtitleContainingIgnoreCaseAndLocationjobContainingIgnoreCase(String jobTitle, String jobLocation, Pageable pageable);

    @Query("{ 'jobid': { $in: ?0 }, 'approvejob': true, 'archive': false, 'jobtitle': { $regex: ?1, $options: 'i' }, 'locationjob': { $regex: ?2, $options: 'i' } }")
    Page<PostJob> findApprovedAndArchivedWithUserStatusAndJobtitleAndLocationjob(List<String> jobIds, String jobTitle, String jobLocation, Pageable pageable);

    @Query("{ 'approvejob': true, 'archive': false, 'jobtitle': { $regex: ?0, $options: 'i' } }")
    Page<PostJob> findByApprovejobTrueAndArchiveFalseAndJobtitleContainingIgnoreCase(String jobTitle, Pageable pageable);

    @Query("{ 'approvejob': true, 'archive': false, 'locationjob': { $regex: ?0, $options: 'i' } }")
    Page<PostJob> findByApprovejobTrueAndArchiveFalseAndLocationjobContainingIgnoreCase(String locationjob, Pageable pageable);
    
    @Query("{ $and: [ { 'empName': { $regex: ?0, $options: 'i' } }, { 'jobtitle': { $regex: ?1, $options: 'i' } } ] }")
    Page<PostJob> findByEmpNameAndJobTitleIgnoreCase(String empName, String jobTitle, Pageable pageable);

	List<PostJob> findByEmpidAndApprovejob(String empid, boolean b);

	Page<PostJob> findByApprovejob(boolean b, Pageable pageable);
	Page<PostJob> findByJobtitleContainingIgnoreCaseAndCompanyforthisjobContainingIgnoreCase(String title,
			String company,Pageable pageable);
}

