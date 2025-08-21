package com.pahanaedu.dao.custom;
 
 

import com.pahanaedu.dao.CrudDao; 
import com.pahanaedu.model.Staff;

public interface StaffDao extends CrudDao<Staff, Long> { 
 
	Staff getStaffByUserName(String username)  throws  Exception;
	 
}
