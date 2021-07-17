package com.shopme.admin.user.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.shopme.admin.user.UserService;

@RestController
@RequestMapping("/users")
public class UserRestController {
	
	@Autowired
	private UserService service;
	
	@PostMapping("/check_email")
	public String checkDuplicateEmail(@Param("id") Integer id, @Param("email") String email) {
		return service.isEmailUnique(id, email) ? "OK" : "Duplicated";
	}

}
