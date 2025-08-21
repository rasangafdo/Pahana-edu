package com.pahanaedu.dao.custom;

import java.sql.ResultSet;
import java.sql.SQLException; 
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map; 

import com.pahanaedu.dao.CrudUtil;
import com.pahanaedu.dto.PaginatedResponse;
import com.pahanaedu.model.Customer;
import com.pahanaedu.model.Item;
import com.pahanaedu.model.Sale;
import com.pahanaedu.model.SaleItem; 

public class SaleDaoImpl implements SaleDao {
 
    public long  createSale(Sale sale) throws Exception {
    	return  CrudUtil.executeInsert(
    		 "INSERT INTO sales (customer_id, total_amount, total_discount, sub_total, paid, balance) VALUES (?, ?, ?, ?, ?, ?)",
            sale.getCustomerId(),  
            sale.getTotalAmount(),
            sale.getTotalDiscount(),
            sale.getSubTotal(),
            sale.getPaid(),
            sale.getBalance() 
        );
    }

    public boolean updatePayment(Long saleId, double paidAmount, double balance) throws Exception {
        return CrudUtil.executeUpdate(
            "UPDATE sales SET paid=?, balance=? WHERE sale_id=?",
            paidAmount,
            balance,
            saleId
        );
    }

 

    @Override
    public Sale get(Long id) throws Exception {
        String sql = " SELECT  s.*, c.name AS customer_name, c.telephone, si.sale_item_id, si.item_id,"+
                "si.qty,  si.discount_amount, si.item_total, i.name AS item_name, i.unit_price,"+
                "i.category_id FROM sales s JOIN customers c ON s.customer_id = c.id LEFT JOIN sale_items si "+
                "ON s.sale_id = si.sale_id LEFT JOIN item i ON si.item_id = i.item_id WHERE s.sale_id = ?";


        ResultSet rs = CrudUtil.executeQuery(sql, id);

        Map<Long, Sale> saleMap = new HashMap<>();
        while (rs.next()) {
            mapDetailedResultSetToSale(rs, saleMap);
        }

        // since it's get(id), there should be only one sale
        return saleMap.values().stream().findFirst().orElse(null);
    }


    @Override
    public PaginatedResponse<Sale> getAll(int pageNumber) throws Exception {
        List<Sale> sales = new ArrayList<>();
	    int pageSize = 20;
	    int offset = (pageNumber - 1) * pageSize;
	    int totalCount = 0;
	    int totalPages = 0; 
        ResultSet rs = CrudUtil.executeQuery(
                "SELECT s.*, c.name AS customer_name, c.telephone, " +
                        "       si.sale_item_id, si.item_id, si.qty, si.discount_amount, si.item_total, " +
                		"i.name AS item_name, i.unit_price, i.category_id," +
                        "       COUNT(*) OVER() AS total_count " +
                        "FROM sales s " +
                        "JOIN customers c ON s.customer_id = c.id " +
                        "LEFT JOIN sale_items si ON s.sale_id = si.sale_id " + 
                        "LEFT JOIN item i ON si.item_id = i.item_id " +
                        "ORDER BY s.sale_date DESC, s.sale_time DESC " +
                        "LIMIT ? OFFSET ?",
            pageSize, offset
        );
	Map<Long, Sale> saleMap = new LinkedHashMap<>();


        while (rs.next()) {
            mapDetailedResultSetToSale(rs, saleMap);

            if (totalCount == 0) {
                totalCount = rs.getInt("total_count");
            }
        }

        sales.addAll(saleMap.values());
        
        totalPages = (int) Math.ceil((double) totalCount / pageSize);


        return new PaginatedResponse<Sale>(sales, totalPages, totalCount);
    }
 
    @Override
    public List<Sale> getRecentSales(int limit) throws Exception {
        List<Sale> sales = new ArrayList<>();

        ResultSet rs = CrudUtil.executeQuery(
            " SELECT  s.*, c.name AS customer_name, c.telephone, si.sale_item_id, si.item_id,"+
                     "si.qty,  si.discount_amount, si.item_total, i.name AS item_name, i.unit_price,"+
                     "i.category_id FROM sales s JOIN customers c ON s.customer_id = c.id LEFT JOIN sale_items si "+
                     "ON s.sale_id = si.sale_id LEFT JOIN item i ON si.item_id = i.item_id "+
                     " ORDER BY sale_date DESC, sale_time DESC LIMIT ?",
            limit
        );

        // 🟢 Map sales + group items
         Map<Long, Sale> saleMap = new LinkedHashMap<>();


        while (rs.next()) {
            mapDetailedResultSetToSale(rs, saleMap);
 
        }

        sales.addAll(saleMap.values());

        return sales;
    }

    
    @Override 
    public PaginatedResponse<Sale> getSalesByCustomer(Customer customer, int pageNumber) throws Exception {
        List<Sale> sales = new ArrayList<>();
        int pageSize = 20;
        int offset = (pageNumber - 1) * pageSize;
        int totalCount = 0;
        int totalPages = 0;

        ResultSet rs = CrudUtil.executeQuery(
            "SELECT s.*, c.name AS customer_name, c.telephone, " +
            "       si.sale_item_id, si.item_id, si.qty, si.discount_amount, si.item_total, "  +
    		"i.name AS item_name, i.unit_price, i.category_id," +
            "       COUNT(*) OVER() AS total_count " +
            "FROM sales s " +
            "JOIN customers c ON s.customer_id = c.id " +
            "LEFT JOIN sale_items si ON s.sale_id = si.sale_id " +
            "LEFT JOIN item i ON si.item_id = i.item_id " +
            "WHERE c.telephone = ? " +
            "ORDER BY s.sale_date DESC, s.sale_time DESC " +
            "LIMIT ? OFFSET ?",
            customer.getTelephone(),
            pageSize,
            offset
        );

        // 🟢 Map sales + group items
         Map<Long, Sale> saleMap = new LinkedHashMap<>();


        while (rs.next()) {
            mapDetailedResultSetToSale(rs, saleMap);

            if (totalCount == 0) {
                totalCount = rs.getInt("total_count");
            }
        }

        sales.addAll(saleMap.values());
        
        totalPages = (int) Math.ceil((double) totalCount / pageSize);

        return new PaginatedResponse<Sale>(sales, totalPages, totalCount);
    }


    private Sale mapDetailedResultSetToSale(ResultSet rs, java.util.Map<Long, Sale> saleMap) throws SQLException {
        long saleId = rs.getLong("sale_id");

        // 🟢 Get or create Sale
        Sale sale = saleMap.get(saleId);
        if (sale == null) {
            sale = mapResultSetToSale(rs);
            sale.setCustomerName(rs.getString("customer_name"));
            sale.setSaleItems(new ArrayList<>());
            saleMap.put(saleId, sale);
        }

        // 🟢 Map SaleItem if present
        long saleItemId = rs.getLong("sale_item_id");
        if (saleItemId != 0) {
            SaleItem saleItem = new SaleItem();
            saleItem.setSaleItemId(saleItemId);
            saleItem.setSaleId(saleId);
            saleItem.setItemID(rs.getLong("item_id"));
            saleItem.setQty(rs.getInt("qty"));
            saleItem.setDiscountAmount(rs.getDouble("discount_amount"));
            saleItem.setItemTotal(rs.getDouble("item_total"));
            saleItem.setLastUpdatedAt(rs.getTimestamp("last_updated_at").toLocalDateTime());

            // 🟢 Map Item
            Item mappedItem = new Item();
            mappedItem.setItemId(rs.getLong("item_id"));
            mappedItem.setName(rs.getString("item_name"));
            mappedItem.setCategoryId(rs.getLong("category_id"));
            mappedItem.setUnitPrice(rs.getDouble("unit_price"));

            saleItem.setItem(mappedItem);

            sale.getSaleItems().add(saleItem);
        }

        return sale;
    }


    private Sale mapResultSetToSale(ResultSet rs) throws SQLException {
        Sale sale = new Sale();
        sale.setSaleId(rs.getLong("sale_id"));
 
        sale.setCustomerId(rs.getLong("customer_id")); 

        sale.setTotalAmount(rs.getDouble("total_amount"));
        sale.setTotalDiscount(rs.getDouble("total_discount"));
        sale.setSubTotal(rs.getDouble("sub_total"));
        sale.setPaid(rs.getDouble("paid"));
        sale.setBalance(rs.getDouble("balance"));
        sale.setSaleDate(rs.getDate("sale_date").toLocalDate());
        sale.setSaleTime(rs.getTime("sale_time").toLocalTime());
        sale.setLastUpdatedAt(rs.getTimestamp("last_updated_at").toLocalDateTime());

        return sale;
    }

	@Override
	public boolean update(Sale t) throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean delete(Long id) throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean create(Sale t) throws Exception {
		// TODO Auto-generated method stub
		return false;
	}
}
