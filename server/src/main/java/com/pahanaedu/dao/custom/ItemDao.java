package com.pahanaedu.dao.custom;

import java.util.List;

import com.pahanaedu.dao.CrudDao;
import com.pahanaedu.dto.PaginatedResponse;
import com.pahanaedu.model.Item;

public interface ItemDao extends CrudDao<Item, Long> {

	PaginatedResponse<Item> searchByName(String keyword, int pageNumber) throws Exception;
	// Search items by name with pagination

	PaginatedResponse<Item> getItemsByCategoryId(Long categoryId, int pageNumber) throws Exception;
	// Get all items under a specific category

	List<Item> getLowStockItems(int threshold) throws Exception;
	// Items where stock_available < threshold

	boolean updateStock(Long itemId, int newStock) throws Exception;
	// Update stock for a specific item

	boolean updateDiscount(Long itemId, double discount, int qtyToAllowDiscount) throws Exception;
	// Change discount and quantity required for discount

	boolean existsByName(String name) throws Exception;
	// Check if item with name exists

	boolean existsByNameExcludingId(String name, Long excludeId) throws Exception;
}
