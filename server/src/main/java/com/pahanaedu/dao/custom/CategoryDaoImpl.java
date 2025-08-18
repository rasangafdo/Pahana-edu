package com.pahanaedu.dao.custom;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import com.pahanaedu.dao.CrudUtil;
import com.pahanaedu.model.Category;

public class CategoryDaoImpl implements CategoryDao {

    @Override
    public boolean create(Category t) throws Exception {
        return CrudUtil.executeUpdate(
            "INSERT INTO category (name) VALUES (?)",
            t.getName()
        );
    }

    @Override
    public boolean update(Category t) throws Exception {
        return CrudUtil.executeUpdate(
            "UPDATE category SET name = ?,WHERE category_id = ?",
            t.getName(), t.getCategoryId()
        );
    }

    @Override
    public boolean delete(Long id) throws Exception {
        return CrudUtil.executeUpdate("DELETE FROM category WHERE category_id = ?", id);
    }

    @Override
    public Category get(Long id) throws Exception {
        ResultSet rs = CrudUtil.executeQuery("SELECT * FROM category WHERE category_id = ?", id);
        if (rs.next()) {
            return mapResultSetToCategory(rs);
        }
        return null;
    }

    @Override
    public List<Category> getAll(int pageNumber) throws Exception {
        List<Category> categories = new ArrayList<>();
        int offset = (pageNumber - 1) * 20;

        ResultSet rs = CrudUtil.executeQuery(
            "SELECT * FROM category ORDER BY last_updated_at DESC LIMIT 20 OFFSET ?",
            offset
        );
        while (rs.next()) {
            categories.add(mapResultSetToCategory(rs));
        }
        return categories;
    }

    @Override
    public List<Category> searchByName(String keyword) throws Exception {
        List<Category> categories = new ArrayList<>();
        ResultSet rs = CrudUtil.executeQuery(
            "SELECT * FROM category WHERE LOWER(name) LIKE ? ORDER BY last_updated_at DESC",
            "%" + keyword.toLowerCase() + "%"
        );
        while (rs.next()) {
            categories.add(mapResultSetToCategory(rs));
        }
        return categories;
    }

    @Override
    public boolean deleteByName(String name) throws Exception {
        return CrudUtil.executeUpdate("DELETE FROM category WHERE name = ?", name);
    }

    @Override
    public boolean existsByName(String name) throws Exception {
        ResultSet rs = CrudUtil.executeQuery("SELECT COUNT(*) FROM category WHERE name = ?", name);
        if (rs.next()) {
            return rs.getInt(1) > 0;
        }
        return false;
    }

    @Override
    public boolean existsByNameExcludingId(String name, Long excludeId) throws Exception {
        ResultSet rs = CrudUtil.executeQuery(
            "SELECT COUNT(*) FROM category WHERE name = ? AND category_id <> ?",
            name, excludeId
        );
        if (rs.next()) {
            return rs.getInt(1) > 0;
        }
        return false;
    }


    @Override
    public List<Category> getRecentlyUpdated(int limit) throws Exception {
        List<Category> categories = new ArrayList<>();
        ResultSet rs = CrudUtil.executeQuery(
            "SELECT * FROM category ORDER BY last_updated_at DESC LIMIT ?",
            limit
        );
        while (rs.next()) {
            categories.add(mapResultSetToCategory(rs));
        }
        return categories;
    }

    @Override
    public List<Category> getCategoriesWithMinItems(int minItems) throws Exception {
        List<Category> categories = new ArrayList<>();
        ResultSet rs = CrudUtil.executeQuery(
            "SELECT c.* FROM category c " +
            "JOIN item i ON c.category_id = i.category_id " +
            "GROUP BY c.category_id " +
            "HAVING COUNT(i.item_id) >= ?",
            minItems
        );
        while (rs.next()) {
            categories.add(mapResultSetToCategory(rs));
        }
        return categories;
    }

    private Category mapResultSetToCategory(ResultSet rs) throws SQLException {
        Category category = new Category();
        category.setCategoryId(rs.getLong("category_id"));
        category.setName(rs.getString("name"));
        Timestamp ts = rs.getTimestamp("last_updated_at");
        if (ts != null) {
            category.setLastUpdatedAt(ts.toLocalDateTime());
        }
        return category;
    }
}
