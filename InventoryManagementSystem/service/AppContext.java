package service;

import exception.InvalidProductException;

/** Holds all services in one place so every screen can share the same data. */
public class AppContext {
    public final ProductService productService = new ProductService();
    public final TransactionService transactionService = new TransactionService();
    public final InventoryService inventoryService = new InventoryService(productService, transactionService);
    public final UserService userService = new UserService();
    public final BackgroundTasks backgroundTasks = new BackgroundTasks(this);

    /** Save everything to the .dat files. */
    public void saveAll() {
        synchronized (productService) {
            try {
                productService.save();
                transactionService.save();
            } catch (InvalidProductException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    /** Called when the program closes. */
    public void shutdown() {
        backgroundTasks.stop();
        saveAll();
    }
}
