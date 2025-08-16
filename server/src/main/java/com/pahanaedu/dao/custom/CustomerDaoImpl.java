package com.pahanaedu.dao.custom;
 
import java.sql.ResultSet;
import java.sql.SQLException; 
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import com.pahanaedu.dao.CrudUtil;
import com.pahanaedu.model.Customer; 
import com.pahanaedu.util.Util;

public class CustomerDaoImpl implements  CustomerDao{

	@Override
	public boolean create(Customer t) throws Exception {

        return CrudUtil.executeUpdate("INSERT INTO customers (name, telephone, address,  role) VALUES (?, ?, ?,  ?)",
                t.getName(), t.getTelephone(), t.getAddress(), t.getRole().name());
		 
	}

	@Override
	public boolean update(Customer customer) throws Exception {
        // Fetch existing customer
        ResultSet rs = CrudUtil.executeQuery(
            "SELECT name, telephone, address, isActive FROM customers WHERE id = ?",
            customer.getId()
        );

        if (!rs.next()) {
            return false; // Not found
        }

        Customer oldCustomer = new Customer();
        oldCustomer.setName(rs.getString("name"));
        oldCustomer.setTelephone(rs.getString("telephone"));
        oldCustomer.setAddress(rs.getString("address"));
        oldCustomer.setIsActive(rs.getBoolean("isActive"));

        // Use old values if null/empty
        String name = Util.anyNullOrEmpty(customer.getName()) ? oldCustomer.getName() : customer.getName();
        String telephone = Util.anyNullOrEmpty(customer.getTelephone()) ? oldCustomer.getTelephone() : customer.getTelephone();
        String address = Util.anyNullOrEmpty(customer.getAddress()) ? oldCustomer.getAddress() : customer.getAddress();
        boolean isActive = Util.anyNullOrEmpty(customer.getIsActive()) ? oldCustomer.getIsActive() : customer.getIsActive();

        return CrudUtil.executeUpdate(
            "UPDATE customers SET name=?, telephone=?, address=?, isActive=? WHERE id=?",
            name, telephone, address, isActive, customer.getId()
        );
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
	 
	    try { 

	           ResultSet rs = CrudUtil.executeQuery(
	               "SELECT * FROM customers ORDER BY lastUpdated DESC LIMIT 20 OFFSET ?",
	               offset
	           );
	           while (rs.next()) {
	               customers.add(mapResultSetToCustomer(rs));
	           } 
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	    return customers;
	}

	@Override
	public Customer getCustomerByTelephone(String telephone)  { 
	        try {
	        	ResultSet rs = CrudUtil.executeQuery(
	                    "SELECT * FROM customers WHERE telephone = ?",
	                    telephone
	                );
	                if (rs.next()) {
	                    return mapResultSetToCustomer(rs);
	                }
	                return null;
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	        return null;
	}

	@Override
	public List<Customer> getCustomersByName(String name, int pageNumber) {
		  List<Customer> customers = new ArrayList<>();
	        int offset = (pageNumber - 1) * 20;
	        
	        try { 
	            ResultSet rs = CrudUtil.executeQuery(
	                "SELECT * FROM customers WHERE LOWER(name) LIKE ? ORDER BY lastUpdated DESC LIMIT 20 OFFSET ?",
	                "%" + name.toLowerCase() + "%", offset
	            );
	            while (rs.next()) {
	                customers.add(mapResultSetToCustomer(rs));
	            }
	            return customers;
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	        return customers;
	}

	@Override
	public List<Customer> getActiveCustomers(int pageNumber) {
		 List<Customer> customers = new ArrayList<>();
	        int offset = (pageNumber - 1) * 20;
	       
	        try {

	            ResultSet rs = CrudUtil.executeQuery(
	                "SELECT * FROM customers WHERE isActive = TRUE ORDER BY lastUpdated DESC LIMIT 20 OFFSET ?",
	                offset
	            );
	            while (rs.next()) {
	                customers.add(mapResultSetToCustomer(rs));
	            }
	            return customers;
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	        return customers;
	}
 
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
