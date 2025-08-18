package com.pahanaedu.dao.custom;

import java.sql.ResultSet;
import java.sql.SQLException; 
import java.util.ArrayList;
import java.util.List;

import com.pahanaedu.dao.CrudUtil; 
import com.pahanaedu.model.Item;
import com.pahanaedu.util.Util;

public class ItemDaoImpl implements ItemDao {

    @Override
    public boolean create(Item t) throws Exception {
        return CrudUtil.executeUpdate(
            "INSERT INTO item (name, unit_price, stock_available, discount, qty_to_allow_discount,  category_id) " +
            "VALUES (?, ?, ?, ?, ?, ?)",
            t.getName(), t.getUnitPrice(), t.getStockAvailable(), t.getDiscount(),
            t.getQtyToAllowDiscount(),  t.getCategoryId()
        );
    }

    @Override
    public boolean update(Item item) throws Exception {
        // Fetch existing item
        ResultSet rs = CrudUtil.executeQuery(
            "SELECT * FROM item WHERE item_id = ?",
            item.getItemId()
        );

        if (!rs.next()) {
            return false; // not found
        }

        Item oldItem = mapResultSetToItem(rs);

        // Use old values if null/empty
        String name =  Util.anyNullOrEmpty(item.getName())  ? oldItem.getName() : item.getName();
        Double unitPrice = Util.anyNullOrEmpty(item.getUnitPrice()) ? oldItem.getUnitPrice() : item.getUnitPrice();
        Integer stockAvailable = Util.anyNullOrEmpty(item.getStockAvailable()) ? oldItem.getStockAvailable() : item.getStockAvailable();
        Double discount = Util.anyNullOrEmpty(item.getDiscount())  ? oldItem.getDiscount() : item.getDiscount();
        Integer qtyToAllowDiscount = Util.anyNullOrEmpty(item.getQtyToAllowDiscount()) ? oldItem.getQtyToAllowDiscount() : item.getQtyToAllowDiscount();
        Long categoryId = Util.anyNullOrEmpty(item.getCategoryId()) ? oldItem.getCategoryId() : item.getCategoryId();

        // Perform update
        return CrudUtil.executeUpdate(
            "UPDATE item SET name=?, unit_price=?, stock_available=?, discount=?, qty_to_allow_discount=?, category_id=? WHERE item_id=?",
            name, unitPrice, stockAvailable, discount, qtyToAllowDiscount, categoryId, item.getItemId()
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
            "SELECT * FROM item WHERE stock_available <= ? ORDER BY stock_available ASC",
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
            "UPDATE item SET stock_available=?  WHERE item_id=?",
            newStock, itemId
        );
    }

    @Override
    public boolean updateDiscount(Long itemId, double discount, int qtyToAllowDiscount) throws Exception {
        return CrudUtil.executeUpdate(
            "UPDATE item SET discount=?, qty_to_allow_discount=?  WHERE item_id=?",
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

    @Override
    public boolean existsByNameExcludingId(String name, Long excludeId) throws Exception {
        ResultSet rs = CrudUtil.executeQuery(
            "SELECT COUNT(*) FROM item WHERE name = ? AND item_id <> ?",
            name, excludeId
        );
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
            item.setLastUpdatedAt(rs.getTimestamp("last_updated_at").toLocalDateTime());
        item.setCategoryId(rs.getLong("category_id"));

        return item;
    }
}
