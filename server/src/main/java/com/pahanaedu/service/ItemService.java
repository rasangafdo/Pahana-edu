package com.pahanaedu.service;

import com.pahanaedu.dao.DaoFactory;
import com.pahanaedu.dao.custom.ItemDaoImpl;
import com.pahanaedu.dto.PaginatedResponse;
import com.pahanaedu.model.Item;

import java.util.List;

public class ItemService {

    private final ItemDaoImpl itemDAO = (ItemDaoImpl) DaoFactory.getInstance().getDao(DaoFactory.DaoTypes.ITEM);

    public PaginatedResponse<Item> getAll(int page) throws Exception{
        return itemDAO.getAll(page);
    }

    public Item get(Long id)throws Exception {
        return itemDAO.get(id);
    }

    public PaginatedResponse<Item> searchByName(String name, int page) throws Exception{
        return itemDAO.searchByName(name, page);
    }

    public PaginatedResponse<Item> getItemsByCategoryId(Long categoryId, int page) throws Exception{
        return itemDAO.getItemsByCategoryId(categoryId, page);
    }

    public List<Item> getLowStockItems(int threshold)throws Exception {
        return itemDAO.getLowStockItems(threshold);
    }

    public boolean create(Item item) throws Exception{
        if (itemDAO.existsByName(item.getName())) {
            throw new IllegalStateException("Item already exists");
        }
        return itemDAO.create(item);
    }

    public boolean update(Item item) throws Exception{
        return itemDAO.update(item);
    }

    public boolean updateStock(Long id, int stock)throws Exception {
        Item item = itemDAO.get(id);
        if (item == null) return false;
        itemDAO.updateStock(id, stock);
        return true;
    }

    public boolean updateDiscount(Long id, double discount, int qty) throws Exception{
        Item item = itemDAO.get(id);
        if (item == null) return false;
        itemDAO.updateDiscount(id, discount, qty);
        return true;
    }

    public boolean delete(Long id) throws Exception{
        return itemDAO.delete(id);
    }

    public boolean existsByNameExcludingId(String name, Long excludeId) throws Exception{
        return itemDAO.existsByNameExcludingId(name, excludeId);
    }
}
