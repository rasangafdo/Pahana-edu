package com.pahanaedu.dao.custom;

import java.sql.ResultSet;
import java.sql.SQLException; 
import java.util.ArrayList;
import java.util.List;

import com.pahanaedu.dao.CrudUtil;
import com.pahanaedu.model.Customer; 
import com.pahanaedu.model.Sale; 

public class SaleDaoImpl implements SaleDao {

    @Override
    public boolean create(Sale sale) throws Exception {
        return CrudUtil.executeUpdate(
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
            "UPDATE sales SET paid_amount=?, balance=? WHERE sale_id=?",
            paidAmount,
            balance,
            saleId
        );
    }

 

    @Override
    public Sale get(Long id) throws Exception {
        ResultSet rs = CrudUtil.executeQuery("SELECT * FROM sales WHERE sale_id=?", id);
        if (rs.next()) {
            return mapResultSetToSale(rs);
        }
        return null;
    }

    @Override
    public List<Sale> getAll(int pageNumber) throws Exception {
        List<Sale> sales = new ArrayList<>();
        int offset = (pageNumber - 1) * 20;
        ResultSet rs = CrudUtil.executeQuery(
            "SELECT * FROM sales ORDER BY sale_date DESC, sale_time DESC LIMIT 20 OFFSET ?",
            offset
        );
        while (rs.next()) {
            sales.add(mapResultSetToSale(rs));
        }
        return sales;
    }
 
    @Override
    public List<Sale> getSalesByCustomer(Customer customer, int pageNumber) throws Exception {
        List<Sale> sales = new ArrayList<>();
        int offset = (pageNumber - 1) * 20;

        ResultSet rs = CrudUtil.executeQuery(
            "SELECT s.*, c.id, c.telephone " +
            "FROM sales s " +
            "JOIN customers c ON s.customer_id = c.id " +
            "WHERE c.telephone = ? " +
            "ORDER BY s.sale_date DESC, s.sale_time DESC " +
            "LIMIT 20 OFFSET ?",
            customer.getTelephone(),
            offset
        );

        while (rs.next()) { 
        	  sales.add(mapResultSetToSale(rs));
        }

        return sales;
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
}
