package com.pahanaedu.dao.custom;

import java.sql.ResultSet;
import java.sql.SQLException; 
import java.util.ArrayList;
import java.util.List;

import com.pahanaedu.dao.CrudUtil;
import com.pahanaedu.model.Customer; 
import com.pahanaedu.model.Sale;
import com.pahanaedu.util.Util;

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

    @Override
    public boolean update(Sale sale) throws Exception {
        
        ResultSet rs = CrudUtil.executeQuery(
                "SELECT * FROM sales WHERE sale_id = ?",
                sale.getSaleId()
            );

            if (!rs.next()) {
                return false; // not found
            }

            Sale oldSale = mapResultSetToSale(rs);

            // Use old values if null/empty
            Long customerId =  Util.anyNullOrEmpty(sale.getCustomerId())  ? oldSale.getCustomerId() : sale.getCustomerId();
            Double totalAmount = Util.anyNullOrEmpty(sale.getTotalAmount()) ? oldSale.getTotalAmount() : sale.getTotalAmount();
            Double totalDiscount = Util.anyNullOrEmpty(sale.getTotalDiscount()) ? oldSale.getTotalDiscount() : oldSale.getTotalDiscount();
            Double subTotal = Util.anyNullOrEmpty(sale.getSubTotal())  ? oldSale.getSubTotal() : oldSale.getSubTotal();
            Double paid = Util.anyNullOrEmpty(sale.getPaid()) ? oldSale.getPaid() : oldSale.getPaid();
            Double balance = Util.anyNullOrEmpty(sale.getBalance()) ? oldSale.getBalance() : oldSale.getBalance();
                       
            return CrudUtil.executeUpdate(
            "UPDATE sales SET customer_id=?, total_amount=?, total_discount=?, sub_total=?, paid=?, balance=? WHERE sale_id=?",
            customerId,totalAmount,totalDiscount,subTotal,paid,balance,
            sale.getSaleId()
        );
    }

    @Override
    public boolean delete(Long id) throws Exception {
        return CrudUtil.executeUpdate("DELETE FROM sales WHERE sale_id=?", id);
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
            "SELECT * FROM sales WHERE customer_id=? ORDER BY sale_date DESC, sale_time DESC LIMIT 20 OFFSET ?",
            customer.getId(),
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
}
