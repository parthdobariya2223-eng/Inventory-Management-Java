import exception.InsufficientStockException;
import exception.InvalidProductException;
import gui.LoginFrame;
import model.Product;
import service.AppContext;

import javax.swing.*;
import java.io.File;

/** Program entry point. */
public class Main {

    public static void main(String[] args) {
        new File("data").mkdirs();                       // make sure the data folder exists

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            // use the default look if this fails
        }

        boolean firstRun = !new File("data/products.dat").exists();

        AppContext ctx = new AppContext();              // loads products.dat, transactions.dat, users.dat
        if (firstRun) {
            addSampleData(ctx);
        }
        ctx.backgroundTasks.start();                    // auto-save, low-stock check, backup

        SwingUtilities.invokeLater(() -> new LoginFrame(ctx).setVisible(true));
    }

    /** A few demo products so the screens are not empty the first time. */
    private static void addSampleData(AppContext ctx) {
        try {
            String[][] items = {
                    // id, name, category, price, supplier, minimum
                    {"P101", "Dell Laptop", "Electronics", "55000", "Dell Supplier", "3"},
                    {"P102", "Wireless Mouse", "Electronics", "800", "Logitech Dealer", "10"},
                    {"P103", "Keyboard", "Electronics", "1500", "Logitech Dealer", "8"},
                    {"P201", "Notebook", "Stationery", "60", "Paper World", "20"},
                    {"P202", "Ball Pen", "Stationery", "10", "Paper World", "50"},
                    {"P301", "Rice 5kg", "Grocery", "450", "Gujarat Traders", "10"},
                    {"P401", "T-Shirt", "Clothing", "499", "Fashion Hub", "15"}
            };
            String[] purchased = {"10", "25", "6", "100", "200", "30", "40"};
            String[] sold = {"2", "18", "0", "20", "40", "5", "10"};

            for (int i = 0; i < items.length; i++) {
                String[] x = items[i];
                Product p = ctx.productService.buildProduct(x[0], x[1], x[2], x[3], "0", x[4], x[5]);
                ctx.productService.addProduct(p);
                ctx.inventoryService.purchaseStock(x[0], purchased[i]);
                if (!sold[i].equals("0")) {
                    ctx.inventoryService.sellStock(x[0], sold[i]);
                }
            }
        } catch (InvalidProductException | InsufficientStockException e) {
            System.out.println("Could not create sample data: " + e.getMessage());
        }
    }
}
