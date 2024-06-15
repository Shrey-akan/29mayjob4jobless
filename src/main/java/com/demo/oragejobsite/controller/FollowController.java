package com.demo.oragejobsite.controller;

import com.demo.oragejobsite.entity.Employer;
import com.demo.oragejobsite.entity.Follow;
import com.demo.oragejobsite.entity.PostJob;
import com.demo.oragejobsite.dao.EmployerDao;
import com.demo.oragejobsite.dao.FollowRepository;
import com.demo.oragejobsite.dao.PostjobDao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/follows")
public class FollowController {

    @Autowired
    private FollowRepository followRepository;
    
    @Autowired
    private EmployerDao employerDao;
    
    @Autowired
    private PostjobDao postjobDao;
    
    @GetMapping
    public ResponseEntity<?> getAllFollows() {
        try {
            List<Follow> follows = followRepository.findAll();
            return ResponseEntity.ok(follows);
        } catch (Exception e) {
            // Log the exception (optional)
            e.printStackTrace();

            // Return an internal server error response
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while fetching all follows: " + e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getFollowById(@PathVariable String id) {
        try {
            return followRepository.findById(id)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            // Log the exception (optional)
            e.printStackTrace();

            // Return an internal server error response
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while fetching follow by ID: " + e.getMessage());
        }
    }


    @PostMapping
    public ResponseEntity<?> toggleFollow(@RequestBody Follow follow) {
        try {
            // Check if a follow record already exists for the given uid and empid
            List<Follow> existingFollows = followRepository.findByUidAndEmpid(follow.getUid(), follow.getEmpid());
            
            if (!existingFollows.isEmpty()) {
                // If exists, delete the existing follow record (unfollow)
                followRepository.deleteAll(existingFollows);
                return ResponseEntity.status(HttpStatus.OK).body("Unfollowed successfully.");
            } else {
                // If not exists, create a new follow record (follow)
                Follow savedFollow = followRepository.save(follow);
                return ResponseEntity.status(HttpStatus.CREATED).body(savedFollow);
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while processing the follow/unfollow action: " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateFollow(@PathVariable String id, @RequestBody Follow followDetails) {
        try {
            return followRepository.findById(id)
                    .map(follow -> {
                        follow.setUid(followDetails.getUid());
                        follow.setEmpid(followDetails.getEmpid());
                        follow.setSendTime(followDetails.getSendTime());
                        follow.setFollowing(followDetails.isFollowing());
                        Follow updatedFollow = followRepository.save(follow);
                        return ResponseEntity.ok(updatedFollow);
                    })
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            // Log the exception (optional)
            e.printStackTrace();

            // Return an internal server error response
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while updating the follow record: " + e.getMessage());
        }
    }

    @GetMapping("/byuid")
    public ResponseEntity<?> getFollowsByUid(@RequestParam String uid) {
        try {
            List<Follow> follows = followRepository.findByUid(uid);
            if (follows.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No follows found for the given UID.");
            }
            return ResponseEntity.ok(follows);
        } catch (Exception e) {
            // Log the exception (optional)
            e.printStackTrace();

            // Return an internal server error response
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while fetching follows by UID: " + e.getMessage());
        }
    }

    @GetMapping("/emp/{empid}")
    public ResponseEntity<?> getFollowsByEmpid(@PathVariable String empid) {
        try {
            List<Follow> follows = followRepository.findByEmpid(empid);
            if (follows.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No follows found for the given empid.");
            }
            return ResponseEntity.ok(follows);
        } catch (Exception e) {
            // Log the exception (optional)
            e.printStackTrace();

            // Return an internal server error response
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while fetching follows by empid: " + e.getMessage());
        }
    }

    
    @GetMapping("/count/unique-empid")
    public ResponseEntity<?> getCountOfUniqueEmpids(@RequestParam String uid) {
        try {
            // Fetch follows by the user
            List<Follow> follows = followRepository.findByUid(uid);
            if (follows.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No follows found for the given UID.");
            }

            // Count unique empids followed by the user
            long uniqueEmpidCount = follows.stream()
                    .map(Follow::getEmpid)
                    .distinct()
                    .count();

            return ResponseEntity.ok(uniqueEmpidCount);
        } catch (Exception e) {
            // Log the exception (optional)
            e.printStackTrace();

            // Return an internal server error response
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while counting unique empids: " + e.getMessage());
        }
    }

    
    @GetMapping("/unique-empids")
    public ResponseEntity<?> getUniqueEmpIdsByUid(@RequestParam String uid) {
        try {
            // Fetch follows by the user
            List<Follow> follows = followRepository.findByUid(uid);
            if (follows.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No follows found for the given UID.");
            }

            // Get unique empids followed by the user
            List<String> uniqueEmpIds = follows.stream()
                    .map(Follow::getEmpid)
                    .distinct()
                    .collect(Collectors.toList());

            return ResponseEntity.ok(uniqueEmpIds);
        } catch (Exception e) {
            // Log the exception (optional)
            e.printStackTrace();

            // Return an internal server error response
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while retrieving unique empids: " + e.getMessage());
        }
    }


    @GetMapping("/employer-data")
    public ResponseEntity<?> getEmployerDataByUid(@RequestParam String uid) {
        try {
            // Fetch follows by the user
            List<Follow> follows = followRepository.findByUid(uid);
            if (follows.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No follows found for the given UID.");
            }

            // Get unique empids followed by the user
            List<String> uniqueEmpIds = follows.stream()
                    .map(Follow::getEmpid)
                    .distinct()
                    .collect(Collectors.toList());

            // Fetch employer data for each unique empid
            List<Employer> employers = uniqueEmpIds.stream()
                    .map(empId -> employerDao.findByEmpid(empId).orElse(null))
                    .filter(emp -> emp != null)
                    .collect(Collectors.toList());

            return ResponseEntity.ok(employers);
        } catch (Exception e) {
            // Log the exception (optional)
            e.printStackTrace();

            // Return an internal server error response
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while retrieving employer data: " + e.getMessage());
        }
    }

    
    
    @GetMapping("/postjobs")
    public ResponseEntity<?> getPostJobsByUid(@RequestParam String uid) {
        try {
            // Fetch follows by the user
            List<Follow> follows = followRepository.findByUid(uid);
            if (follows.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No follows found for the given UID.");
            }

            // Get unique empids followed by the user
            List<String> uniqueEmpIds = follows.stream()
                    .map(Follow::getEmpid)
                    .distinct()
                    .collect(Collectors.toList());

            // Log unique empids
            System.out.println("Unique empids: " + uniqueEmpIds);

            // Fetch approved post jobs for each unique empid
            List<PostJob> postJobs = uniqueEmpIds.stream()
                    .flatMap(empId -> {
                        List<PostJob> jobs = postjobDao.findByEmpidAndApprovejob(empId, true);
                        System.out.println("Jobs for empid " + empId + ": " + jobs);
                        return jobs.stream();
                    })
                    .collect(Collectors.toList());

            return ResponseEntity.ok(postJobs);
        } catch (Exception e) {
            // Log the exception (optional)
            e.printStackTrace();

            // Return an internal server error response
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while retrieving post jobs: " + e.getMessage());
        }
    }

    
    
    @GetMapping("/approved-postjobs")
    public ResponseEntity<?> getApprovedPostJobsByUidAndEmpid(
            @RequestParam String uid,
            @RequestParam String empid) {
        try {
            // Check if the user follows the given empid
            List<Follow> follows = followRepository.findByUidAndEmpid(uid, empid);
            if (follows.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("The user does not follow the specified employee.");
            }

            // Get the follow record for the given uid and empid
            Follow follow = follows.get(0); // Assuming only one follow record per (uid, empid) pair

            // Fetch approved post jobs for the given empid and with sendTime greater than follow's sendTime
            List<PostJob> approvedPostJobs = postjobDao.findByEmpidAndApprovejob(empid, true).stream()
                    .filter(postJob -> postJob.getSendTime().after(follow.getSendTime()))
                    .collect(Collectors.toList());

            return ResponseEntity.ok(approvedPostJobs);
        } catch (Exception e) {
            // Log the exception (optional)
            e.printStackTrace();

            // Return an internal server error response
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while retrieving approved post jobs: " + e.getMessage());
        }
    }

    
    
    
    @GetMapping("/posted-job-count")
    public ResponseEntity<?> getPostedJobCountByUid(@RequestParam String uid) {
        try {
            // Get follows by the user
            List<Follow> follows = followRepository.findByUid(uid);

            // Check if follows list is empty
            if (follows.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No follows found for the given uid.");
            }

            // Group follows by empid and get the latest sendTime for each empid
            Map<String, Date> empidToSendTimeMap = follows.stream()
                    .collect(Collectors.toMap(
                            Follow::getEmpid,
                            Follow::getSendTime,
                            (oldValue, newValue) -> newValue.after(oldValue) ? newValue : oldValue
                    ));

            // Get the count of approved jobs for each unique empid where sendTime > follow's sendTime
            long totalApprovedJobCount = empidToSendTimeMap.entrySet().stream()
                    .mapToLong(entry -> {
                        String empid = entry.getKey();
                        Date followSendTime = entry.getValue();
                        return postjobDao.findByEmpidAndApprovejobTrue(empid).stream()
                                .filter(postJob -> postJob.getSendTime().before(followSendTime))
                                .count();
                    })
                    .sum();

            return ResponseEntity.ok(totalApprovedJobCount);
        } catch (Exception e) {
            // Log the exception (optional)
            e.printStackTrace();

            // Return an internal server error response
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while retrieving the posted job count: " + e.getMessage());
        }
    }

    
    @GetMapping("/follow-count")
    public ResponseEntity<?> getFollowCountByEmpid(@RequestParam String empid) {
        try {
            List<Follow> follows = followRepository.findByEmpid(empid);
            long followCount = follows.size();
            return ResponseEntity.ok(followCount);
        } catch (Exception e) {
            // Log the exception (optional)
            e.printStackTrace();

            // Return an internal server error response
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while fetching follow count for empid: " + empid + ". Error: " + e.getMessage());
        }
    }


    
    
}
