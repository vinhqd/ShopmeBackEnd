package com.shopme.admin.category;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shopme.common.entity.Category;

@Service
@Transactional
public class CategoryService {
	
	@Autowired
	private CategoryRepository repo;
	
	public List<Category> listAll(String sortDir) {
		Sort sort = Sort.by("name");
		if (sortDir.equalsIgnoreCase("asc")) {
			sort = sort.ascending();
		} else if (sortDir.equalsIgnoreCase("desc")) {
			sort = sort.descending();
		}
		List<Category> rootCategories = repo.findRootCategories(sort);
		return listHierarchicalCategories(rootCategories, sortDir);
	}
	
	private List<Category> listHierarchicalCategories(List<Category> rootCategories, String sortDir) {
		List<Category> hierarchicalCategories = new ArrayList<>();
		
		for (Category rootCategory : rootCategories) {
			hierarchicalCategories.add(Category.copyFull(rootCategory));
			
			Set<Category> children = sortSubCategories(rootCategory.getChildren(), sortDir);
			for (Category subCategory : children) {
				String name = "--" + subCategory.getName();
				hierarchicalCategories.add(Category.copyFull(subCategory, name));
				
				listSubHierarchicalCategories(hierarchicalCategories, subCategory, 1, sortDir);
			}
		}
		
		return hierarchicalCategories;
	}
	
	private void listSubHierarchicalCategories(List<Category> hierarchicalCategories, Category parent, int subLevel, String sortDir) {
		int newSubLevel = subLevel + 1;
		Set<Category> children = sortSubCategories(parent.getChildren(), sortDir);
		for (Category subCategory : children) {
			String name = "";
			for (int i = 0; i < newSubLevel; i++) {
				name += "--";
			}
			name += subCategory.getName();
			hierarchicalCategories.add(Category.copyFull(subCategory, name));
			listSubCategoriesUsedInForm(hierarchicalCategories, subCategory, newSubLevel);
		}
	}

	public Category saveCategory(Category category) {
		return repo.save(category);
	}
	
	public List<Category> listCategoriesUsedInForm() {
		List<Category> categoriesInForm = new ArrayList<>();
		List<Category> categoriesInDB = repo.findRootCategories(Sort.by("name").ascending());
		
		for (Category category : categoriesInDB) {
			if (category.getParent() == null) {
				categoriesInForm.add(Category.copyIdAndName(category));
				Set<Category> children = sortSubCategories(category.getChildren());
				for (Category subCategory : children) {
					String name = "--" + subCategory.getName();
					categoriesInForm.add(Category.copyIdAndName(subCategory.getId(), name));
					listSubCategoriesUsedInForm(categoriesInForm, subCategory, 1);
				}
			}
		}
		
		return categoriesInForm;
	}
	
	private void listSubCategoriesUsedInForm(List<Category> categoriesInForm, Category parent, int subLevel) {
		int newSubLevel = subLevel + 1;
		Set<Category> children = sortSubCategories(parent.getChildren());
		for (Category subCategory : children) {
			String name = "";
			for (int i = 0; i < newSubLevel; i++) {
				name += "--";
			}
			name += subCategory.getName();
			categoriesInForm.add(Category.copyIdAndName(subCategory.getId(), name));
			listSubCategoriesUsedInForm(categoriesInForm, subCategory, newSubLevel);
		}
	}
	
	public Category get(Integer id) throws CategoryNotFoundException {
		try {
			return repo.findById(id).get();
		} catch (NoSuchElementException ex) {
			throw new CategoryNotFoundException("Could not find any Category with ID: " + id);
		}
	}
	
	public void enabledStatus(Integer id, boolean status) {
		repo.updateEnabledStatus(id, status);
	}
	
	public String checkUnique(Integer id, String name, String alias) {
		boolean isCreatingNew = (id == null || id == 0);
		Category categoryByName = repo.findByName(name);
		if (isCreatingNew) {
			if (categoryByName != null) {
				return "DuplicateName";
			} else {
				Category categoryByAlias = repo.findByAlias(alias);
				if (categoryByAlias != null) {
					return "DuplicateAlias";
				}
			}
		} else {
			if (categoryByName != null && categoryByName.getId() != id) {
				return "DuplicateName";
			}
			Category categoryByAlias = repo.findByAlias(alias);
			if (categoryByAlias != null && categoryByAlias.getId() != id) {
				return "DuplicateAlias";
			}
		}
		return "OK";
	}
	
	private SortedSet<Category> sortSubCategories(Set<Category> childre) {
		return sortSubCategories(childre, "asc");
	}
	
	private SortedSet<Category> sortSubCategories(Set<Category> children, String sortDir) {
		SortedSet<Category> sortedChildren = new TreeSet<>(new Comparator<Category>() {
			@Override
			public int compare(Category o1, Category o2) {
				if (sortDir.equalsIgnoreCase("asc")) {
					return o1.getName().compareTo(o2.getName());
				} else {
					return o2.getName().compareTo(o1.getName());
				}
			}
		});
		sortedChildren.addAll(children);
		return sortedChildren;
	}

}
