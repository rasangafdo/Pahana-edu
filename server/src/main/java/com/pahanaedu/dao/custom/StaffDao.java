package com.pahanaedu.dao.custom;
 

import java.util.List;

import com.pahanaedu.dao.CrudDao;
import com.pahanaedu.model.Customer;
import com.pahanaedu.model.Staff;

public interface StaffDao extends CrudDao<Staff, Long> { 

	Customer getStaffByTelephone(String telephone) throws Exception;
	
	List<Customer> getStaffsByName(String name, int pageNumber)  throws  Exception;
	
	String login(Staff staff) throws Exception;
}
