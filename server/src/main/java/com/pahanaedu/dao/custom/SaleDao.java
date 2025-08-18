package com.pahanaedu.dao.custom;

import java.util.List;
import com.pahanaedu.dao.CrudDao;
import com.pahanaedu.model.Sale;
import com.pahanaedu.model.Customer;

public interface SaleDao extends CrudDao<Sale, Long> {
    List<Sale> getSalesByCustomer(Customer customer, int pageNumber) throws Exception;
    boolean updatePayment(Long saleId, double paidAmount, double balance) throws Exception;
}
