package com.shopme.admin.category;

import org.springframework.data.jpa.repository.JpaRepository;

import com.shopme.common.entity.Category;

public interface CategoryRepository extends JpaRepository<Category, Integer> {

}
