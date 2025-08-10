package com.pahanaedu.dao;

import com.pahanaedu.model.Customer;
import com.pahanaedu.util.DBConnection;
import com.pahanaedu.util.Util;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CustomerDAO {

    // Get all customers
	public List<Customer> getAllCustomers(int pageNumber) {
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

    // Get customer by telephone
    public Customer getCustomerByTelephone(String telephone) {
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

    // Search customers by name (partial match)
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

    // Get active customers 
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

    // Create a new customer (returns generated id)
    public long createCustomer(Customer customer) {
        String sql = "INSERT INTO customers (name, telephone, address,  role) VALUES (?, ?, ?,  ?)";
        long generatedId = -1;

        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, customer.getName());
            stmt.setString(2, customer.getTelephone());
            stmt.setString(3, customer.getAddress()); 
            stmt.setString(4, customer.getRole().name());

            int affectedRows = stmt.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Creating customer failed, no rows affected.");
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    generatedId = generatedKeys.getLong(1);
                    customer.setId(generatedId); // update model with generated id
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return generatedId;
    }

    // Get customer by ID
    public Customer getCustomerById(Long id) {
        String sql = "SELECT * FROM customers WHERE id = ?";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, id);
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

    // Update existing customer
    public boolean updateCustomer(Customer customer) {
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


 
 


    // Helper method to map ResultSet row to Customer object
    private Customer mapResultSetToCustomer(ResultSet rs) throws SQLException {
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
