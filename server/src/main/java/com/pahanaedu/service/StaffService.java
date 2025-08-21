package com.pahanaedu.service; 

import java.util.List;

import com.pahanaedu.dao.DaoFactory; 
import com.pahanaedu.dao.custom.StaffDaoImpl;
import com.pahanaedu.dto.LoginDto;
import com.pahanaedu.model.Staff; 
import com.pahanaedu.util.PasswordHash;

public class StaffService {

	private final StaffDaoImpl staffDAO = (StaffDaoImpl) DaoFactory.getInstance().getDao(DaoFactory.DaoTypes.STAFF);

	
	public List<Staff> getAllStaff() throws Exception {
		 List<Staff> staff = staffDAO.getAllStaff(); 
		 return staff;
	}

	
	public Staff getStaffUser(String username) throws Exception {
		 Staff staff = staffDAO.getStaffByUserName(username);
		 staff.setPassword(null);
		 return staff;
	}
	
	public Staff verifyPassword(LoginDto loginRequest ) throws Exception {

        Staff staff = staffDAO.getStaffByUserName(loginRequest.getUsername());


       if(staff != null && PasswordHash.verifyPassword(loginRequest.getPassword(), staff.getPassword())) {
    	   return staff;
       }
        	return null;
	}
	
	public boolean createStaff(Staff staff) throws Exception {
		return staffDAO.create(staff);
	}
	
	public boolean delete(Long id) throws Exception {
		 return staffDAO.delete(id);
	}
	
	public boolean updateStaff(Staff staff) throws Exception {
	    return staffDAO.update(staff);
	}

}
