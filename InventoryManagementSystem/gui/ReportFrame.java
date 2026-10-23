package gui;

import model.Product;
import model.Transaction;
import service.AppContext;
import util.Money;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

/** Screen 7: text reports. */
public class ReportFrame extends JFrame {
    private final AppContext ctx;
    private final JTextArea output = new JTextArea();

    public ReportFrame(AppContext ctx) {
        super("Reports");
        this.ctx = ctx;
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel buttons = new JPanel(new GridLayout(0, 1, 8, 8));
        buttons.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        addReportButton(buttons, "1. All Products", this::allProducts);
        addReportButton(buttons, "2. Low Stock Products", this::lowStock);
        addReportButton(buttons, "3. Inventory Value", this::inventoryValue);
        addReportButton(buttons, "4. Purchase History", this::purchaseHistory);
        addReportButton(buttons, "5. Sales History", this::salesHistory);
        addReportButton(buttons, "6. Product-wise Stock", this::productWiseStock);
        addReportButton(buttons, "7. Summary", this::summary);

        JPanel left = new JPanel(new BorderLayout());
        left.add(buttons, BorderLayout.NORTH);

        output.setEditable(false);
        output.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));

        add(GuiUtil.title("REPORTS"), BorderLayout.NORTH);
        add(left, BorderLayout.WEST);
        add(new JScrollPane(output), BorderLayout.CENTER);

        summary();
        setSize(950, 560);
        setLocationRelativeTo(null);
    }

    private void addReportButton(JPanel panel, String text, Runnable report) {
        JButton b = new JButton(text);
        b.addActionListener(e -> {
            report.run();
            output.setCaretPosition(0);
        });
        panel.add(b);
    }

    // ---------------- reports ----------------
    private void allProducts() {
        StringBuilder sb = new StringBuilder("ALL PRODUCTS\n\n");
        sb.append(String.format("%-8s %-22s %-13s %14s %6s%n", "ID", "Name", "Category", "Price", "Qty"));
        sb.append("-".repeat(68)).append("\n");
        for (Product p : ctx.productService.getAllProducts()) {
            sb.append(String.format("%-8s %-22s %-13s %14s %6d%n", p.getProductId(), p.getName(),
                    p.getCategory(), Money.format(p.getPrice()), p.getQuantity()));
        }
        output.setText(sb.toString());
    }

    private void lowStock() {
        StringBuilder sb = new StringBuilder("LOW STOCK PRODUCTS\n\n");
        sb.append(String.format("%-8s %-22s %10s %10s%n", "ID", "Name", "Quantity", "Minimum"));
        sb.append("-".repeat(54)).append("\n");
        ArrayList<Product> low = ctx.inventoryService.getLowStockProducts();
        for (Product p : low) {
            sb.append(String.format("%-8s %-22s %10d %10d%n",
                    p.getProductId(), p.getName(), p.getQuantity(), p.getMinimumStock()));
        }
        if (low.isEmpty()) sb.append("No low stock products. \n");
        output.setText(sb.toString());
    }

    private void inventoryValue() {
        StringBuilder sb = new StringBuilder("INVENTORY VALUE  (Price x Quantity)\n\n");
        for (Product p : ctx.productService.getAllProducts()) {
            sb.append(String.format("%-22s %14s x %-5d = %16s%n", p.getName(),
                    Money.format(p.getPrice()), p.getQuantity(), Money.format(p.getTotalValue())));
        }
        sb.append("\n").append("-".repeat(64)).append("\n");
        sb.append("Total Inventory Value = ").append(Money.format(ctx.inventoryService.getInventoryValue()));
        output.setText(sb.toString());
    }

    private void purchaseHistory() {
        output.setText(history("PURCHASE HISTORY", Transaction.Type.PURCHASE,
                "Total Purchases", ctx.inventoryService.getTotalPurchases()));
    }

    private void salesHistory() {
        output.setText(history("SALES HISTORY", Transaction.Type.SALE,
                "Total Sales", ctx.inventoryService.getTotalSales()));
    }

    private String history(String title, Transaction.Type type, String totalLabel, double total) {
        StringBuilder sb = new StringBuilder(title + "\n\n");
        sb.append(String.format("%-6s %-8s %-22s %5s %14s  %s%n",
                "ID", "Product", "Name", "Qty", "Amount", "Date"));
        sb.append("-".repeat(70)).append("\n");
        for (Transaction t : ctx.transactionService.getByType(type)) {
            sb.append(String.format("%-6s %-8s %-22s %5d %14s  %s%n", t.getTransactionId(), t.getProductId(),
                    t.getProductName(), t.getQuantity(), Money.format(t.getTotalAmount()), t.getDateString()));
        }
        sb.append("\n").append(totalLabel).append(" : ").append(Money.format(total));
        return sb.toString();
    }

    private void productWiseStock() {
        StringBuilder sb = new StringBuilder("PRODUCT-WISE STOCK\n\n");
        sb.append("Categories: ").append(ctx.productService.getCategories()).append("\n\n");
        sb.append(String.format("%-8s %-22s %-13s %6s %6s  %s%n", "ID", "Name", "Category", "Qty", "Min", "Status"));
        sb.append("-".repeat(70)).append("\n");
        for (Product p : ctx.productService.getAllProducts()) {
            sb.append(String.format("%-8s %-22s %-13s %6d %6d  %s%n", p.getProductId(), p.getName(),
                    p.getCategory(), p.getQuantity(), p.getMinimumStock(), GuiUtil.status(p)));
        }
        output.setText(sb.toString());
    }

    private void summary() {
        StringBuilder sb = new StringBuilder("SUMMARY\n\n");
        sb.append(String.format("Total Products       : %d%n", ctx.inventoryService.getTotalProducts()));
        sb.append(String.format("Total Units          : %d%n", ctx.inventoryService.getTotalStock()));
        sb.append(String.format("Low Stock Items      : %d%n", ctx.inventoryService.getLowStockProducts().size()));
        sb.append(String.format("Inventory Value      : %s%n", Money.format(ctx.inventoryService.getInventoryValue())));
        sb.append(String.format("Total Purchases      : %s%n", Money.format(ctx.inventoryService.getTotalPurchases())));
        sb.append(String.format("Total Sales          : %s%n", Money.format(ctx.inventoryService.getTotalSales())));
        output.setText(sb.toString());
    }
}
