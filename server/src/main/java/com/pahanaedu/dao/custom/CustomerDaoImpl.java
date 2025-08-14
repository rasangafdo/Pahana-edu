package com.pahanaedu.dao.custom;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException; 
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import com.pahanaedu.dao.CrudUtil;
import com.pahanaedu.model.Customer;
import com.pahanaedu.util.DBConnection;
import com.pahanaedu.util.Util;

public class CustomerDaoImpl implements  CustomerDao{

	@Override
	public boolean create(Customer t) throws Exception {

        return CrudUtil.executeUpdate("INSERT INTO customers (name, telephone, address,  role) VALUES (?, ?, ?,  ?)",
                t.getName(), t.getTelephone(), t.getAddress(), t.getRole().name());
		 
	}

	@Override
	public boolean update(Customer customer) throws Exception {
        String selectSql = "SELECT name, telephone, address, isActive FROM customers WHERE id = ?";
        String updateSql = "UPDATE customers SET name=?, telephone=?, address=?, isActive=? WHERE id=?";

        try (Connection conn = DBConnection.getInstance().getConnection()) {
            // 1. Fetch current customer data
            Customer oldCustomer = null;
            try (PreparedStatement selectStmt = conn.prepareStatement(selectSql)) {
                selectStmt.setLong(1, customer.getId());
                try (ResultSet rs = selectStmt.executeQuery()) {
                    if (rs.next()) {
                        oldCustomer = new Customer();
                        oldCustomer.setName(rs.getString("name"));
                        oldCustomer.setTelephone(rs.getString("telephone"));
                        oldCustomer.setAddress(rs.getString("address"));
                        oldCustomer.setIsActive(rs.getBoolean("isActive"));
                    } else {
                        // Customer not found
                        return false;
                    }
                }
            }

            // 2. Use old values if input is null or empty
            String name = Util.anyNullOrEmpty(customer.getName()) ? oldCustomer.getName() : customer.getName();
            String telephone = Util.anyNullOrEmpty(customer.getTelephone()) ? oldCustomer.getTelephone() : customer.getTelephone();
            String address = Util.anyNullOrEmpty(customer.getAddress()) ? oldCustomer.getAddress() : customer.getAddress();
            boolean isActive = Util.anyNullOrEmpty(customer.getIsActive()) ? oldCustomer.getIsActive() : customer.getIsActive();

            // 3. Update with final values
            try (PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
                updateStmt.setString(1, name);
                updateStmt.setString(2, telephone);
                updateStmt.setString(3, address);
                updateStmt.setBoolean(4, isActive);
                updateStmt.setLong(5, customer.getId());

                int affectedRows = updateStmt.executeUpdate();
                return affectedRows > 0;
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
	}

	@Override
	public boolean delete(Long id) throws Exception {

        return CrudUtil.executeUpdate("DELETE FROM customers WHERE id = ?",
                id);
		
	}


	@Override
	public Customer get(Long id) throws Exception {
		ResultSet rs = CrudUtil.executeQuery("SELECT * FROM customers WHERE id = ?", id);
		
	    if (rs.next()) {
	          return mapResultSetToCustomer(rs);
	        }
	        return null;
	}

	@Override
	public List<Customer> getAll(int pageNumber) {
	    List<Customer> customers = new ArrayList<>();
	    int offset = (pageNumber - 1) * 20;
	    String sql = "SELECT * FROM customers ORDER BY lastUpdated DESC LIMIT 20 OFFSET ?";

	    try (Connection conn = DBConnection.getInstance().getConnection();
	         PreparedStatement stmt = conn.prepareStatement(sql)) {

	        stmt.setInt(1, offset);
	        try (ResultSet rs = stmt.executeQuery()) {
	            while (rs.next()) {
	                customers.add(mapResultSetToCustomer(rs));
	            }
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return customers;
	}

	@Override
	public Customer getCustomerByTelephone(String telephone)  {
		 String sql = "SELECT * FROM customers WHERE telephone = ?";
	        try (Connection conn = DBConnection.getInstance().getConnection();
	             PreparedStatement stmt = conn.prepareStatement(sql)) {

	            stmt.setString(1, telephone);
	            try (ResultSet rs = stmt.executeQuery()) {
	                if (rs.next()) {
	                    return mapResultSetToCustomer(rs);
	                }
	            }
	        } catch (SQLException e) {
	            e.printStackTrace();
	        }
	        return null;
	}

	@Override
	public List<Customer> getCustomersByName(String name, int pageNumber) {
		  List<Customer> customers = new ArrayList<>();
	        int offset = (pageNumber - 1) * 20;
	        String sql = "SELECT * FROM customers WHERE LOWER(name) LIKE ? ORDER BY lastUpdated DESC LIMIT 20 OFFSET ?";

	        try (Connection conn = DBConnection.getInstance().getConnection();
	             PreparedStatement stmt = conn.prepareStatement(sql)) {

	            stmt.setString(1, "%" + name.toLowerCase() + "%");
	            stmt.setInt(2, offset);

	            try (ResultSet rs = stmt.executeQuery()) {
	                while (rs.next()) {
	                    customers.add(mapResultSetToCustomer(rs));
	                }
	            }
	        } catch (SQLException e) {
	            e.printStackTrace();
	        }
	        return customers;
	}

	@Override
	public List<Customer> getActiveCustomers(int pageNumber) {
		 List<Customer> customers = new ArrayList<>();
	        int offset = (pageNumber - 1) * 20;
	        String sql = "SELECT * FROM customers WHERE isActive = TRUE ORDER BY lastUpdated DESC LIMIT 20 OFFSET ?";

	        try (Connection conn = DBConnection.getInstance().getConnection();
	             PreparedStatement stmt = conn.prepareStatement(sql)) {
	 
	            stmt.setInt(1, offset);

	            try (ResultSet rs = stmt.executeQuery()) {
	                while (rs.next()) {
	                    customers.add(mapResultSetToCustomer(rs));
	                }
	            }
	        } catch (SQLException e) {
	            e.printStackTrace();
	        }
	        return customers;
	}

	@Override
	public Customer mapResultSetToCustomer(ResultSet rs) throws SQLException {
		 Customer customer = new Customer();
	        customer.setId(rs.getLong("id"));
	        customer.setName(rs.getString("name"));
	        customer.setTelephone(rs.getString("telephone"));
	        customer.setAddress(rs.getString("address"));
	        customer.setIsActive(rs.getBoolean("isActive"));
	        Timestamp ts = rs.getTimestamp("lastUpdated");
	        if (ts != null) {
	            customer.setLastUpdated(ts.toLocalDateTime());
	        } 
	        return customer;
	}

}
