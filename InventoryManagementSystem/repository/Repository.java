package repository;

import java.io.*;
import java.util.ArrayList;

/**
 * A GENERIC repository: it can store any Serializable type T
 * (Product, Transaction, User ...) and save/load it from a file.
 */
public class Repository<T extends Serializable> {
    protected ArrayList<T> data = new ArrayList<>();

    public synchronized void add(T item) {
        data.add(item);
    }

    public synchronized boolean remove(T item) {
        return data.remove(item);
    }

    /** Returns a copy so callers can sort/filter without touching our list. */
    public synchronized ArrayList<T> getAll() {
        return new ArrayList<>(data);
    }

    /** Write the whole list to a file using ObjectOutputStream. */
    public synchronized void saveToFile(String fileName) throws IOException {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(fileName))) {
            out.writeObject(data);
        }
    }

    /** Read the list back from a file. Does nothing if the file does not exist yet. */
    @SuppressWarnings("unchecked")
    public synchronized void loadFromFile(String fileName) throws IOException, ClassNotFoundException {
        File file = new File(fileName);
        if (!file.exists()) {
            return;
        }
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(file))) {
            data = (ArrayList<T>) in.readObject();
        }
    }
}
