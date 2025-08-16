package com.pahanaedu.model;
 
import java.time.LocalDateTime; 
 
public class SaleItem {
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long saleItemId;

//    @ManyToOne
    private Item item;

//    @ManyToOne
    private Sale sale;

    private int qty;
    private double discountAmount;
    private double itemTotal;
    private LocalDateTime lastUpdatedAt;
	public Long getSaleItemId() {
		return saleItemId;
	}
	public void setSaleItemId(Long saleItemId) {
		this.saleItemId = saleItemId;
	}
	public Item getItem() {
		return item;
	}
	public void setItem(Item item) {
		this.item = item;
	}
	public Sale getSale() {
		return sale;
	}
	public void setSale(Sale sale) {
		this.sale = sale;
	}
	public int getQty() {
		return qty;
	}
	public void setQty(int qty) {
		this.qty = qty;
	}
	public double getDiscountAmount() {
		return discountAmount;
	}
	public void setDiscountAmount(double discountAmount) {
		this.discountAmount = discountAmount;
	}
	public double getItemTotal() {
		return itemTotal;
	}
	public void setItemTotal(double itemTotal) {
		this.itemTotal = itemTotal;
	}
	public LocalDateTime getLastUpdatedAt() {
		return lastUpdatedAt;
	}
	public void setLastUpdatedAt(LocalDateTime lastUpdatedAt) {
		this.lastUpdatedAt = lastUpdatedAt;
	}
    
    
    
}