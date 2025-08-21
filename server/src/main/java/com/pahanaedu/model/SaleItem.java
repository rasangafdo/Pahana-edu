package com.pahanaedu.model;
 
import java.time.LocalDateTime; 
 
public class SaleItem {
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long saleItemId;

    private Item item;
//    @ManyToOne
    private Long itemId;

//    @ManyToOne
    private Long saleId;

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
	public Long getItemID() {
		return itemId;
	}
	public void setItemID(Long itemId) {
		this.itemId = itemId;
	}
	public Long getSaleId() {
		return saleId;
	}
	public void setSaleId(Long saleId) {
		this.saleId = saleId;
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
    
	public void setItem(Item item) {
		this.item = item;
	}
    
	public Item getItem() {
		return item;
	}
    
}