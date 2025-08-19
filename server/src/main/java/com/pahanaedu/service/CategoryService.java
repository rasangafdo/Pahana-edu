package com.pahanaedu.service;

import com.pahanaedu.dao.DaoFactory;
import com.pahanaedu.dao.custom.CategoryDaoImpl;
import com.pahanaedu.model.Category;

import java.util.List;

public class CategoryService {
    private final CategoryDaoImpl categoryDAO;

    public CategoryService() {
        this.categoryDAO = (CategoryDaoImpl) DaoFactory.getInstance().getDao(DaoFactory.DaoTypes.CATEGORY);
    }

    // Get all with pagination
    public List<Category> getAll(int page) throws Exception {
        return categoryDAO.getAll(page);
    }

    // Get by ID
    public Category get(Long id) throws Exception {
        return categoryDAO.get(id);
    }

    // Search by name
    public List<Category> searchByName(String name) throws Exception {
        return categoryDAO.searchByName(name);
    }

    // Recently updated
    public List<Category> getRecentlyUpdated(int limit) throws Exception {
        return categoryDAO.getRecentlyUpdated(limit);
    }

    // With minimum items
    public List<Category> getCategoriesWithMinItems(int min) throws Exception {
        return categoryDAO.getCategoriesWithMinItems(min);
    }

    // Create new category (with duplicate check)
    public boolean create(Category category) throws Exception {
        if (categoryDAO.existsByName(category.getName())) {
            throw new IllegalStateException("Category already exists");
        }
        return categoryDAO.create(category);
    }

    // Update existing
    public boolean update(Category category) throws Exception {
        if (categoryDAO.existsByNameExcludingId(category.getName(), category.getCategoryId())) {
            throw new IllegalStateException("Another category with this name already exists");
        }
        return categoryDAO.update(category);
    }

    // Delete by ID or name
    public boolean deleteById(Long id) throws Exception {
        return categoryDAO.delete(id);
    }

    public boolean deleteByName(String name) throws Exception {
        return categoryDAO.deleteByName(name);
    }
}
