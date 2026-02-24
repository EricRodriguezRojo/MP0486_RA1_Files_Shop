package model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

@Entity
@Table(name = "inventory")
public class Product {
    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    
    @Column(name = "name")
    private String name;
    
    @Column(name = "price")
    private double price; 
    
    @Transient
    private Amount publicPrice;
    
    @Transient
    private Amount wholesalerPrice;
    
    @Column(name = "available")
    private boolean available;
    
    @Column(name = "stock")
    private int stock;
    
    @Transient
    private static int totalProducts;
    
    @Transient
    public final static double EXPIRATION_RATE=0.60;
    
    public Product() {
        totalProducts++;
    }
    
    public Product(String name, Amount wholesalerPrice, boolean available, int stock) {
        this();
        this.name = name;
        this.wholesalerPrice = wholesalerPrice;
        this.publicPrice = new Amount(wholesalerPrice.getValue() * 2);
        this.available = available;
        this.stock = stock;
        this.price = this.publicPrice.getValue();
    }
    
    public Amount getPublicPrice() {
        if (this.publicPrice == null) this.publicPrice = new Amount(this.price);
        return publicPrice;
    }

    public Amount getWholesalerPrice() {
        if (this.wholesalerPrice == null) this.wholesalerPrice = new Amount(this.price / 2.0);
        return wholesalerPrice;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public double getPrice() { return this.price; }
    
    public void setPrice(double price) {
        this.price = price;
        this.publicPrice = new Amount(price);
        this.wholesalerPrice = new Amount(price / 2.0);
    }

    public void setPublicPrice(Amount publicPrice) {
        this.publicPrice = publicPrice;
        if (publicPrice != null) this.price = publicPrice.getValue();
    }

    public void setWholesalerPrice(Amount wholesalerPrice) {
        this.wholesalerPrice = wholesalerPrice;
        if (wholesalerPrice != null) {
            this.publicPrice = new Amount(wholesalerPrice.getValue() * 2);
            this.price = this.publicPrice.getValue();
        }
    }

    public boolean isAvailable() { return available; }
    public void setAvailable(boolean available) { this.available = available; }
    public int getStock() { return stock; }
    public void setStock(int stock) { this.stock = stock; }
    public static int getTotalProducts() { return totalProducts; }
    public static void setTotalProducts(int totalProducts) { Product.totalProducts = totalProducts; }
    
    public void expire() {
        this.getPublicPrice().setValue(this.getPublicPrice().getValue() * EXPIRATION_RATE); 
        this.price = this.publicPrice.getValue();
    }

    @Override
    public String toString() {
        return "Product [id=" + id + ", name=" + name + ", publicPrice=" + getPublicPrice() + 
               ", wholesalerPrice=" + getWholesalerPrice() + ", available=" + available + ", stock=" + stock + "]";
    }
}