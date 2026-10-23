package repository;

import model.Product;
import java.io.IOException;
import java.util.HashMap;

/**
 * Stores products in an ArrayList (from Repository) AND in a HashMap
 * (Product ID -> Product) for very fast lookup.
 */
public class ProductRepository extends Repository<Product> {
    private HashMap<String, Product> map = new HashMap<>();

    @Override
    public synchronized void add(Product p) {
        super.add(p);
        map.put(p.getProductId().toUpperCase(), p);
    }

    @Override
    public synchronized boolean remove(Product p) {
        map.remove(p.getProductId().toUpperCase());
        return super.remove(p);
    }

    public synchronized Product findById(String id) {
        if (id == null) return null;
        return map.get(id.trim().toUpperCase());
    }

    /** After loading the list from the file, rebuild the HashMap. */
    @Override
    public synchronized void loadFromFile(String fileName) throws IOException, ClassNotFoundException {
        super.loadFromFile(fileName);
        map.clear();
        for (Product p : data) {
            map.put(p.getProductId().toUpperCase(), p);
        }
    }
}
