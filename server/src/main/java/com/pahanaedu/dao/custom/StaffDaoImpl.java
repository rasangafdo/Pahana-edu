package com.pahanaedu.dao.custom;
   
import java.util.List;

import com.pahanaedu.dto.PaginatedResponse;
import com.pahanaedu.model.Customer;
import com.pahanaedu.model.Staff;  

public class StaffDaoImpl implements  StaffDao {

	@Override
	public boolean create(Staff t) throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean update(Staff t) throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean delete(Long id) throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Staff get(Long id) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PaginatedResponse<Staff> getAll(int pageNumber) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Customer getStaffByTelephone(String telephone) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Customer> getStaffsByName(String name, int pageNumber) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String login(Staff staff) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

}
