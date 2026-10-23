package gui;

import exception.InsufficientStockException;
import exception.InvalidProductException;
import model.Product;
import service.AppContext;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

/**
 * Screen 4/5: stock management.
 * Tabs: 0 = Stock Overview, 1 = Purchase (add stock), 2 = Sale (remove stock), 3 = Low Stock
 */
public class StockFrame extends JFrame {
    private final AppContext ctx;

    private final JTextField purchaseId = new JTextField(12);
    private final JTextField purchaseQty = new JTextField(12);
    private final JTextField saleId = new JTextField(12);
    private final JTextField saleQty = new JTextField(12);

    private final DefaultTableModel overviewModel =
            GuiUtil.tableModel("ID", "Name", "Quantity", "Minimum", "Status");
    private final DefaultTableModel lowModel =
            GuiUtil.tableModel("ID", "Name", "Quantity", "Minimum");

    public StockFrame(AppContext ctx, int startTab) {
        super("Stock Management");
        this.ctx = ctx;
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Stock Overview", buildOverviewTab());
        tabs.addTab("Purchase (Add Stock)", buildActionTab(purchaseId, purchaseQty,
                "Quantity Purchased:", "ADD STOCK", this::doPurchase));
        tabs.addTab("Sale (Remove Stock)", buildActionTab(saleId, saleQty,
                "Quantity Sold:", "SELL", this::doSale));
        tabs.addTab("Low Stock Alert", buildLowStockTab());
        tabs.setSelectedIndex(startTab);

        add(GuiUtil.title("STOCK MANAGEMENT"), BorderLayout.NORTH);
        add(tabs, BorderLayout.CENTER);

        refreshTables();
        setSize(800, 520);
        setLocationRelativeTo(null);
    }

    // ---------------- tabs ----------------
    private JPanel buildOverviewTab() {
        JTable table = new JTable(overviewModel);
        table.setRowHeight(24);
        GuiUtil.highlightLowStock(table, 4);
        return tablePanel(table);
    }

    private JPanel buildLowStockTab() {
        JTable table = new JTable(lowModel);
        table.setRowHeight(24);
        return tablePanel(table);
    }

    private JPanel tablePanel(JTable table) {
        JButton refresh = new JButton("REFRESH");
        refresh.addActionListener(e -> refreshTables());
        JPanel bottom = new JPanel();
        bottom.add(refresh);

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        panel.add(bottom, BorderLayout.SOUTH);
        return panel;
    }

    /** Builds the Purchase and Sale tabs (they look the same). */
    private JPanel buildActionTab(JTextField idField, JTextField qtyField,
                                  String qtyLabel, String buttonText, Runnable action) {
        JButton button = new JButton(buttonText);
        button.addActionListener(e -> action.run());

        JPanel form = new JPanel(new GridLayout(3, 2, 10, 15));
        form.add(new JLabel("Product ID:"));
        form.add(idField);
        form.add(new JLabel(qtyLabel));
        form.add(qtyField);
        form.add(new JLabel(""));
        form.add(button);

        JPanel wrapper = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 60));
        wrapper.add(form);
        return wrapper;
    }

    // ---------------- actions ----------------
    private void doPurchase() {
        try {
            Product p = ctx.inventoryService.purchaseStock(purchaseId.getText(), purchaseQty.getText());
            GuiUtil.info(this, "Stock added!\n" + p.getName() + " now has " + p.getQuantity() + " units.");
            purchaseQty.setText("");
            refreshTables();
        } catch (InvalidProductException ex) {
            GuiUtil.error(this, ex.getMessage());
        }
    }

    private void doSale() {
        try {
            Product p = ctx.inventoryService.sellStock(saleId.getText(), saleQty.getText());
            String message = "Sale completed!\n" + p.getName() + " now has " + p.getQuantity() + " units.";
            if (p.isLowStock()) {
                message += "\n\n\u26A0 LOW STOCK (minimum level is " + p.getMinimumStock() + ")";
            }
            GuiUtil.info(this, message);
            saleQty.setText("");
            refreshTables();
        } catch (InvalidProductException | InsufficientStockException ex) {
            GuiUtil.error(this, ex.getMessage());
        }
    }

    private void refreshTables() {
        overviewModel.setRowCount(0);
        for (Product p : ctx.productService.getAllProducts()) {
            overviewModel.addRow(new Object[]{
                    p.getProductId(), p.getName(), p.getQuantity(), p.getMinimumStock(), GuiUtil.status(p)});
        }
        lowModel.setRowCount(0);
        for (Product p : ctx.inventoryService.getLowStockProducts()) {
            lowModel.addRow(new Object[]{
                    p.getProductId(), p.getName(), p.getQuantity(), p.getMinimumStock()});
        }
    }
}
