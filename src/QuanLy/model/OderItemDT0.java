package QuanLy.model;

public class OderItemDT0 {
    private int productId;
    private int quantity;
    private double priceAtBuy;
    
    public OderItemDT0(int productId, int quantity, double priceAtBuy) {
        this.productId = productId;
        this.quantity = quantity;
        this.priceAtBuy = priceAtBuy;
    }
    public int getProductId() {
    	return this.productId;}
    public int getQuantity() {
    	return this.quantity;}    
    public double getPriceAtBuy() {
    	return this.priceAtBuy;}    
}
