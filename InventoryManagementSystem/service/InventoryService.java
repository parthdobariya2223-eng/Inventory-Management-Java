package service;

import exception.InsufficientStockException;
import exception.InvalidProductException;
import model.Product;
import model.Transaction;

import java.util.ArrayList;

/** Stock logic: purchase, sell, low-stock detection, inventory value, totals. */
public class InventoryService {
    private final ProductService productService;
    private final TransactionService transactionService;

    public InventoryService(ProductService productService, TransactionService transactionService) {
        this.productService = productService;
        this.transactionService = transactionService;
    }

    // ---------------- purchase (add stock) ----------------
    public Product purchaseStock(String id, String qtyText) throws InvalidProductException {
        int qty = parseQuantity(qtyText);
        synchronized (productService) {                 // one thread at a time
            Product p = findProduct(id);
            p.setQuantity(p.getQuantity() + qty);       // 10 + 20 = 30
            transactionService.record(p, Transaction.Type.PURCHASE, qty);
            productService.save();
            transactionService.save();
            return p;
        }
    }

    // ---------------- sale (remove stock) ----------------
    public Product sellStock(String id, String qtyText)
            throws InvalidProductException, InsufficientStockException {
        int qty = parseQuantity(qtyText);
        synchronized (productService) {
            Product p = findProduct(id);
            if (qty > p.getQuantity()) {
                throw new InsufficientStockException(
                        "Insufficient Stock!\nAvailable Stock: " + p.getQuantity());
            }
            p.setQuantity(p.getQuantity() - qty);       // 30 - 5 = 25
            transactionService.record(p, Transaction.Type.SALE, qty);
            productService.save();
            transactionService.save();
            return p;
        }
    }

    private Product findProduct(String id) throws InvalidProductException {
        if (id.trim().isEmpty()) throw new InvalidProductException("Product ID cannot be empty");
        Product p = productService.findById(id);
        if (p == null) throw new InvalidProductException("Product not found: " + id.trim());
        return p;
    }

    private int parseQuantity(String text) throws InvalidProductException {
        if (text.trim().isEmpty()) throw new InvalidProductException("Quantity cannot be empty");
        int qty;
        try {
            qty = Integer.parseInt(text.trim());
        } catch (NumberFormatException e) {
            throw new InvalidProductException("Quantity must be a whole number");
        }
        if (qty <= 0) throw new InvalidProductException("Quantity must be greater than zero");
        return qty;
    }

    // ---------------- dashboard / report numbers ----------------
    public ArrayList<Product> getLowStockProducts() {
        ArrayList<Product> low = new ArrayList<>();
        for (Product p : productService.getAllProducts()) {
            if (p.isLowStock()) low.add(p);
        }
        return low;
    }

    public int getTotalProducts() {
        return productService.getAllProducts().size();
    }

    public int getTotalStock() {
        int total = 0;
        for (Product p : productService.getAllProducts()) total += p.getQuantity();
        return total;
    }

    /** Inventory value = sum of (price x quantity) for every product. */
    public double getInventoryValue() {
        double total = 0;
        for (Product p : productService.getAllProducts()) total += p.getTotalValue();
        return total;
    }

    public double getTotalSales() {
        double total = 0;
        for (Transaction t : transactionService.getByType(Transaction.Type.SALE)) {
            total += t.getTotalAmount();
        }
        return total;
    }

    public double getTotalPurchases() {
        double total = 0;
        for (Transaction t : transactionService.getByType(Transaction.Type.PURCHASE)) {
            total += t.getTotalAmount();
        }
        return total;
    }
}
