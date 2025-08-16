package com.pahanaedu.dao.custom;
  
import java.util.List;
 
import com.pahanaedu.model.Item; 

public class SaleItemDaoImpl implements  ItemDao{

	@Override
	public boolean create(Item t) throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean update(Item t) throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean delete(Long id) throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Item get(Long id) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Item> getAll(int pageNumber) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Item> searchByName(String keyword, int pageNumber) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Item> getItemsByCategoryId(Long categoryId, int pageNumber) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Item> getLowStockItems(int threshold) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean updateStock(Long itemId, int newStock) throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean updateDiscount(Long itemId, double discount, int qtyToAllowDiscount) throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean existsByName(String name) throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

 

}
