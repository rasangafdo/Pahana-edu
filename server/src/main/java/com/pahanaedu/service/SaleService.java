package com.pahanaedu.service;

import com.pahanaedu.dao.custom.SaleDaoImpl;
import com.pahanaedu.model.Customer;
import com.pahanaedu.model.Item;
import com.pahanaedu.model.Sale;
import com.pahanaedu.model.SaleItem;
import com.pahanaedu.util.DBConnection;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import com.pahanaedu.dao.DaoFactory;
import com.pahanaedu.dao.custom.*;

public class SaleService {
    private final SaleDaoImpl saleDAO = (SaleDaoImpl) DaoFactory.getInstance().getDao(DaoFactory.DaoTypes.SALE);
    private final SaleItemDaoImpl saleItemDAO = (SaleItemDaoImpl) DaoFactory.getInstance().getDao(DaoFactory.DaoTypes.SALEITEM);
    private final ItemDaoImpl itemDAO  = (ItemDaoImpl) DaoFactory.getInstance().getDao(DaoFactory.DaoTypes.ITEM);
    private final CustomerDaoImpl customerDAO  = (CustomerDaoImpl) DaoFactory.getInstance().getDao(DaoFactory.DaoTypes.CUSTOMER);

    public Sale createSale(Customer customerInput, List<SaleItem> saleItems) throws Exception {
        Connection con = null;
        try {
        	 // 1 Find or create customer
            Customer customer = customerDAO.getCustomerByTelephone(customerInput.getTelephone());
            if (customer == null) {
                // Ensure required fields
                if (customerInput.getName() == null || customerInput.getAddress() == null) {
                    throw new Exception("Customer details are incomplete");
                }
                Boolean isCreated = customerDAO.create(customerInput);
                if (!isCreated) {
                    throw new Exception("Failed to create customer");
                }
                customer = customerDAO.getCustomerByTelephone(customerInput.getTelephone()); // reload with id
            }
            
            
            con = DBConnection.getInstance().getConnection();
            con.setAutoCommit(false); // 🔹 start transaction

           

            // 2 Prepare Sale
            Sale sale = new Sale();
            sale.setCustomerId(customer.getId()); 

            BigDecimal total = BigDecimal.ZERO;
            BigDecimal subTotal = BigDecimal.ZERO;
            BigDecimal totalDiscount = BigDecimal.ZERO;

            // 3 Process Sale Items
            for (SaleItem si : saleItems) {
                Item item = itemDAO.get(si.getItem().getItemId());
                if (item == null) throw new Exception("Item not found " + si.getItem().getItemId());

                if (item.getStockAvailable() < si.getQty()) {
                    throw new Exception("Insufficient stock for " + item.getName());
                }

                BigDecimal price = BigDecimal.valueOf(item.getUnitPrice());
                BigDecimal discount = BigDecimal.ZERO;

                if (si.getQty() >= item.getQtyToAllowDiscount()) {
                    discount = BigDecimal.valueOf(item.getDiscount());
                }

                BigDecimal lineTotal = (price.subtract(discount))
                    .multiply(BigDecimal.valueOf(si.getQty()));

                si.setItem(item);
                si.setDiscountAmount(discount.doubleValue());
                si.setItemTotal(lineTotal.doubleValue());

                subTotal = subTotal.add(price.multiply(BigDecimal.valueOf(si.getQty())));
                totalDiscount = totalDiscount.add(discount.multiply(BigDecimal.valueOf(si.getQty())));
                total = total.add(lineTotal);

                // 🔹 reduce stock immediately
                itemDAO.updateStock(item.getItemId(), item.getStockAvailable() - si.getQty());
            }

            // 4 Save Sale
            sale.setSubTotal(subTotal.doubleValue());
            sale.setTotalDiscount(totalDiscount.doubleValue());
            sale.setTotalAmount(total.doubleValue());
            sale.setPaid(0.0);     // You can adjust later
            sale.setBalance(total.doubleValue()); // assuming not paid yet

            boolean saleSaved = saleDAO.create(sale);
            if (!saleSaved) throw new Exception("Failed to save sale");

            // 5 Save Sale Items
            for (SaleItem si : saleItems) {
                si.setSale(sale);
                boolean itemSaved = saleItemDAO.create(si);
                if (!itemSaved) throw new Exception("Failed to save sale item");
            }

            con.commit(); //  commit all
            return sale;

        } catch (Exception e) {
            if (con != null) {
                try { con.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            }
            throw e; // rethrow to servlet
        } finally {
            if (con != null) {
                try { con.setAutoCommit(true); con.close(); } catch (SQLException ex) { ex.printStackTrace(); }
            }
        }
    }
}
