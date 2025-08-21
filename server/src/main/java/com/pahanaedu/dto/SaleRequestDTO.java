package com.pahanaedu.dto;

import java.util.List;

import com.pahanaedu.model.Customer;
import com.pahanaedu.model.SaleItem;

public class SaleRequestDTO {
    private Customer customer;
    private List<SaleItem> saleItems;
    private double paid;
    private double balance;


    public Customer getCustomer() { return customer; }
    public void setCustomer(Customer customer) { this.customer = customer; }

    public List<SaleItem> getSaleItems() { return saleItems; }
    public void setSaleItems(List<SaleItem> saleItems) { this.saleItems = saleItems; }

    public double getPaid() { return paid; }
    public void setPaid(double paid) { this.paid = paid; }

    public double getBalance() { return balance; }
    public void setBalance(double balance) { this.balance = balance; }
}
