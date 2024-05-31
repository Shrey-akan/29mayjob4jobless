package com.demo.oragejobsite.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.demo.oragejobsite.dao.ConatctDao;
import com.demo.oragejobsite.entity.Contact;

@CrossOrigin(origins = "${myapp.url}")
@RestController
public class ContactController {
	@Autowired
	private ConatctDao cd;
	
	
	@CrossOrigin(origins = "${myapp.url}")
	@PostMapping("/insertcontact")
	public ResponseEntity<Boolean> insertcontact(@RequestBody Contact contact) {
	    try {
	        Contact savedContact = cd.save(contact);
	        return ResponseEntity.status(HttpStatus.CREATED).body(true);
	    } catch (DataAccessException e) {
	        e.printStackTrace();
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(false);
	    } catch (Exception e) {
	        e.printStackTrace();
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(false);
	    }
	}

	
	
//	@CrossOrigin(origins = "${myapp.url}")
//	@GetMapping("/fetchcontact")
//	public ResponseEntity<List<Contact>> fetchcontact() {
//	    try {
//	        List<Contact> contacts = cd.findAll();
//	        return ResponseEntity.ok(contacts);
//	    } catch (Exception e) {
//	        e.printStackTrace();
//	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
//	    }
//	}
	@CrossOrigin(origins = "${myapp.url}")
	@GetMapping("/fetchcontact")
	public ResponseEntity<?> fetchcontact(
			@RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {
		  try {
	            Pageable pageable = PageRequest.of(page, size);
	            Page<Contact> contacts = cd.findAll(pageable);
	            
	            Map<String, Object> response = new HashMap<>();
	  	        response.put("contacts", contacts.getContent());
	  	        response.put("currentPage", page);
	  	        response.put("totalItems", contacts.getTotalElements());
	  	        response.put("totalPages", contacts.getTotalPages());
	            return ResponseEntity.ok(response);
	        } catch (Exception e) {
	            e.printStackTrace();
	            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
	        }
	}
}
