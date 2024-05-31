package com.demo.oragejobsite.controller;


import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.demo.oragejobsite.dao.ApplicantsCountDao;
import com.demo.oragejobsite.dao.PostjobDao;
import com.demo.oragejobsite.dao.SavedJobDao;
import com.demo.oragejobsite.entity.ApplicantsCount;
import com.demo.oragejobsite.entity.PostJob;
import com.demo.oragejobsite.entity.SavedJob;



@CrossOrigin(origins = "${myapp.url}")
@RestController
public class PostjobController {
	@Autowired
	private PostjobDao pjd;
	
	
	  @Autowired
	  private SavedJobDao savedJobServiceimpl;
	  @Autowired
	    private ApplicantsCountDao applicantsCountRepository;
	  
	  @CrossOrigin(origins = "${myapp.url}")
	  @PostMapping("/jobpostinsert")
	  public ResponseEntity<?> jobpostinsert(@RequestBody PostJob pj) {
	      try {
	          PostJob savedPostJob = pjd.save(pj);
	          System.out.println("checking the response "+ savedPostJob.getJobid());
	          String jobid = savedPostJob.getJobid();
	          return ResponseEntity.status(HttpStatus.CREATED).body(savedPostJob.getJobid());
	      } catch (DataAccessException e) {
	          e.printStackTrace();
	          return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	              .body(null); // Or you can return a ResponseEntity with HttpStatus and without a body
	      } catch (Exception e) {
	          e.printStackTrace();
	          return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	              .body(null); // Or you can return a ResponseEntity with HttpStatus and without a body
	      }
	  }
	  	
	  	@CrossOrigin(origins = "${myapp.url}")
	  	@DeleteMapping("/deleteJob/{jobId}")
	    public ResponseEntity<Object> deleteJob(@PathVariable String jobId) {
	        try {
	                       if (pjd.existsById(jobId)) {
	               
	                pjd.deleteById(jobId);
	                return ResponseEntity.status(HttpStatus.OK).body("Job with ID " + jobId + " has been deleted successfully.");
	            } else {
	               
	                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Job with ID " + jobId + " does not exist.");
	            }
	        } catch (DataAccessException e) {
	            // Handle database access exception
	            e.printStackTrace();
	            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Database error occurred: " + e.getMessage());
	        } catch (Exception e) {
	            // Handle other exceptions
	            e.printStackTrace();
	            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while processing your request: " + e.getMessage());
	        }
	    }
	  	
	  	@CrossOrigin(origins = "${myapp.url}")
	  	@GetMapping("/fetchjobpost")
	  	public ResponseEntity<Map<String, Object>> fetchjobpost(
	  	        @RequestParam(required = false) String empid,
	  	        @RequestParam(defaultValue = "0") int page,
	  	        @RequestParam(required = false) String searchJobTitle,
	  	        @RequestParam(required = false) String searchLocation,
	  	        @RequestParam(required = false) String searchCompany) {
	  	    try {
	  	        int pageSize = (empid != null && !empid.isEmpty()) ? 2 : 5;
	  	        Pageable pageable = PageRequest.of(page, pageSize);
	  	        String jobTitleRegex = searchJobTitle != null ? ".*" + Pattern.quote(searchJobTitle.toLowerCase(Locale.ENGLISH)) + ".*" : ".*";
	  	        String locationRegex = searchLocation != null ? ".*" + Pattern.quote(searchLocation.toLowerCase(Locale.ENGLISH)) + ".*" : ".*";
	  	        String companyRegex = searchCompany != null ? ".*" + Pattern.quote(searchCompany.toLowerCase(Locale.ENGLISH)) + ".*" : ".*";
 
	  	        Page<PostJob> jobPage;
	  	        if (empid != null && !empid.isEmpty()) {
	  	            jobPage = pjd.findByEmpidAndApprovejobAndJobTitleAndLocationAndCompany(empid, true, jobTitleRegex, locationRegex, companyRegex, pageable);
	  	        } else {
	  	            jobPage = pjd.findByApprovejobAndJobTitleAndLocationAndCompany(true, jobTitleRegex, locationRegex, companyRegex, pageable);
	  	        }
 
	  	        List<Map<String, Object>> jobPostsWithStatus = jobPage.stream().map(postJob -> {
	  	            Map<String, Object> jobPostMap = new HashMap<>();
	  	            jobPostMap.put("jobid", postJob.getJobid());
	  	            jobPostMap.put("empName", postJob.getEmpName());
	  	            jobPostMap.put("empEmail", postJob.getEmpEmail());
	  	            jobPostMap.put("jobtitle", postJob.getJobtitle());
	  	            jobPostMap.put("companyforthisjob", postJob.getCompanyforthisjob());
	  	            jobPostMap.put("numberofopening", postJob.getNumberofopening());
	  	            jobPostMap.put("locationjob", postJob.getLocationjob());
	  	            jobPostMap.put("jobtype", postJob.getJobtype());
	  	            jobPostMap.put("schedulejob", postJob.getSchedulejob());
	  	            jobPostMap.put("payjob", postJob.getPayjob());
	  	            jobPostMap.put("descriptiondata", postJob.getDescriptiondata());
	  	            jobPostMap.put("empid", postJob.getEmpid());
	  	            jobPostMap.put("archive", postJob.isArchive());
	  	            jobPostMap.put("approvejob", postJob.isApprovejob());
	  	            jobPostMap.put("experience", postJob.getExperience());
 
	  	            LocalDateTime sendTime = postJob.getSendTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
	  	            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
	  	            String formattedSendTime = sendTime.format(formatter);
	  	            jobPostMap.put("sendTime", formattedSendTime);
 
	  	            return jobPostMap;
	  	        }).collect(Collectors.toList());
 
	  	        // Fetching all job posts based on empid and approvejob status
	  	        List<PostJob> jobPosts;
	  	        if (empid != null && !empid.isEmpty()) {
	  	            jobPosts = pjd.findByEmpidAndApprovejob(empid, true, pageable).getContent();
	  	        } else {
	  	            jobPosts = pjd.findByApprovejob(true, pageable).getContent();
	  	        }
	  	        System.out.println("hello" + jobPosts);
 
	  	        // Update each job post with the applicants count
	  	        for (PostJob jobPost : jobPosts) {
	  	            int applicantsCount = getApplicantsCount(jobPost.getJobid(), empid);
	  	            jobPost.setApplicants(applicantsCount);
	  	        }
 
	  	        Map<String, Object> response = new HashMap<>();
	  	        response.put("jobPosts", jobPostsWithStatus);
	  	        response.put("currentPage", page);
	  	        response.put("totalItems", jobPage.getTotalElements());
	  	        response.put("totalPages", jobPage.getTotalPages());
	  	        response.put("allJobPosts", jobPosts);
 
	  	        return ResponseEntity.ok(response);
	  	    } catch (Exception e) {
	  	        e.printStackTrace();
	  	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
	  	    }
	  	}

	  
	    @CrossOrigin(origins = "${myapp.url}")
		  @GetMapping("/fetchjobpostadmin")
		  public ResponseEntity<?> fetchjobpostadmin(
				  	@RequestParam(required = false) String empid,
			        @RequestParam(defaultValue = "0") int page,
			        @RequestParam(defaultValue = "5") int size,
			        @RequestParam(required = false) String empName,
			        @RequestParam(required = false) String jobTitle) {
		      try {
		          Pageable pageable = PageRequest.of(page, size);
		          Page<PostJob> jobPostsPage;
		          
		          if(empName!=null && !empName.isEmpty() && jobTitle!=null && !jobTitle.isEmpty()){
		        	  jobPostsPage=pjd.findByEmpNameAndJobTitleIgnoreCase(empName, jobTitle, pageable);
		          }
		          else if(empName!=null && !empName.isEmpty()){
		        	  jobPostsPage=pjd.findByEmpNameAndJobTitleIgnoreCase(empName, "", pageable);
		          }
		          else if(jobTitle!=null && !jobTitle.isEmpty()){
		        	  jobPostsPage=pjd.findByEmpNameAndJobTitleIgnoreCase("", jobTitle, pageable);
		          }
		          else if (empid != null) {
		              jobPostsPage = pjd.findByEmpid(empid, pageable);
		          } else {
		              jobPostsPage = pjd.findAll(pageable);
		          }

		          for (PostJob jobPost : jobPostsPage) {
		              int applicantsCount = getApplicantsCount(jobPost.getJobid(), empid);
		              jobPost.setApplicants(applicantsCount);
		          }

		          Map<String, Object> response = new HashMap<>();
		          response.put("content", jobPostsPage.getContent());
		          response.put("totalItems", jobPostsPage.getTotalElements());
		          response.put("currentPage", jobPostsPage.getNumber());
		          response.put("totalPages", jobPostsPage.getTotalPages());

		          return ResponseEntity.ok(response);
		      } catch (Exception e) {
		          e.printStackTrace();
		          return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
		      }
		  }
	  
//	 
	  @CrossOrigin(origins = "${myapp.url}")
	  @GetMapping("/fetchdisapprovejobpost")
	  public ResponseEntity<Map<String, Object>> fetchDisapprovejobpostadmin(
	          @RequestParam(required = false) String empid,
	          @RequestParam(defaultValue = "0") int page,
	          @RequestParam(required = false) String searchJobTitle) {
	      try {
	    	  int pageSize = (empid!=null && !empid.isEmpty())?2:5; 
	          Pageable pageable = PageRequest.of(page, pageSize);
	          Page<PostJob> jobPage;
	          String jobTitleRegex =searchJobTitle != null ? ".*" + Pattern.quote(searchJobTitle.toLowerCase(Locale.ENGLISH)) + ".*" : ".*";

	          if (empid != null && !empid.isEmpty()) {
	              if (searchJobTitle != null && !searchJobTitle.isEmpty()) {
	                  
	                  jobPage = pjd.findByEmpidAndJobtitleRegexAndApprovejobFalse(empid, jobTitleRegex, pageable);
	              } else {
	                  jobPage = pjd.findByEmpidAndApprovejobFalse(empid, pageable);
	              }
	          } else {
	              if (searchJobTitle != null && !searchJobTitle.isEmpty()) {
	                  jobPage = pjd.findByJobtitleRegexAndApprovejobFalse(jobTitleRegex, pageable);
	              } else {
	                  jobPage = pjd.findByApprovejobFalse(pageable);
	              }
	          }

	          List<PostJob> jobPosts = jobPage.getContent();

	          for (PostJob jobPost : jobPosts) {
	              int applicantsCount = getApplicantsCount(jobPost.getJobid(), empid);
	              jobPost.setApplicants(applicantsCount);
	          }

	          Map<String, Object> response = new HashMap<>();
	          response.put("jobPosts", jobPosts);
	          response.put("currentPage", page);
	          response.put("totalItems", jobPage.getTotalElements());
	          response.put("totalPages", jobPage.getTotalPages());

	          return ResponseEntity.ok(response);
	      } catch (Exception e) {
	          e.printStackTrace();
	          return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
	      }
	  }


	    // Helper method to fetch the count of applicants based on jobid and empid
	    private int getApplicantsCount(String jobid, String empid) {
	        try {
	            ApplicantsCount applicantsCount = applicantsCountRepository.findByJobidAndEmpid(jobid, empid);
	            System.out.print(applicantsCount);
	            return (applicantsCount != null) ? applicantsCount.getApplicants() : 0;
	        } catch (Exception e) {
	            e.printStackTrace();
	            return 0;
	        }
	    }	
	

	    @GetMapping("/fetchjobpoststatus")
	    public ResponseEntity<Map<String, Object>> fetchjobpoststatus(
	            @RequestParam(required = false) String uid,
	            @RequestParam(defaultValue = "0") int page,
	            @RequestParam(defaultValue = "5") int size,
	            @RequestParam(required = false) String searchJobTitle,
	            @RequestParam(required = false) String searchLocation) {
	        try {
	            Pageable pageable = PageRequest.of(page, size);
	            String jobTitleRegex = searchJobTitle != null ? ".*" + Pattern.quote(searchJobTitle.toLowerCase(Locale.ENGLISH)) + ".*" : ".*";
	            String locationRegex = searchLocation != null ? ".*" + Pattern.quote(searchLocation) + ".*" : ".*";
 
	            Page<PostJob> jobPage = pjd.findByJobTitleAndLocation(jobTitleRegex, locationRegex, pageable);
 
	            List<Map<String, Object>> jobPostsWithStatus = jobPage.stream()
	                .filter(postJob -> !postJob.isArchive() && postJob.isApprovejob())
	                .map(postJob -> {
	                    Map<String, Object> jobPostMap = new HashMap<>();
	                    jobPostMap.put("jobid", postJob.getJobid());
	                    jobPostMap.put("empName", postJob.getEmpName());
	                    jobPostMap.put("empEmail", postJob.getEmpEmail());
	                    jobPostMap.put("jobtitle", postJob.getJobtitle());
	                    jobPostMap.put("companyforthisjob", postJob.getCompanyforthisjob());
	                    jobPostMap.put("numberofopening", postJob.getNumberofopening());
	                    jobPostMap.put("locationjob", postJob.getLocationjob());
	                    jobPostMap.put("jobtype", postJob.getJobtype());
	                    jobPostMap.put("schedulejob", postJob.getSchedulejob());
	                    jobPostMap.put("payjob", postJob.getPayjob());
	                    jobPostMap.put("descriptiondata", postJob.getDescriptiondata());
	                    jobPostMap.put("empid", postJob.getEmpid());
	                    jobPostMap.put("archive", postJob.isArchive());
	                    jobPostMap.put("approvejob", postJob.isApprovejob());
	                    jobPostMap.put("experience", postJob.getExperience());
 
	                    LocalDateTime sendTime = postJob.getSendTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
	                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
	                    String formattedSendTime = sendTime.format(formatter);
	                    jobPostMap.put("sendTime", formattedSendTime);
 
	                    if (uid != null) {
	                        SavedJob savedJob = savedJobServiceimpl.findByJobidAndUid(postJob.getJobid(), uid);
	                        boolean saveStatus = (savedJob != null) && savedJob.getSaveStatus();
	                        jobPostMap.put("saveStatus", saveStatus);
	                    }
 
	                    return jobPostMap;
	                }).collect(Collectors.toList());
 
	            Map<String, Object> response = new HashMap<>();
	            response.put("jobPosts", jobPostsWithStatus);
	            response.put("currentPage", page);
	            response.put("totalItems", jobPage.getTotalElements());
	            response.put("totalPages", jobPage.getTotalPages());
 
	            return ResponseEntity.ok(response);
	        } catch (Exception e) {
	            e.printStackTrace();
	            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
	        }
	    }

	    
	    @GetMapping("/fetchjobpoststatuscheck")
	    public ResponseEntity<Map<String, Object>> fetchjobpoststatuscheck(
	            @RequestParam(required = false) String uid,
	            @RequestParam(defaultValue = "0") int page,
	            @RequestParam(defaultValue = "5") int size,
	            @RequestParam(required = false) String jobTitle,
	            @RequestParam(required = false) String jobLocation) {
	        try {
	            Pageable pageable = PageRequest.of(page, size);
	            Page<PostJob> applyJobsPage;

	            if (uid != null) {
	                // Get jobIds saved by the user
	                List<SavedJob> savedJobs = savedJobServiceimpl.findByUidAndSaveStatusTrue(uid);
	                List<String> jobIds = savedJobs.stream().map(SavedJob::getJobid).collect(Collectors.toList());

	                if (jobTitle != null && jobLocation != null) {
	                    applyJobsPage = pjd.findApprovedAndArchivedWithUserStatusAndJobtitleAndLocationjob(jobIds, jobTitle, jobLocation, pageable);
	                } else if (jobTitle != null) {
	                    applyJobsPage = pjd.findApprovedAndArchivedWithUserStatusAndJobtitleAndLocationjob(jobIds, jobTitle, "", pageable);
	                } else if (jobLocation != null) {
	                    applyJobsPage = pjd.findApprovedAndArchivedWithUserStatusAndJobtitleAndLocationjob(jobIds, "", jobLocation, pageable);
	                } else {
	                    applyJobsPage = pjd.findApprovedAndArchivedWithUserStatus(jobIds, pageable);
	                }
	            } else {
	                if (jobTitle != null && jobLocation != null) {
	                    applyJobsPage = pjd.findByApprovejobTrueAndArchiveFalseAndJobtitleContainingIgnoreCaseAndLocationjobContainingIgnoreCase(jobTitle, jobLocation, pageable);
	                } else if (jobTitle != null) {
	                    applyJobsPage = pjd.findByApprovejobTrueAndArchiveFalseAndJobtitleContainingIgnoreCase(jobTitle, pageable);
	                } else if (jobLocation != null) {
	                    applyJobsPage = pjd.findByApprovejobTrueAndArchiveFalseAndLocationjobContainingIgnoreCase(jobLocation, pageable);
	                } else {
	                    applyJobsPage = pjd.findByApprovejobTrueAndArchiveFalse(pageable);
	                }
	            }

	            List<Map<String, Object>> jobPostsWithStatus = applyJobsPage.getContent().stream()
	                    .filter(postJob -> !postJob.isArchive() && postJob.isApprovejob()) // Check for approved and not archived
	                    .map(postJob -> {
	                        if (uid != null) {
	                            SavedJob savedJob = savedJobServiceimpl.findByJobidAndUid(postJob.getJobid(), uid);
	                            boolean saveStatus = (savedJob != null) && savedJob.getSaveStatus();
	                            if (!saveStatus) {
	                                return null; // Skip this job post if saveStatus is false
	                            }
	                        }

	                        Map<String, Object> jobPostMap = new HashMap<>();
	                        jobPostMap.put("jobid", postJob.getJobid());
	                        jobPostMap.put("empName", postJob.getEmpName());
	                        jobPostMap.put("empEmail", postJob.getEmpEmail());
	                        jobPostMap.put("jobtitle", postJob.getJobtitle());
	                        jobPostMap.put("companyforthisjob", postJob.getCompanyforthisjob());
	                        jobPostMap.put("numberofopening", postJob.getNumberofopening());
	                        jobPostMap.put("locationjob", postJob.getLocationjob());
	                        jobPostMap.put("jobtype", postJob.getJobtype());
	                        jobPostMap.put("schedulejob", postJob.getSchedulejob());
	                        jobPostMap.put("payjob", postJob.getPayjob());
	                        jobPostMap.put("descriptiondata", postJob.getDescriptiondata());
	                        jobPostMap.put("empid", postJob.getEmpid());
	                        jobPostMap.put("archive", postJob.isArchive());
	                        jobPostMap.put("approvejob", postJob.isApprovejob());
	                        jobPostMap.put("experience", postJob.getExperience());

	                        LocalDateTime sendTime = postJob.getSendTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
	                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
	                        String formattedSendTime = sendTime.format(formatter);
	                        jobPostMap.put("sendTime", formattedSendTime);

	                        if (uid != null) {
	                            jobPostMap.put("saveStatus", true);
	                        }

	                        return jobPostMap;
	                    })
	                    .collect(Collectors.toList());

	            Map<String, Object> response = new HashMap<>();
	            response.put("jobPosts", jobPostsWithStatus);
	            response.put("currentPage", applyJobsPage.getNumber());
	            response.put("totalItems", applyJobsPage.getTotalElements());
	            response.put("totalPages", applyJobsPage.getTotalPages());

	            return ResponseEntity.ok(response);
	        } catch (Exception e) {
	            e.printStackTrace();
	            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
	        }
	    }

	
	    
	@CrossOrigin(origins = "${myapp.url}")
	@GetMapping("/fetchJobPostById/{jobId}")
	public ResponseEntity<PostJob> fetchJobPostById(@PathVariable String jobId) {
	    try {
	        Optional<PostJob> jobPost = pjd.findById(jobId);
	        if (jobPost.isPresent()) {
	        	  PostJob jobPostdata = jobPost.get();
	              System.out.println("Hello"+jobPostdata);
	              if (jobPostdata.isArchive()) {
	            	  System.out.println("Hello"+jobPostdata.isArchive());
	            	  System.out.println("Hello dfghdrthdfjh"+jobPostdata.getEmpEmail());
	                  return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null); // Skip archived job posts
	              }
	            return ResponseEntity.ok(jobPost.get());
	        } else {
	            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
	        }
	    } catch (Exception e) {
	        e.printStackTrace();
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
	    }
	}

	
	@GetMapping("/fetchArchivedJobPosts")
	public ResponseEntity<List<PostJob>> fetchArchivedJobPosts(@RequestParam(required = false) String empid) {
	    try {
	        List<PostJob> archivedJobPosts;
	        if (empid != null) {
	            archivedJobPosts = pjd.findByEmpidAndArchiveTrue(empid);
	        } else {
	            archivedJobPosts = pjd.findByArchiveTrue();
	        }
	        return ResponseEntity.ok(archivedJobPosts);
	    } catch (Exception e) {
	        e.printStackTrace();
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
	    }
	}
	
	
	 
	@CrossOrigin(origins = "${myapp.url}")
	@PutMapping("/jobpostupdate/{jobid}")
	public ResponseEntity<Object> jobpostupdate(@PathVariable String jobid, @RequestBody PostJob updatedJob) {
	    try {
	        Optional<PostJob> existingJobOptional = pjd.findById(jobid);
	        if (existingJobOptional.isPresent()) {
	            PostJob existingJob = existingJobOptional.get();

	            boolean currentApprovalStatus = existingJob.isApprovejob();

	            // Update the approvejob field based on its current value
	            existingJob.setApprovejob(!currentApprovalStatus);

	            // Get all fields of the PostJob class
	            Field[] fields = PostJob.class.getDeclaredFields();
	            for (Field field : fields) {
	                // Set field accessible to allow modification
	            	if (field.getName().equals("approvejob")) {
	                    continue;
	                }
	                field.setAccessible(true);

	                // Get the value of the field from the updatedJob object
	                Object value = field.get(updatedJob);

	                // If the value is not null, update the corresponding field in the existingJob object
	                if (value != null) {
	                    field.set(existingJob, value);
	                }
	            }

	            pjd.save(existingJob);
	            return ResponseEntity.status(HttpStatus.OK).body(existingJob);
	        } else {
	            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(false);
	        }
	    } catch (DataAccessException e) {
	        e.printStackTrace();
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Database error occurred: " + e.getMessage());
	    } catch (Exception e) {
	        e.printStackTrace();
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while processing your request: " + e.getMessage());
	    }
	}

@CrossOrigin(origins = "${myapp.url}")
    @PutMapping("/jobpostupdatedis/{jobid}")
	public ResponseEntity<Object> jobpostupdatedis(@PathVariable String jobid, @RequestBody PostJob updatedJob) {
	    try {
	        Optional<PostJob> existingJobOptional = pjd.findById(jobid);
	        if (existingJobOptional.isPresent()) {
	            PostJob existingJob = existingJobOptional.get();

	            // Get all fields of the PostJob class
	            Field[] fields = PostJob.class.getDeclaredFields();
	            for (Field field : fields) {
	                // Set field accessible to allow modification
	            	if (field.getName().equals("approvejob")) {
	                    continue;
	                }
	                field.setAccessible(true);

	                // Get the value of the field from the updatedJob object
	                Object value = field.get(updatedJob);

	                // If the value is not null, update the corresponding field in the existingJob object
	                if (value != null) {
	                    field.set(existingJob, value);
	                }
	            }

	            pjd.save(existingJob);
	            return ResponseEntity.status(HttpStatus.OK).body(existingJob);
	        } else {
	            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(false);
	        }
	    } catch (DataAccessException e) {
	        e.printStackTrace();
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Database error occurred: " + e.getMessage());
	    } catch (Exception e) {
	        e.printStackTrace();
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while processing your request: " + e.getMessage());
	    }
	}



	
	@CrossOrigin(origins = "${myapp.url}", methods = { RequestMethod.PUT })
	@PutMapping("/updateJobStatus/{jobid}")
	public ResponseEntity<Object> updateJobStatus(@PathVariable String jobid, @RequestBody PostJob updatedJob) {
	    try {
	        Optional<PostJob> existingJob = pjd.findById(jobid);
	        if (existingJob.isPresent()) {
	            PostJob currentJob = existingJob.get();
	      
	            pjd.save(currentJob);
	            return ResponseEntity.status(HttpStatus.OK).body(currentJob);
	        } else {
	            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(false);
	        }
	    } catch (DataAccessException e) {
	        e.printStackTrace();
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Database error occurred: " + e.getMessage());
	    } catch (Exception e) {
	        e.printStackTrace();
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while processing your request: " + e.getMessage());
	    }
	}
	
	
	
	
	@CrossOrigin(origins = "${myapp.url}")
	@GetMapping("/fetchJobByTitle")
	public ResponseEntity<?> fetchJobs(@RequestParam(required = false) String title, 
			@RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
			@RequestParam(required = false) String company) {
	    try {
	    	Pageable pageable = PageRequest.of(page, size);
            Page<PostJob> jobResults;
	        
	        if (title != null && company != null) {
	            jobResults = pjd.findByJobtitleContainingIgnoreCaseAndCompanyforthisjobContainingIgnoreCase(title, company,pageable);
	        } else if (title != null) {
//	            jobResults = pjd.findByJobtitleContainingIgnoreCase(title,pageable);
	        	jobResults = pjd.findByJobtitleContainingIgnoreCaseAndCompanyforthisjobContainingIgnoreCase(title, "",pageable);
	        } else if (company != null) {
//	            jobResults = pjd.findByCompanyforthisjobContainingIgnoreCase(company,pageable);
	        	jobResults = pjd.findByJobtitleContainingIgnoreCaseAndCompanyforthisjobContainingIgnoreCase("", company,pageable);
	        } else {
	            return ResponseEntity.badRequest().body(null); // Both title and company are null
	        }
	        Map<String , Object> response = new HashMap<>();
	        response.put("jobPosts", jobResults.getContent());
	        response.put("currentPage", jobResults.getNumber());
            response.put("totalItems", jobResults.getTotalElements());
            response.put("totalPages", jobResults.getTotalPages());
	        return ResponseEntity.ok(response);
	    } catch (Exception e) {
	        e.printStackTrace();
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
	    }
	}

	
	
//	@CrossOrigin(origins = "${myapp.url}")
//	@GetMapping("/fetchJobByTitle")
//	public ResponseEntity<List<PostJob>> fetchJobs(@RequestParam(required = false) String title, @RequestParam(required = false) String company) {
//	    try {
//	        List<PostJob> jobResults;
//	        
//	        if (title != null && company != null) {
//	            jobResults = pjd.findByJobtitleContainingIgnoreCaseAndCompanyforthisjobContainingIgnoreCase(title, company);
//	        } else if (title != null) {
//	            jobResults = pjd.findByJobtitleContainingIgnoreCase(title);
//	        } else if (company != null) {
//	            jobResults = pjd.findByCompanyforthisjobContainingIgnoreCase(company);
//	        } else {
//	            return ResponseEntity.badRequest().body(null); // Both title and company are null
//	        }
//	        
//	        return ResponseEntity.ok(jobResults);
//	    } catch (Exception e) {
//	        e.printStackTrace();
//	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
//	    }
//	}


}

