package service;

import exception.InvalidProductException;
import model.Product;
import repository.ProductRepository;

import java.io.IOException;
import java.util.*;

/** Add / view / search / update / delete / sort products + input validation. */
public class ProductService {
    public static final String FILE = "data/products.dat";

    public static final String[] SEARCH_OPTIONS = {"Product ID", "Name", "Category", "Supplier"};
    public static final String[] SORT_OPTIONS =
            {"Name (A-Z)", "Price (Low-High)", "Price (High-Low)", "Quantity (Low-High)"};

    private final ProductRepository repo = new ProductRepository();

    public ProductService() {
        try {
            repo.loadFromFile(FILE);
        } catch (Exception e) {
            System.out.println("Could not load products: " + e.getMessage());
        }
    }

    // ---------------- file handling ----------------
    /** (InvalidProductException is also used for file errors to keep things simple.) */
    public void save() throws InvalidProductException {
        try {
            repo.saveToFile(FILE);
        } catch (IOException e) {
            throw new InvalidProductException("Could not save products: " + e.getMessage());
        }
    }

    // ---------------- validation ----------------
    /** Reads text from the form, validates it and returns a Product object. */
    public Product buildProduct(String id, String name, String category, String priceText,
                                String qtyText, String supplier, String minText)
            throws InvalidProductException {

        if (id.trim().isEmpty()) throw new InvalidProductException("Product ID cannot be empty");
        if (name.trim().isEmpty()) throw new InvalidProductException("Product name cannot be empty");
        if (category.trim().isEmpty()) throw new InvalidProductException("Category cannot be empty");
        if (supplier.trim().isEmpty()) throw new InvalidProductException("Supplier cannot be empty");

        double price = parseDouble(priceText, "Price");
        int quantity = parseInt(qtyText, "Quantity");
        int minimum = parseInt(minText, "Minimum stock");

        if (price < 0) throw new InvalidProductException("Price cannot be negative");
        if (quantity < 0) throw new InvalidProductException("Quantity cannot be negative");
        if (minimum < 0) throw new InvalidProductException("Minimum stock cannot be negative");

        return new Product(id.trim().toUpperCase(), name.trim(), category.trim(),
                price, quantity, supplier.trim(), minimum);
    }

    private double parseDouble(String text, String label) throws InvalidProductException {
        if (text.trim().isEmpty()) throw new InvalidProductException(label + " cannot be empty");
        try {
            return Double.parseDouble(text.trim());
        } catch (NumberFormatException e) {
            throw new InvalidProductException(label + " must be a valid number");
        }
    }

    private int parseInt(String text, String label) throws InvalidProductException {
        if (text.trim().isEmpty()) throw new InvalidProductException(label + " cannot be empty");
        try {
            return Integer.parseInt(text.trim());
        } catch (NumberFormatException e) {
            throw new InvalidProductException(label + " must be a whole number");
        }
    }

    // ---------------- CRUD ----------------
    public synchronized void addProduct(Product p) throws InvalidProductException {
        if (repo.findById(p.getProductId()) != null) {
            throw new InvalidProductException("Product ID must be unique. '" + p.getProductId() + "' already exists");
        }
        repo.add(p);
        save();
    }

    public synchronized void updateProduct(String id, String name, String category, String priceText,
                                           String supplier, String minText) throws InvalidProductException {
        Product existing = repo.findById(id);
        if (existing == null) throw new InvalidProductException("Product not found: " + id.trim());

        // Re-use validation (quantity is not changed here, so we pass "0")
        Product temp = buildProduct(id, name, category, priceText, "0", supplier, minText);

        existing.setName(temp.getName());
        existing.setCategory(temp.getCategory());
        existing.setPrice(temp.getPrice());
        existing.setSupplier(temp.getSupplier());
        existing.setMinimumStock(temp.getMinimumStock());
        save();
    }

    public synchronized void deleteProduct(String id) throws InvalidProductException {
        Product p = repo.findById(id);
        if (p == null) throw new InvalidProductException("Product not found: " + id.trim());
        repo.remove(p);
        save();
    }

    public Product findById(String id) {
        return repo.findById(id);
    }

    public ArrayList<Product> getAllProducts() {
        return repo.getAll();
    }

    // ---------------- search ----------------
    public ArrayList<Product> search(String by, String text) {
        String key = text.trim().toLowerCase();
        ArrayList<Product> result = new ArrayList<>();
        for (Product p : repo.getAll()) {
            String value;
            switch (by) {
                case "Product ID": value = p.getProductId(); break;
                case "Name":       value = p.getName(); break;
                case "Category":   value = p.getCategory(); break;
                default:           value = p.getSupplier(); break;
            }
            if (value.toLowerCase().contains(key)) {
                result.add(p);
            }
        }
        return result;
    }

    // ---------------- sorting with Comparator ----------------
    public void sort(ArrayList<Product> list, String option) {
        switch (option) {
            case "Name (A-Z)":
                list.sort(Comparator.comparing(Product::getName, String.CASE_INSENSITIVE_ORDER));
                break;
            case "Price (Low-High)":
                list.sort(Comparator.comparingDouble(Product::getPrice));
                break;
            case "Price (High-Low)":
                list.sort(Comparator.comparingDouble(Product::getPrice).reversed());
                break;
            case "Quantity (Low-High)":
                list.sort(Comparator.comparingInt(Product::getQuantity));
                break;
        }
    }

    // ---------------- unique, sorted categories ----------------
    public TreeSet<String> getCategories() {
        HashSet<String> unique = new HashSet<>();           // removes duplicates
        for (Product p : repo.getAll()) {
            unique.add(p.getCategory());
        }
        return new TreeSet<>(unique);                       // sorts them A-Z
    }
}
