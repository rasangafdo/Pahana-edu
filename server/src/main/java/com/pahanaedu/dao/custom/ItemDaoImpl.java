package com.pahanaedu.dao.custom;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import com.pahanaedu.dao.CrudUtil;
import com.pahanaedu.model.Category;
import com.pahanaedu.model.Item;

public class ItemDaoImpl implements ItemDao {

    @Override
    public boolean create(Item t) throws Exception {
        return CrudUtil.executeUpdate(
            "INSERT INTO item (name, unit_price, stock_available, discount, qty_to_allow_discount, last_updated_at, category_id) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?)",
            t.getName(), t.getUnitPrice(), t.getStockAvailable(), t.getDiscount(),
            t.getQtyToAllowDiscount(), t.getLastUpdatedAt(), t.getCategory().getCategoryId()
        );
    }

    @Override
    public boolean update(Item t) throws Exception {
        return CrudUtil.executeUpdate(
            "UPDATE item SET name=?, unit_price=?, stock_available=?, discount=?, qty_to_allow_discount=?, last_updated_at=?, category_id=? " +
            "WHERE item_id=?",
            t.getName(), t.getUnitPrice(), t.getStockAvailable(), t.getDiscount(),
            t.getQtyToAllowDiscount(), t.getLastUpdatedAt(), t.getCategory().getCategoryId(), t.getItemId()
        );
    }

    @Override
    public boolean delete(Long id) throws Exception {
        return CrudUtil.executeUpdate("DELETE FROM item WHERE item_id = ?", id);
    }

    @Override
    public Item get(Long id) throws Exception {
        ResultSet rs = CrudUtil.executeQuery("SELECT * FROM item WHERE item_id = ?", id);
        if (rs.next()) {
            return mapResultSetToItem(rs);
        }
        return null;
    }

    @Override
    public List<Item> getAll(int pageNumber) throws Exception {
        List<Item> items = new ArrayList<>();
        int offset = (pageNumber - 1) * 20;

        ResultSet rs = CrudUtil.executeQuery(
            "SELECT * FROM item ORDER BY last_updated_at DESC LIMIT 20 OFFSET ?",
            offset
        );
        while (rs.next()) {
            items.add(mapResultSetToItem(rs));
        }
        return items;
    }

    @Override
    public List<Item> searchByName(String keyword, int pageNumber) throws Exception {
        List<Item> items = new ArrayList<>();
        int offset = (pageNumber - 1) * 20;

        ResultSet rs = CrudUtil.executeQuery(
            "SELECT * FROM item WHERE LOWER(name) LIKE ? ORDER BY last_updated_at DESC LIMIT 20 OFFSET ?",
            "%" + keyword.toLowerCase() + "%", offset
        );
        while (rs.next()) {
            items.add(mapResultSetToItem(rs));
        }
        return items;
    }

    @Override
    public List<Item> getItemsByCategoryId(Long categoryId, int pageNumber) throws Exception {
        List<Item> items = new ArrayList<>();
        int offset = (pageNumber - 1) * 20;

        ResultSet rs = CrudUtil.executeQuery(
            "SELECT * FROM item WHERE category_id = ? ORDER BY last_updated_at DESC LIMIT 20 OFFSET ?",
            categoryId, offset
        );
        while (rs.next()) {
            items.add(mapResultSetToItem(rs));
        }
        return items;
    }

    @Override
    public List<Item> getLowStockItems(int threshold) throws Exception {
        List<Item> items = new ArrayList<>();

        ResultSet rs = CrudUtil.executeQuery(
            "SELECT * FROM item WHERE stock_available < ? ORDER BY stock_available ASC",
            threshold
        );
        while (rs.next()) {
            items.add(mapResultSetToItem(rs));
        }
        return items;
    }

    @Override
    public boolean updateStock(Long itemId, int newStock) throws Exception {
        return CrudUtil.executeUpdate(
            "UPDATE item SET stock_available=?, last_updated_at=NOW() WHERE item_id=?",
            newStock, itemId
        );
    }

    @Override
    public boolean updateDiscount(Long itemId, double discount, int qtyToAllowDiscount) throws Exception {
        return CrudUtil.executeUpdate(
            "UPDATE item SET discount=?, qty_to_allow_discount=?, last_updated_at=NOW() WHERE item_id=?",
            discount, qtyToAllowDiscount, itemId
        );
    }

    @Override
    public boolean existsByName(String name) throws Exception {
        ResultSet rs = CrudUtil.executeQuery("SELECT COUNT(*) FROM item WHERE name = ?", name);
        if (rs.next()) {
            return rs.getInt(1) > 0;
        }
        return false;
    }

    // Mapper
    private Item mapResultSetToItem(ResultSet rs) throws SQLException {
        Item item = new Item();
        item.setItemId(rs.getLong("item_id"));
        item.setName(rs.getString("name"));
        item.setUnitPrice(rs.getDouble("unit_price"));
        item.setStockAvailable(rs.getInt("stock_available"));
        item.setDiscount(rs.getDouble("discount"));
        item.setQtyToAllowDiscount(rs.getInt("qty_to_allow_discount"));
        Timestamp ts = rs.getTimestamp("last_updated_at");
        if (ts != null) {
            item.setLastUpdatedAt(ts.toLocalDateTime());
        }

        // Map category reference (only ID here, full Category can be fetched separately)
        Category category = new Category();
        category.setCategoryId(rs.getLong("category_id"));
        item.setCategory(category);

        return item;
    }
}
