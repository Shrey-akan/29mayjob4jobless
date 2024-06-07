package com.demo.oragejobsite.controller;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.demo.oragejobsite.dao.ApplicantsCountDao;
import com.demo.oragejobsite.dao.ApplyDao;
import com.demo.oragejobsite.dao.UserStatusDao;
import com.demo.oragejobsite.entity.ApplicantsCount;
import com.demo.oragejobsite.entity.ApplyJob;
import com.demo.oragejobsite.entity.UserStatus;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@CrossOrigin(origins = "${myapp.url}")
@RestController
public class ApplyController {
	@Autowired
	private ApplyDao apd;
	@Autowired
    private ApplicantsCountDao applicantsCountRepository;
	@Autowired
	private UserStatusDao userstatusdao;
	
	@Autowired
	private UserStatusDao userstatdao;
	@CrossOrigin(origins = "${myapp.url}")
    @PostMapping("/insertapplyjob")
	public ResponseEntity<?> insertapplyjob(@RequestBody ApplyJob applyjob) {
        try {
            // Check if the jobid and uid already exist
            ApplyJob existingApplyJob = apd.findByJobidAndUid(applyjob.getJobid(), applyjob.getUid());
            if (existingApplyJob != null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("You have already applied for this job.");
            }

            applyjob.setProfileupdate("Waiting");
            System.out.println("ApplyJob object before saving: " + applyjob.getProfileupdate());
            ApplyJob savedApplyJob = apd.save(applyjob);
            // Update ApplicantsCount based on jobid
            String jobid = applyjob.getJobid();
            // Rest of the code to update applicants count...
ApplicantsCount applicantsCount = getApplicantsCountByJobId(jobid);
            if (applicantsCount == null) {
                // If no entry exists for the jobid, create a new one
                applicantsCount = new ApplicantsCount();
                applicantsCount.setJobid(jobid);
                applicantsCount.setEmpid(applyjob.getEmpid());
                applicantsCount.setUid(applyjob.getUid());
                applicantsCount.setJuid(applyjob.getJuid());
                applicantsCount.setApplicants(1);
                System.out.println(applicantsCount.getApplicants()+" "+applicantsCount.getJobid());
            } else {
            int currentApplicants = applicantsCount.getApplicants();
            currentApplicants++;
            applicantsCount.setApplicants(currentApplicants);
            }
            applicantsCountRepository.save(applicantsCount);

            return ResponseEntity.status(HttpStatus.CREATED).body(savedApplyJob);
        } catch (DataAccessException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Database error occurred: " + e.getMessage());
        } catch (Exception e) {
            // Handle any other exceptions that may occur
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while processing your request: " + e.getMessage());
        }
    }


    private ApplicantsCount getApplicantsCountByJobId(String jobid) {
        return applicantsCountRepository.findByJobid(jobid);
    }
	
    @CrossOrigin(origins = "${myapp.url}")
    @GetMapping("/fetchapplyform")
    public ResponseEntity<?> fetchapplyform(
            @RequestParam(required = false) String uid,
            @RequestParam(required = false) String empid,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String jobTitle,
            @RequestParam(required = false) String jobStatus) {
        
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<ApplyJob> applyJobsPage;

            // Prioritize uid filter
            if (uid != null && !uid.isEmpty() && jobTitle != null && !jobTitle.isEmpty()) {
            	applyJobsPage = apd.findByUidAndJutitleContainingIgnoreCase(uid, jobTitle, pageable);
            }
            else if (uid != null && !uid.isEmpty() && jobStatus != null && !jobStatus.isEmpty()) {
            	applyJobsPage = apd.findByUidAndProfileupdateIgnoreCase(uid, jobStatus, pageable);
            }
            else if (uid != null && !uid.isEmpty()) {
                applyJobsPage = apd.findByUid(uid, pageable);
            }
            else if (empid != null && !empid.isEmpty() && jobTitle != null && !jobTitle.isEmpty()) {
//                applyJobsPage = apd.findByEmpidAndJutitle(empid, jobTitle, pageable);
                applyJobsPage = apd.findByEmpidAndJutitleContainingIgnoreCase(empid, jobTitle, pageable);
            }
            else if (empid != null && !empid.isEmpty() && jobStatus != null && !jobStatus.isEmpty()) {
//            	applyJobsPage = apd.findByEmpidAndProfileupdateWaiting(empid, pageable);
            	applyJobsPage = apd.findByEmpidAndProfileupdateIgnoreCase(empid,jobStatus, pageable);
            }
            else if (empid != null && !empid.isEmpty()) {
                applyJobsPage = apd.findByEmpid(empid, pageable);
            }
            else {
                applyJobsPage = apd.findAll(pageable);
            }

            Map<String, Object> response = new HashMap<>();
            response.put("jobPosts", applyJobsPage.getContent());
            response.put("currentPage", applyJobsPage.getNumber());
            response.put("totalItems", applyJobsPage.getTotalElements());
            response.put("totalPages", applyJobsPage.getTotalPages());

            return ResponseEntity.ok(response);
        } catch (DataAccessException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Database error occurred: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while processing your request: " + e.getMessage());
        }
    }


	@CrossOrigin(origins = "${myapp.url}")
	@GetMapping("/notificationforuser")
	public ResponseEntity<?> notificationforuser(@RequestParam(required = false) String uid) {
	    try {
	        List<ApplyJob> applyJobs;

	        if (uid != null && !uid.isEmpty()) {
	            applyJobs = apd.findByUid(uid);
	            System.out.println(applyJobs);
	            
	            List<UserStatus> userStatusList = userstatdao.findByUid(uid);
	            System.out.println(userStatusList);

	            Iterator<ApplyJob> iterator = applyJobs.iterator();
	            while (iterator.hasNext()) {
	                ApplyJob applyJob = iterator.next();
	                boolean foundMatchingUserStatus = false;
	                for (UserStatus userStatus : userStatusList) {
	                    if (uid.equals(userStatus.getUid()) &&
	                        applyJob.getUid().equals(userStatus.getUid()) &&
	                        applyJob.getJuid().equals(userStatus.getJuid()) &&  
	                        userStatus.getViewcheck() != null && 
	                        userStatus.getViewcheck()) {

	                        System.out.println(uid.equals(userStatus.getUid()));
	                        System.out.println(applyJob.getUid().equals(userStatus.getUid()));
	                        System.out.println(applyJob.getJuid().equals(userStatus.getJuid()));
	                        System.out.println(userStatus.getViewcheck());

	                        applyJob.setUserStatus(true);
	                        System.out.println("check");
	                        foundMatchingUserStatus = true;
	                        break;
	                    }
	                }

	                if (!foundMatchingUserStatus) {
	                    applyJob.setUserStatus(false);
	                }
	                if (applyJob.isNotifydelete()) {
	                    iterator.remove(); // Safely remove the element using iterator
	                }
	            }
	        } else {
	            applyJobs = apd.findAll();
	        }
	        return ResponseEntity.ok(applyJobs);
	    } catch (DataAccessException e) {
	        e.printStackTrace();
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	                .body("Database error occurred: " + e.getMessage());
	    } catch (Exception e) {
	        e.printStackTrace();
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	                .body("An error occurred while processing your request: " + e.getMessage());
	    }
	}
	@CrossOrigin(origins = "${myapp.url}")
	@PostMapping("/updateProfileUpdate")
	public ResponseEntity<?> updateProfileUpdate(@RequestBody ApplyJob applyJob) {
	    try {
	        ApplyJob existingApplyJob = apd.findByJuid(applyJob.getJuid());
	        if (existingApplyJob != null) {
	            existingApplyJob.setProfileupdate(applyJob.getProfileupdate());
	            existingApplyJob.setNotifydelete(false);
	            ApplyJob updatedApplyJob = apd.save(existingApplyJob);	            
	            String jobid = applyJob.getJobid();
	            UserStatus userstat = userstatusdao.findByJuid(applyJob.getJuid());
	            if(userstat == null) {
	            	  userstat = new UserStatus();
	                  userstat.setJuid(applyJob.getJuid());
	                  userstat.setJobid(applyJob.getJobid());
	                  userstat.setUid(existingApplyJob.getUid());
	                  userstat.setEmpid(existingApplyJob.getJobid());
	                  userstat.setApplystatus(applyJob.getProfileupdate());
	                  userstat.setViewcheck(true);
	                  userstatusdao.save(userstat);
	            	
	            }else {
	            	   userstat.setApplystatus(applyJob.getProfileupdate());
	            	   userstat.setViewcheck(true);
	                   userstatusdao.save(userstat);
	            }
	            ApplicantsCount applicantsCount = getApplicantsCountByJobId(jobid);
	            if (applicantsCount == null) {
	                return ResponseEntity.ok(updatedApplyJob);
	            } else {
	                int currentApplicants = applicantsCount.getApplicants();
	                if (currentApplicants > 0) {
	                    currentApplicants--;
	                    applicantsCount.setApplicants(currentApplicants);
	                    applicantsCountRepository.save(applicantsCount);
	                }
	            }
	            return ResponseEntity.ok(updatedApplyJob);
	        } else {
	            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("ApplyJob not found for UID: " + applyJob.getJuid());
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
	@GetMapping("/fetchapplyformbyjobid")
	public ResponseEntity<?> fetchApplyFormByJobId(
	        @RequestParam(name = "empid") String empid,
	        @RequestParam(name = "jobid") String jobid
	) {
	    try {
	        List<ApplyJob> applyJobs = apd.findByEmpidAndJobid(empid, jobid);
	        return ResponseEntity.ok(applyJobs);
	    } catch (DataAccessException e) {
	        e.printStackTrace();
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Database error occurred: " + e.getMessage());
	    } catch (Exception e) {
	        e.printStackTrace();
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while processing your request: " + e.getMessage());
	    }
	}

	

	@CrossOrigin(origins = "${myapp.url}")
	@GetMapping("/notifyEmployer")
	public ResponseEntity<?> notifyEmployer(@RequestParam(name = "empid") String empid) {
	    try {
	        // Fetch all apply jobs for the given employer
	        List<ApplyJob> applyJobs = apd.findByEmpid(empid);

	        // Group apply jobs by jobid and count waiting applications
	        Map<String, Long> jobidWaitingCountMap = applyJobs.stream()
	                .filter(applyJob -> "Waiting".equals(applyJob.getProfileupdate()))
	                .collect(Collectors.groupingBy(
	                        ApplyJob::getJobid,
	                        Collectors.counting()
	                ));
	        
	        List<ApplyJob> waitingApplications = applyJobs.stream()
	                .filter(applyJob -> "Waiting".equals(applyJob.getProfileupdate()))
	                .collect(Collectors.toList());

	        // Count the number of waiting applications
	        long waitingApplicationsCount = waitingApplications.size();

	        Map<String, Object> responseMap = new HashMap<>();
	        responseMap.put("jobidWaitingCountMap", jobidWaitingCountMap);
	        responseMap.put("waitingApplicationsCount", waitingApplicationsCount);

	        return ResponseEntity.ok(responseMap);
	    } catch (DataAccessException e) {
	        e.printStackTrace();
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Database error occurred: " + e.getMessage());
	    } catch (Exception e) {
	        e.printStackTrace();
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while processing your request: " + e.getMessage());
	    }
	}
	
//	@GetMapping("/fetchUniqueApplyJobByUid")
//    public ResponseEntity<?> fetchUniqueApplyJobByUid(@RequestParam String uid) {
//        try {
//            // Fetch ApplyJob entities by uid
//            List<ApplyJob> applyJobs = apd.findByUid(uid);
//
//
//            // Extract unique ApplyJob objects based on jucompny
//            Map<String, ApplyJob> uniqueApplyJobsMap = applyJobs.stream()
//                    .collect(Collectors.toMap(
//                            ApplyJob::getJucompny,
//                            applyJob -> applyJob,
//                            (existing, replacement) -> existing
//                    ));
//
//
//            List<ApplyJob> uniqueApplyJobs = uniqueApplyJobsMap.values().stream().collect(Collectors.toList());
//
//
//            return ResponseEntity.ok(uniqueApplyJobs);
//        } catch (DataAccessException e) {
//            e.printStackTrace();
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                    .body("Database error occurred: " + e.getMessage());
//        } catch (Exception e) {
//            e.printStackTrace();
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                    .body("An error occurred while processing your request: " + e.getMessage());
//        }
//    }
	
	
	
	@GetMapping("/fetchUniqueApplyJobByUid")
	public ResponseEntity<?> fetchUniqueApplyJobByUid(
	        @RequestParam String uid,
	        @RequestParam(defaultValue = "0") int page,
	        @RequestParam(defaultValue = "5") int size) {
	    try {
	        Pageable pageable = PageRequest.of(page, size);
 
            Page<ApplyJob> applyJobsPage = apd.findByUid(uid, pageable);
            
            Map<String, ApplyJob> uniqueApplyJobsMap = applyJobsPage.getContent().stream()
                    .collect(Collectors.toMap(
                            ApplyJob::getJucompny,
                            applyJob -> applyJob,
                            (existing, replacement) -> existing
                    ));
	        
	        List<ApplyJob> uniqueApplyJobs = uniqueApplyJobsMap.values().stream().collect(Collectors.toList());
 
            Page<ApplyJob> uniqueApplyJobPage = new PageImpl<>(uniqueApplyJobs, pageable, applyJobsPage.getTotalElements());
	        
	        Map<String, Object> response = new HashMap<>();
            response.put("jobPosts", uniqueApplyJobPage.getContent());
            response.put("currentPage", uniqueApplyJobPage.getNumber());
            response.put("totalPages", uniqueApplyJobPage.getTotalPages());
            response.put("totalItems", uniqueApplyJobPage.getTotalElements());
 
	        return ResponseEntity.ok(response);
	    } catch (DataAccessException e) {
	        e.printStackTrace();
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	                .body("Database error occurred: " + e.getMessage());
	    } catch (Exception e) {
	        e.printStackTrace();
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	                .body("An error occurred while processing your request: " + e.getMessage());
	    }
	}
//	@GetMapping("/fetchUniqueJunamesByEmpid")
//    public ResponseEntity<?> fetchUniqueJunamesByEmpid(@RequestParam String empid) {
//        try {
//            // Fetch ApplyJob entities by empid
//            List<ApplyJob> applyJobs = apd.findByEmpid(empid);
//
//
//            // Extract unique ApplyJob objects based on juname
//            Map<String, ApplyJob> uniqueApplyJobsMap = applyJobs.stream()
//                    .collect(Collectors.toMap(
//                            ApplyJob::getJuname,
//                            applyJob -> applyJob,
//                            (existing, replacement) -> existing
//                    ));
//
//
//            List<ApplyJob> uniqueApplyJobs = uniqueApplyJobsMap.values().stream().collect(Collectors.toList());
//
//
//            return ResponseEntity.ok(uniqueApplyJobs);
//        } catch (DataAccessException e) {
//            e.printStackTrace();
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                    .body("Database error occurred: " + e.getMessage());
//        } catch (Exception e) {
//            e.printStackTrace();
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                    .body("An error occurred while processing your request: " + e.getMessage());
//        }
//    }
	
	@GetMapping("/fetchUniqueJunamesByEmpid")
    public ResponseEntity<?> fetchUniqueJunamesByEmpid(
            @RequestParam String empid,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            Pageable pageable = PageRequest.of(page, size);
 
            Page<ApplyJob> applyJobPage = apd.findByEmpid(empid, pageable);
 
            Map<String, ApplyJob> uniqueApplyJobsMap = applyJobPage.getContent().stream()
                    .collect(Collectors.toMap(
                            ApplyJob::getJuname,
                            applyJob -> applyJob,
                            (existing, replacement) -> existing
                    ));
 
            List<ApplyJob> uniqueApplyJobs = uniqueApplyJobsMap.values().stream().collect(Collectors.toList());
 
            Page<ApplyJob> uniqueApplyJobPage = new PageImpl<>(uniqueApplyJobs, pageable, applyJobPage.getTotalElements());
 
            Map<String, Object> response = new HashMap<>();
            response.put("jobPosts", uniqueApplyJobPage.getContent());
            response.put("currentPage", uniqueApplyJobPage.getNumber());
            response.put("totalPages", uniqueApplyJobPage.getTotalPages());
            response.put("totalItems", uniqueApplyJobPage.getTotalElements());
 
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while processing your request: " + e.getMessage());
        }
	}
//	@GetMapping("/check-application")
//    public ResponseEntity<?> checkIfApplied(@RequestParam String jobid, @RequestParam String uid) {
//        try {
//            // Check if the user has already applied for the job
//            ApplyJob existingApplication = apd.findByJobidAndUid(jobid, uid);
//            if (existingApplication != null) {
//                // User has already applied for this job
//                return ResponseEntity.ok().body("User has already applied for this job");
//            } else {
//                // User has not applied for this job
//                return ResponseEntity.ok().body("User has not applied for this job");
//            }
//        } catch (DataAccessException e) {
//            e.printStackTrace();
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Database error occurred: " + e.getMessage());
//        } catch (Exception e) {
//            e.printStackTrace();
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while processing your request: " + e.getMessage());
//        }
//    }
	@GetMapping("/check-application")
	public ResponseEntity<Map<String, String>> checkIfApplied(@RequestParam String jobid, @RequestParam String uid) {
	    try {
	        // Check if the user has already applied for the job
	        ApplyJob existingApplication = apd.findByJobidAndUid(jobid, uid);
	        Map<String, String> response = new HashMap<>();
	        if (existingApplication != null) {
	            // User has already applied for this job
	            response.put("message", "User has already applied for this job");
	        } else {
	            // User has not applied for this job
	            response.put("message", "User has not applied for this job");
	        }
	        return ResponseEntity.ok(response);
	    } catch (DataAccessException e) {
	        e.printStackTrace();
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
	    } catch (Exception e) {
	        e.printStackTrace();
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
	    }
	}







  @CrossOrigin(origins = "${myapp.url}")
    @GetMapping("/notificationforuserApp")
    public ResponseEntity<?> notificationforuserApp(
            @RequestParam(required = false) String uid,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            Pageable pageable = PageRequest.of(page, size); // Create pageable object
            Page<ApplyJob> applyJobsPage;
	
            if (uid != null && !uid.isEmpty()) {
	
                applyJobsPage = apd.findByUid(uid, pageable); // Fetch paginated results for the specific user
                List<UserStatus> userStatusList = userstatdao.findByUid(uid);
 
                Iterator<ApplyJob> iterator = applyJobsPage.iterator();
 
                while (iterator.hasNext()) {
	
                    ApplyJob applyJob = iterator.next();
                    
                    boolean foundMatchingUserStatus = false;
                    for (UserStatus userStatus : userStatusList) {
                        if (uid.equals(userStatus.getUid()) &&
                                applyJob.getUid().equals(userStatus.getUid()) &&
                                applyJob.getJuid().equals(userStatus.getJuid()) &&
                                userStatus.getViewcheck() != null &&
                                userStatus.getViewcheck()) {
 
                            applyJob.setUserStatus(true);
                            foundMatchingUserStatus = true;
                            break;
                        }
                    }
 
                    if (!foundMatchingUserStatus) {
                        applyJob.setUserStatus(false);
                    }
 
                    if (applyJob.isNotifydelete()) {	
                        iterator.remove(); // Safely remove the element using iterator
                    }
                }
            } else {
                applyJobsPage = apd.findAll(pageable); // Fetch paginated results for all users
            }
 
            // Return paginated response with currentPage, totalPages, totalElements, and content
            Map<String, Object> response = new HashMap<>();
            response.put("jobPosts", applyJobsPage.getContent());
            response.put("currentPage", applyJobsPage.getNumber());
            response.put("totalPages", applyJobsPage.getTotalPages());
            response.put("totalElements", applyJobsPage.getTotalElements());
 
            return ResponseEntity.ok(response);
        } catch (DataAccessException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Database error occurred: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while processing your request: " + e.getMessage());
        }
    }
}
