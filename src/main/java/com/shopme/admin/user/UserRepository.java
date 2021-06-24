package com.shopme.admin.user;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.shopme.common.entity.User;

public interface UserRepository extends JpaRepository<User, Integer> {
	
	@Query("SELECT u FROM User u WHERE email = :email")
	User getUserByEmail(@Param("email") String email);
	
	Long countById(Integer id);
	
	@Query("SELECT u FROM User u JOIN u.roles r WHERE CONCAT(u.id, ' ', u.email, ' '"
			+ ", u.firstName, ' ', u.lastName, '', r.name) LIKE %?1% GROUP BY u.id")
	Page<User> findAll(String keyword, Pageable pageable);
	
	@Query("UPDATE User u SET u.enabled = ?2 WHERE u.id = ?1")
	@Modifying
	void updateEnabledStatus( Integer id, boolean enabled);

}
