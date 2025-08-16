package com.pahanaedu.dao.custom;
 
import java.util.List;

import com.pahanaedu.dao.CrudDao;
import com.pahanaedu.model.Customer;

public interface CustomerDao extends CrudDao<Customer, Long> {
	Customer getCustomerByTelephone(String telephone) throws Exception;
	
	List<Customer> getCustomersByName(String name, int pageNumber)  throws  Exception;
	
	List<Customer> getActiveCustomers(int pageNumber) throws  Exception;
	
	
	 
}
