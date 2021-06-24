package com.shopme.admin.user;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.annotation.Rollback;

import com.shopme.common.entity.Role;
import com.shopme.common.entity.User;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
public class UserRepositoryTest {
	
	@Autowired
	private UserRepository repo;
	
	@Autowired
	private TestEntityManager entityManager;
	
	@Test
	public void testCreateNewUserWithneRole() {
		User user = new User("vinhqd@gmail.com", "123456", "Vinh", "Quan Duc");
		Role roleAdmin = entityManager.find(Role.class, 1);
		user.addRole(roleAdmin);
		
		User savedUser = repo.save(user);
		
		assertThat(savedUser.getId()).isGreaterThan(0);
	}
	
	@Test
	public void testCreateNewUserWithTwoRole() {
		User user = new User("vinhqd1@gmail.com", "123456", "Vinh", "Quan Duc");
		Role roleEditor = new Role(3);
		Role roleAssistant = new Role(5);
		user.addRole(roleEditor);
		user.addRole(roleAssistant);
		
		User savedUser = repo.save(user);
		
		assertThat(savedUser.getId()).isGreaterThan(0);
	}
	
	@Test
	public void testListAllUsers() {
		Iterable<User> users = repo.findAll();
		users.forEach(user -> System.out.println(user));
	}
	
	@Test
	public void testGetUserById() {
		Optional<User> user = repo.findById(1);
		System.out.println(user.get());
		assertThat(user.get()).isNotNull();
	}
	
	@Test
	public void testUpdateUserDetails() {
		User user = repo.findById(1).get();
		user.setEnabled(true);
		user.setEmail("abc@abc.com");
		
		repo.save(user);
	}
	
	@Test
	public void testUpdateUserRole() {
		User user = repo.findById(2).get();
		user.getRoles().remove(new Role(1));
		repo.save(user);
	}
	
	@Test
	public void testDeleteUser() {
		Integer userId = 2;
		repo.deleteById(userId);
	}
	
	@Test
	public void testGetUserByEmail() {
		String email = "vinh@localhost";
		User user = repo.getUserByEmail(email);
		assertThat(user).isNotNull();
	}
	
	@Test
	public void testCountById() {
		Long count = repo.countById(1);
		System.out.println(count);
		assertThat(count).isGreaterThan(0);
	}
	
	@Test
	public void testUpdateEnabledStatus() {
		repo.updateEnabledStatus(1, false);
	}
	
	@Test
	public void testListFirstPage() {
		
		int pageNumber = 1;
		int pageSize = 4;
		
		Pageable pageable = PageRequest.of(pageNumber, pageSize);
		Page<User> page = repo.findAll(pageable);
		List<User> users = page.getContent();
		users.forEach(user -> {
			System.out.println(user);
		});
		assertThat(users.size()).isEqualTo(pageSize);
		
	}
	
	@Test
	public void testSearchUsers() {
		
		int pageNumber = 1;
		int pageSize = 4;
		
		Pageable pageable = PageRequest.of(pageNumber, pageSize);
		Page<User> page = repo.findAll("admin", pageable);
		List<User> users = page.getContent();
		users.forEach(user -> {
			System.out.println(user);
		});
		
	}

}
