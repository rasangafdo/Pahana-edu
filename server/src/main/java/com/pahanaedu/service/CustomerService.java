package com.pahanaedu.service;

import java.util.List;

import com.pahanaedu.dao.DaoFactory;
import com.pahanaedu.dao.custom.CustomerDaoImpl;
import com.pahanaedu.dto.PaginatedResponse;
import com.pahanaedu.model.Customer;

public class CustomerService {
	  private final CustomerDaoImpl customerDao;

	    public CustomerService() {
	        this.customerDao = (CustomerDaoImpl) DaoFactory.getInstance().getDao(DaoFactory.DaoTypes.CUSTOMER);
	    }

	     
	    public PaginatedResponse<Customer> getAll(int page) throws Exception {
	        return customerDao.getAll(page);
	    }
 
	    public Customer get(Long id) throws Exception {
	        return customerDao.get(id);
	    }
 
	    public List<Customer> getCustomersByName(String name, int pageNumber) throws Exception {
	        return customerDao.getCustomersByName(name,pageNumber);
	    }
	    
	    public List<Customer> getActiveCustomers(int page){
	    	return customerDao.getActiveCustomers(page);
	    }
	    
	    public Customer getCustomerByTelephone(String name) {
	    	return customerDao.getCustomerByTelephone(name);
	    }
	    
	    // Create new customer (with duplicate check)
	    public boolean create(Customer customer) throws Exception {
	        if (customerDao.getCustomerByTelephone(customer.getTelephone())!= null) {
	            throw new IllegalStateException("Customer already exists");
	        }
	        return customerDao.create(customer);
	    }

	    // Update existing
	    public boolean update(Customer customer) throws Exception {
	    	if(customerDao.get(customer.getId()) == null) {
	            throw new IllegalStateException("Customer not found");
	    	}
	        if (customerDao.existsByTeleExcludingId(customer.getTelephone(), customer.getId())) {
	            throw new IllegalStateException("Another customer with this telephone number already exists");
	        }
	        return customerDao.update(customer);
	    }

	    
	    
}
