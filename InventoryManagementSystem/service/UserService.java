package service;

import model.User;
import repository.Repository;

/** Handles admin login. Default account: admin / admin123 */
public class UserService {
    public static final String FILE = "data/users.dat";

    private final Repository<User> repo = new Repository<>();

    public UserService() {
        try {
            repo.loadFromFile(FILE);
            if (repo.getAll().isEmpty()) {              // first run: create default admin
                repo.add(new User("admin", "admin123"));
                repo.saveToFile(FILE);
            }
        } catch (Exception e) {
            System.out.println("Could not load users: " + e.getMessage());
            repo.add(new User("admin", "admin123"));
        }
    }

    public boolean login(String username, String password) {
        for (User u : repo.getAll()) {
            if (u.matches(username, password)) return true;
        }
        return false;
    }
}
