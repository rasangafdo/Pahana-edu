package com.pahanaedu.dto;

import java.util.List;

import com.pahanaedu.model.Customer;
import com.pahanaedu.model.SaleItem;

public class SaleRequestDTO {
    private Customer customer;
    private List<SaleItem> saleItems;

    public Customer getCustomer() { return customer; }
    public void setCustomer(Customer customer) { this.customer = customer; }

    public List<SaleItem> getSaleItems() { return saleItems; }
    public void setSaleItems(List<SaleItem> saleItems) { this.saleItems = saleItems; }
}
