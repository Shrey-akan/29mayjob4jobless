package com.demo.oragejobsite.controller;


import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.demo.oragejobsite.dao.PostjobDao;
import com.demo.oragejobsite.dao.RefreshTokenRepository;
import com.demo.oragejobsite.dao.SubAdminDao;
import com.demo.oragejobsite.entity.PostJob;
import com.demo.oragejobsite.entity.RefreshToken;
import com.demo.oragejobsite.entity.SubAdminDetails;
import com.demo.oragejobsite.service.SubAdminDetailsService;
import com.demo.oragejobsite.util.TokenProvider;





@RestController
@RequestMapping("/subadmindetails")
@CrossOrigin(origins = "${myapp.url}")
public class SubAdminDetController {
	
	  private final SubAdminDetailsService subAdminDetailsService;
	    private final TokenProvider tokenProvider;
	    private final RefreshTokenRepository refreshTokenRepository;
	    private final PostjobDao postjobDao;
	    
	    
	    @Autowired
	    private SubAdminDao subAdminDetailsDao;
	    
	    @Autowired
	    public SubAdminDetController(SubAdminDetailsService subAdminDetailsService,
	                                 TokenProvider tokenProvider,
	                                 RefreshTokenRepository refreshTokenRepository,
	                                 PostjobDao postjobDao) {
	        this.subAdminDetailsService = subAdminDetailsService;
	        this.tokenProvider = tokenProvider;
	        this.refreshTokenRepository = refreshTokenRepository;
	        this.postjobDao = postjobDao;
	    }
    	
    private static String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(password.getBytes());
            byte[] hashedPasswordBytes = md.digest();

            StringBuilder sb = new StringBuilder();
            for (byte b : hashedPasswordBytes) {
                sb.append(String.format("%02x", b));
            }

            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error hashing password", e);
        }
    }
    
    
    
    @CrossOrigin(origins = "${myapp.url}")
    @PostMapping("/subadminLoginCheck")
    public ResponseEntity<?> subadminLoginCheck(@RequestBody SubAdminDetails subAdmin, HttpServletResponse response) {
        try {
            String subAdminMail = subAdmin.getSubadminmail();
            String subAdminPass = subAdmin.getSubadminpassword();
            subAdminPass = hashPassword(subAdminPass);

            SubAdminDetails authenticatedSubAdmin = subAdminDetailsService.authenticateSubAdmin(subAdminMail, subAdminPass);

            if (authenticatedSubAdmin != null) {
                Cookie subAdminCookie = new Cookie("subadmin", subAdminMail);
                subAdminCookie.setMaxAge(3600);
                subAdminCookie.setPath("/");
                response.addCookie(subAdminCookie);

                String refreshToken = tokenProvider.generateRefreshToken(subAdminMail, authenticatedSubAdmin.getSubadminid());

                RefreshToken refreshTokenEntity = new RefreshToken();
                refreshTokenEntity.setTokenId(refreshToken);
                refreshTokenEntity.setUsername(authenticatedSubAdmin.getSubadminid());
                refreshTokenEntity.setExpiryDate(tokenProvider.getExpirationDateFromRefreshToken(refreshToken));
                refreshTokenRepository.save(refreshTokenEntity);

                String accessToken = tokenProvider.generateAccessToken(authenticatedSubAdmin.getSubadminid());

                Map<String, Object> responseBody = new HashMap<>();
                responseBody.put("accessToken", accessToken);
                responseBody.put("refreshToken", refreshToken);
                responseBody.put("subadminid", authenticatedSubAdmin.getSubadminid());

                return ResponseEntity.status(HttpStatus.OK).body(responseBody);
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid subadmin credentials");
            }
        } catch (DataAccessException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error accessing data");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Unexpected error");
        }
    }
    
    @PostMapping("/add")
    public ResponseEntity<Object> addSubAdmin(@RequestBody SubAdminDetails subAdminDetails) {
        String hashedPassword = hashPassword(subAdminDetails.getSubadminpassword());
        subAdminDetails.setSubadminpassword(hashedPassword);
        return subAdminDetailsService.addSubAdmin(subAdminDetails);
    }

//    @GetMapping("/all")
//    public ResponseEntity<Object> getAllSubAdmins() {
//        return subAdminDetailsService.getAllSubAdmins();
//    }
    @GetMapping("/all")
    public ResponseEntity<Object> getAllSubAdmins(@RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {
    	try {
            Pageable pageable = PageRequest.of(page, size);
            Page<SubAdminDetails> subAdminsPage = subAdminDetailsDao.findAll(pageable);
            
            Map<String, Object> response = new HashMap<>();
            response.put("content", subAdminsPage.getContent());
            response.put("totalItems", subAdminsPage.getTotalElements());
            response.put("currentPage", subAdminsPage.getNumber());
            response.put("totalPages", subAdminsPage.getTotalPages());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getSubAdminById(@PathVariable String id) {
        return subAdminDetailsService.getSubAdminById(id);
    }

//    @PutMapping("/{id}")
//    public ResponseEntity<Object> updateSubAdmin(@PathVariable String id, @RequestBody SubAdminDetails subAdminDetails) {
//        return subAdminDetailsService.updateSubAdmin(id, subAdminDetails);
//    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Object> updateSubAdmin(@PathVariable String id, @RequestBody SubAdminDetails subAdminDetails) {
        try {
        	
            Optional<SubAdminDetails> existingSubAdminOptional = subAdminDetailsDao.findById(id);
            if (!existingSubAdminOptional.isPresent()) {
                return ResponseEntity.notFound().build();
            }
            
            // Update the existing subadmin details
            SubAdminDetails existingSubAdmin = existingSubAdminOptional.get();
            existingSubAdmin.setSubadminame(subAdminDetails.getSubadminame());
            existingSubAdmin.setSubadminmail(subAdminDetails.getSubadminmail());
            existingSubAdmin.setSubadminpassword(subAdminDetails.getSubadminpassword());
            existingSubAdmin.setManageUsers(subAdminDetails.isManageUsers());
            existingSubAdmin.setManageEmployers(subAdminDetails.isManageEmployers());
            existingSubAdmin.setPostJob(subAdminDetails.isPostJob());
            existingSubAdmin.setApplyJob(subAdminDetails.isApplyJob());
            existingSubAdmin.setManageBlogs(subAdminDetails.isManageBlogs());
            existingSubAdmin.setPushNotification(subAdminDetails.isPushNotification());
            existingSubAdmin.setApproveJobDetails(subAdminDetails.isApproveJobDetails());
            existingSubAdmin.setEnquiry(subAdminDetails.isEnquiry());
            // Update fields of existingSubAdmin with values from subAdminDetails

            subAdminDetailsDao.save(existingSubAdmin); // Save the updated subadmin details
            
            return ResponseEntity.ok().body("{\"message\": \"SubAdmin with ID " + id + " updated successfully\"}");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to update SubAdmin with ID: " + id);
        }
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteSubAdmin(@PathVariable String id) {
        try {
            Optional<SubAdminDetails> subAdminOptional = subAdminDetailsDao.findById(id);
            if (!subAdminOptional.isPresent()) {
                return ResponseEntity.notFound().build();
            }
            subAdminDetailsDao.deleteById(id);
//            return ResponseEntity.ok("SubAdmin with ID " + id + " deleted successfully");
            return ResponseEntity.ok().body("{\"message\": \"SubAdmin with ID " + id + " deleted successfully\"}");

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to delete SubAdmin with ID: " + id);
        }
    }

    
    @PostMapping("/approveJob")
    public ResponseEntity<?> approveJobPost(@RequestParam String jobId, @RequestParam String subAdminId) {
        try {
            SubAdminDetails subAdmin = subAdminDetailsService.getSubAdById(subAdminId);
            if (subAdmin == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Subadmin not found");
            }

            if (!subAdmin.isApproveJobDetails()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Subadmin does not have permission to approve job posts");
            }
            
            
            PostJob postJob = postjobDao.findByJobid(jobId);
            if (postJob == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Job post not found");
            }

            postJob.setApprovejob(true);
            postjobDao.save(postJob);

            return ResponseEntity.status(HttpStatus.OK).body("Job post approved successfully");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error occurred while approving job post");
        }
    }

}
