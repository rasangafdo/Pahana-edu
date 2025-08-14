package com.pahanaedu.dao.custom;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import com.pahanaedu.dao.CrudDao;
import com.pahanaedu.model.Customer;

public interface CustomerDao extends CrudDao<Customer, Long> {
	Customer getCustomerByTelephone(String telephone) throws SQLException;
	
	List<Customer> getCustomersByName(String name, int pageNumber)  throws SQLException;
	
	List<Customer> getActiveCustomers(int pageNumber) throws SQLException;
	
	Customer mapResultSetToCustomer(ResultSet rs) throws SQLException;
}
