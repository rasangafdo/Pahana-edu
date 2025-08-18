package com.pahanaedu.dao.custom;

import com.pahanaedu.dao.CrudUtil;
import com.pahanaedu.model.Item;
import com.pahanaedu.model.Sale;
import com.pahanaedu.model.SaleItem; 

import java.sql.*; 
import java.util.ArrayList;
import java.util.List;

public class SaleItemDaoImpl implements SaleItemDao {

    @Override
    public boolean create(SaleItem t) throws Exception {
      return CrudUtil.executeUpdate("INSERT INTO sale_items (sale_id, item_id, qty, discount_amount, item_total) " +
           "VALUES (?, ?, ?, ?, ?)", t.getSale().getSaleId(),t.getItem().getItemId(),t.getQty(),t.getDiscountAmount(),t.getItemTotal());
             
    }
 
 

    @Override
    public SaleItem get(Long id) throws Exception {  
            ResultSet rs = CrudUtil.executeQuery("SELECT * FROM sale_items WHERE sale_item_id=?", id);
            if (rs.next()) {
                return mapResultSetToSaleItem(rs);
            } 
        return null;
    }


    @Override
    public List<SaleItem> getItemsBySale(Sale sale) throws Exception {
        List<SaleItem> list = new ArrayList<>(); 
            ResultSet rs = CrudUtil.executeQuery("SELECT * FROM sale_items WHERE sale_id=?",sale.getSaleId());
            while (rs.next()) {
                list.add(mapResultSetToSaleItem(rs));
            }
        
        return list;
    }

    private SaleItem mapResultSetToSaleItem(ResultSet rs) throws Exception {
        SaleItem si = new SaleItem();
        si.setSaleItemId(rs.getLong("sale_item_id"));

        Item item = new Item();
        item.setItemId(rs.getLong("item_id"));
        si.setItem(item);

        Sale sale = new Sale();
        sale.setSaleId(rs.getLong("sale_id"));
        si.setSale(sale);

        si.setQty(rs.getInt("qty"));
        si.setDiscountAmount(rs.getDouble("discount_amount"));
        si.setItemTotal(rs.getDouble("item_total"));

        Timestamp ts = rs.getTimestamp("last_updated_at");
        si.setLastUpdatedAt(ts != null ? ts.toLocalDateTime() : null);

        return si;
    }



	@Override
	public boolean update(SaleItem t) throws Exception {
		// TODO Auto-generated method stub
		return false;
	}



	@Override
	public boolean delete(Long id) throws Exception {
		// TODO Auto-generated method stub
		return false;
	}



	@Override
	public List<SaleItem> getAll(int pageNumber) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}
}
