package model;

import java.io.Serializable;

/** Represents one product in the shop. Serializable so it can be saved to a file. */
public class Product implements Serializable {
    private static final long serialVersionUID = 1L;

    private String productId;
    private String name;
    private String category;
    private double price;
    private int quantity;
    private String supplier;
    private int minimumStock;

    public Product(String productId, String name, String category, double price,
                   int quantity, String supplier, int minimumStock) {
        this.productId = productId;
        this.name = name;
        this.category = category;
        this.price = price;
        this.quantity = quantity;
        this.supplier = supplier;
        this.minimumStock = minimumStock;
    }

    // ---------- getters ----------
    public String getProductId() { return productId; }
    public String getName() { return name; }
    public String getCategory() { return category; }
    public double getPrice() { return price; }
    public int getQuantity() { return quantity; }
    public String getSupplier() { return supplier; }
    public int getMinimumStock() { return minimumStock; }

    // ---------- setters ----------
    public void setName(String name) { this.name = name; }
    public void setCategory(String category) { this.category = category; }
    public void setPrice(double price) { this.price = price; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public void setSupplier(String supplier) { this.supplier = supplier; }
    public void setMinimumStock(int minimumStock) { this.minimumStock = minimumStock; }

    /** A product is "low stock" when quantity is at or below the minimum level. */
    public boolean isLowStock() {
        return quantity <= minimumStock;
    }

    /** Value of this product in the shop = price x quantity. */
    public double getTotalValue() {
        return price * quantity;
    }

    @Override
    public String toString() {
        return productId + " | " + name + " | " + category + " | " + price + " | " + quantity;
    }
}
