package service;

import exception.InvalidProductException;
import model.Product;
import model.Transaction;
import repository.Repository;

import java.io.IOException;
import java.util.ArrayList;

/** Keeps the purchase / sale history. */
public class TransactionService {
    public static final String FILE = "data/transactions.dat";

    // Generic repository used with the Transaction type
    private final Repository<Transaction> repo = new Repository<>();

    public TransactionService() {
        try {
            repo.loadFromFile(FILE);
        } catch (Exception e) {
            System.out.println("Could not load transactions: " + e.getMessage());
        }
    }

    public synchronized void record(Product p, Transaction.Type type, int quantity) {
        String id = String.format("T%03d", repo.getAll().size() + 1);
        repo.add(new Transaction(id, p.getProductId(), p.getName(), type, quantity, p.getPrice()));
    }

    public ArrayList<Transaction> getAll() {
        return repo.getAll();
    }

    public ArrayList<Transaction> getByType(Transaction.Type type) {
        ArrayList<Transaction> result = new ArrayList<>();
        for (Transaction t : repo.getAll()) {
            if (t.getType() == type) result.add(t);
        }
        return result;
    }

    public void save() throws InvalidProductException {
        try {
            repo.saveToFile(FILE);
        } catch (IOException e) {
            throw new InvalidProductException("Could not save transactions: " + e.getMessage());
        }
    }
}
