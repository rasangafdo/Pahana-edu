package com.pahanaedu.dao.custom;

import java.util.List;

import com.pahanaedu.dao.CrudDao;
import com.pahanaedu.model.Category; 

public interface CategoryDao extends CrudDao<Category, Long> {

	List<Category> searchByName(String keyword) throws Exception;
	// Find categories whose name contains a keyword

	boolean deleteByName(String name) throws Exception;
	// Delete a category by its name

	boolean existsByName(String name) throws Exception;
	// Check if a category with a given name exists

	List<Category> getRecentlyUpdated(int limit) throws Exception;
	// Get categories ordered by last_updated_at DESC

	List<Category> getCategoriesWithMinItems(int minItems) throws Exception;
	// Get categories having at least 'minItems' number of items

	
}
