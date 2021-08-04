package com.shopme.admin.category;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.shopme.common.entity.Category;

@ExtendWith(MockitoExtension.class)
@ExtendWith(SpringExtension.class)
public class CategoryServiceTests {
	
	@MockBean
	private CategoryRepository repo;
	
	@InjectMocks
	private CategoryService service;
	
	@Test
	public void checkUniqueInNewModeReturnDuplicateName() {
		Integer id = null;
		String name = "Computers";
		String alias = "abc";
		
		Category category = new Category(id, name, alias);
		
		Mockito.when(repo.findByName(name)).thenReturn(category);
		Mockito.when(repo.findByAlias(name)).thenReturn(null);
		
		String results = service.checkUnique(id, name, alias);
		
		assertThat(results).isEqualTo("DuplicateName");
	}
	
	@Test
	public void checkUniqueInNewModeReturnDuplicateAlias() {
		Integer id = null;
		String name = "NameAbc";
		String alias = "computers";
		
		Category category = new Category(id, name, alias);
		
		Mockito.when(repo.findByName(name)).thenReturn(null);
		Mockito.when(repo.findByAlias(alias)).thenReturn(category);
		
		String results = service.checkUnique(id, name, alias);
		
		assertThat(results).isEqualTo("DuplicateAlias");
	}
	
	@Test
	public void checkUniqueInNewModeReturnDuplicateOK() {
		Integer id = null;
		String name = "NameAbc";
		String alias = "computers";
		
		Mockito.when(repo.findByName(name)).thenReturn(null);
		Mockito.when(repo.findByAlias(alias)).thenReturn(null);
		
		String results = service.checkUnique(id, name, alias);
		
		assertThat(results).isEqualTo("OK");
	}

}
