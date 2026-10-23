package model;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/** One purchase or sale of a product. */
public class Transaction implements Serializable {
    private static final long serialVersionUID = 1L;

    public enum Type { PURCHASE, SALE }

    private String transactionId;
    private String productId;
    private String productName;
    private Type type;
    private int quantity;
    private double unitPrice;
    private LocalDate date;

    public Transaction(String transactionId, String productId, String productName,
                       Type type, int quantity, double unitPrice) {
        this.transactionId = transactionId;
        this.productId = productId;
        this.productName = productName;
        this.type = type;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.date = LocalDate.now();
    }

    public String getTransactionId() { return transactionId; }
    public String getProductId() { return productId; }
    public String getProductName() { return productName; }
    public Type getType() { return type; }
    public int getQuantity() { return quantity; }
    public double getUnitPrice() { return unitPrice; }
    public LocalDate getDate() { return date; }

    public double getTotalAmount() {
        return unitPrice * quantity;
    }

    /** Date as dd-MM-yyyy, e.g. 01-10-2026 */
    public String getDateString() {
        return date.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
    }
}
