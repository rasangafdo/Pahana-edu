package com.pahanaedu.model;
 
import java.time.*;
import java.util.List;
 
public class Sale { 
    private Long saleId;

//    @ManyToOne
    private User user;

//    @OneToMany(mappedBy = "sale")
    private List<SaleItem> items;

    private double totalAmount;
    private double totalDiscount;
    private double subTotal;
    private double paid;
    private double balance;
    private LocalDate saleDate;
    private LocalTime saleTime;
    
	public Long getSaleId() {
		return saleId;
	}
	public void setSaleId(Long saleId) {
		this.saleId = saleId;
	}
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}
	public List<SaleItem> getItems() {
		return items;
	}
	public void setItems(List<SaleItem> items) {
		this.items = items;
	}
	public double getTotalAmount() {
		return totalAmount;
	}
	public void setTotalAmount(double totalAmount) {
		this.totalAmount = totalAmount;
	}
	public double getTotalDiscount() {
		return totalDiscount;
	}
	public void setTotalDiscount(double totalDiscount) {
		this.totalDiscount = totalDiscount;
	}
	public double getSubTotal() {
		return subTotal;
	}
	public void setSubTotal(double subTotal) {
		this.subTotal = subTotal;
	}
	public double getPaid() {
		return paid;
	}
	public void setPaid(double paid) {
		this.paid = paid;
	}
	public double getBalance() {
		return balance;
	}
	public void setBalance(double balance) {
		this.balance = balance;
	}
	public LocalDate getSaleDate() {
		return saleDate;
	}
	public void setSaleDate(LocalDate saleDate) {
		this.saleDate = saleDate;
	}
	public LocalTime getSaleTime() {
		return saleTime;
	}
	public void setSaleTime(LocalTime saleTime) {
		this.saleTime = saleTime;
	}
    
    
    
    
}