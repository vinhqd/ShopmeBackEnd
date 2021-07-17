package com.shopme.admin.user.controller;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.shopme.admin.FileUploadUtil;
import com.shopme.admin.user.UserNotFoundException;
import com.shopme.admin.user.UserService;
import com.shopme.admin.user.export.UserCsvExporter;
import com.shopme.admin.user.export.UserExcelExporter;
import com.shopme.admin.user.export.UserPdfExporter;
import com.shopme.common.entity.Role;
import com.shopme.common.entity.User;

@Controller
@RequestMapping("/users")
public class UserController {
	
	@Autowired
	private UserService service;
	
	@GetMapping
	public String listAll(Model model) {
		return listByPage(1, model, "id", "asc", null);
	}
	
	@GetMapping("/page/{pageNum}")
	public String listByPage(@PathVariable("pageNum") int pageNum, Model model,
			@Param("sortField") String sortField, @Param("sortDir") String sortDir,
			@Param("keyword") String keyword) {
		Page<User> page = service.listByPage(pageNum, sortField, sortDir, keyword);
		List<User> users = page.getContent();
		
		long totalItems = page.getTotalElements();
		long startCount = (pageNum - 1) * UserService.USERS_PER_PAGE + 1;
		long endCount = startCount + UserService.USERS_PER_PAGE - 1;
		if (endCount > totalItems) {
			endCount = totalItems;
		}
		
		String reverseSortDir = sortDir.equalsIgnoreCase("asc") ? "desc" : "asc";
		
		model.addAttribute("currentPage", pageNum);
		model.addAttribute("totalItems", totalItems);
		model.addAttribute("totalPages", page.getTotalPages());
		model.addAttribute("startCount", startCount);
		model.addAttribute("endCount", endCount);
		model.addAttribute("sortField", sortField);
		model.addAttribute("sortDir", sortDir);
		model.addAttribute("reverseSortDir", reverseSortDir);
		model.addAttribute("keyword", keyword);
		model.addAttribute("listUsers", users);
		return "/users/users";
	}
	
	@GetMapping("/new")
	public String newUser(Model model) {
		List<Role> listRoles = service.listRoles();
		User user = new User();
		user.setEnabled(true);
		
		model.addAttribute("user", user);
		model.addAttribute("listRoles", listRoles);
		model.addAttribute("pageTitle", "Create new user");
		
		return "users/user_form";
	}
	
	@PostMapping("/save")
	public String save(User user, RedirectAttributes redirectAttributes,
			@RequestParam("image") MultipartFile multipartFile) throws IOException {
		if (!multipartFile.isEmpty()) {
			String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
			user.setPhotos(fileName);
			User savedUser = service.save(user);
			String uploadDir = "user-photos/" + savedUser.getId();
			FileUploadUtil.cleanDir(uploadDir);
			FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);
		} else {
			if (user.getPhotos().isEmpty()) user.setPhotos(null);
			service.save(user);
		}
		
		redirectAttributes.addFlashAttribute("message", "The user has been saved successfully.");
		
		return getRedirectURLtoAffectedUser(user);
	}

	private String getRedirectURLtoAffectedUser(User user) {
		String firstPathOfEmail = user.getEmail().split("@")[0];
		return "redirect:/users/page/1?sortField=id&sortDir=asc&keyword=" + firstPathOfEmail;
	}
	
	@GetMapping("/edit/{id}")
	public String editUser(@PathVariable(name = "id") Integer id,
			Model model,
			RedirectAttributes redirectAttributes) {
		try {
			User user = service.get(id);
			List<Role> listRoles = service.listRoles();
			model.addAttribute("user", user);
			model.addAttribute("listRoles", listRoles);
			model.addAttribute("pageTitle", "Edit user (ID: " + id + ")");
			return "users/user_form";
		} catch (UserNotFoundException e) {
			redirectAttributes.addFlashAttribute("message", e.getMessage());
			return "redirect:/users";
		}
	}
	
	@GetMapping("/delete/{id}")
	public String deleteUser(
			@PathVariable("id") Integer id,
			Model model,
			RedirectAttributes redirectAttributes) {
		try {
			service.delete(id);
			redirectAttributes.addFlashAttribute("message", "The user ID: " + id + " has been deleted successfully");
		} catch (UserNotFoundException e) {
			redirectAttributes.addFlashAttribute("message", e.getMessage());
		}
		return "redirect:/users";
	}
	
	@GetMapping("/{id}/enabled/{status}")
	public String updateEnabledStatusUser(
			@PathVariable("id") Integer id, 
			@PathVariable("status") boolean enabled,
			RedirectAttributes redirectAttributes) {
		try {
			User user = service.get(id);
			service.updateUserEnabledStatus(id, enabled);
			String status = enabled ? "enabled" : "disabled";
			String message = "The user ID: " + id + " has beean " + status;
			redirectAttributes.addFlashAttribute("message", message);
			
			return getRedirectURLtoAffectedUser(user);
		} catch (UserNotFoundException e) {
			redirectAttributes.addFlashAttribute("message", e.getMessage());
			return "redirect:/users";
		}
	}
	
	@GetMapping("/export/csv")
	public void exportToCSV(HttpServletResponse response) throws IOException {
		List<User> users = service.listAll();
		UserCsvExporter exporter = new UserCsvExporter();
		exporter.export(users, response);
	}
	
	@GetMapping("/export/excel")
	public void exportToExcel(HttpServletResponse response) throws IOException {
		List<User> users = service.listAll();
		UserExcelExporter exporter = new UserExcelExporter();
		exporter.export(users, response);
	}
	
	@GetMapping("/export/pdf")
	public void exportToPdf(HttpServletResponse response) throws IOException {
		List<User> users = service.listAll();
		UserPdfExporter exporter = new UserPdfExporter();
		exporter.export(users, response);
	}

}
