package com.pahanaedu.dao.custom;
  
import java.util.List;
 
import com.pahanaedu.model.Category; 

public class CategoryDaoImpl implements  CategoryDao{

	@Override
	public boolean create(Category t) throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean update(Category t) throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean delete(Long id) throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Category get(Long id) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Category> getAll(int pageNumber) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Category> searchByName(String keyword) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean deleteByName(String name) throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean existsByName(String name) throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public List<Category> getRecentlyUpdated(int limit) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Category> getCategoriesWithMinItems(int minItems) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}
 

 

}
