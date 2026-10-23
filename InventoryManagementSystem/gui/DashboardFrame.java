package gui;

import service.AppContext;
import util.Money;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;

/** Screen 2: dashboard with live numbers and buttons to open every other screen. */
public class DashboardFrame extends JFrame {
    private final AppContext ctx;
    private final JLabel totalProducts = valueLabel();
    private final JLabel totalStock = valueLabel();
    private final JLabel lowStock = valueLabel();
    private final JLabel inventoryValue = valueLabel();
    private final JLabel alertLabel = new JLabel(" ", SwingConstants.CENTER);
    private final ArrayList<JFrame> openFrames = new ArrayList<>();

    public DashboardFrame(AppContext ctx) {
        super("Inventory Management System - Dashboard");
        this.ctx = ctx;
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        // ----- statistics -----
        JPanel stats = new JPanel(new GridLayout(4, 2, 10, 14));
        stats.setBorder(BorderFactory.createEmptyBorder(10, 60, 10, 60));
        addStat(stats, "Total Products", totalProducts);
        addStat(stats, "Total Stock", totalStock);
        addStat(stats, "Low Stock Products", lowStock);
        addStat(stats, "Inventory Value", inventoryValue);

        alertLabel.setForeground(new Color(190, 0, 0));
        alertLabel.setFont(new Font("SansSerif", Font.BOLD, 14));

        JPanel center = new JPanel(new BorderLayout());
        center.add(stats, BorderLayout.CENTER);
        center.add(alertLabel, BorderLayout.SOUTH);

        // ----- buttons -----
        JPanel buttons = new JPanel(new GridLayout(2, 4, 10, 10));
        buttons.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20));
        addButton(buttons, "Products",     () -> open(new ProductFrame(ctx)));
        addButton(buttons, "Stock",        () -> open(new StockFrame(ctx, 0)));
        addButton(buttons, "Sales",        () -> open(new StockFrame(ctx, 2)));
        addButton(buttons, "Purchase",     () -> open(new StockFrame(ctx, 1)));
        addButton(buttons, "Low Stock",    () -> open(new StockFrame(ctx, 3)));
        addButton(buttons, "Transactions", () -> open(new TransactionFrame(ctx)));
        addButton(buttons, "Reports",      () -> open(new ReportFrame(ctx)));
        addButton(buttons, "Logout",       this::logout);

        add(GuiUtil.title("INVENTORY MANAGEMENT SYSTEM"), BorderLayout.NORTH);
        add(center, BorderLayout.CENTER);
        add(buttons, BorderLayout.SOUTH);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowActivated(WindowEvent e) {    // refresh when we come back to this window
                refresh();
            }

            @Override
            public void windowClosing(WindowEvent e) {
                int answer = JOptionPane.showConfirmDialog(DashboardFrame.this,
                        "Exit the application?", "Exit", JOptionPane.YES_NO_OPTION);
                if (answer == JOptionPane.YES_OPTION) {
                    ctx.shutdown();
                    System.exit(0);
                }
            }
        });

        // The background thread tells us when the low-stock count changes
        ctx.backgroundTasks.setLowStockListener(count -> refresh());

        refresh();
        setSize(750, 460);
        setLocationRelativeTo(null);
    }

    /** Re-calculate and show all dashboard numbers. */
    public void refresh() {
        totalProducts.setText(String.valueOf(ctx.inventoryService.getTotalProducts()));
        totalStock.setText(String.valueOf(ctx.inventoryService.getTotalStock()));
        int low = ctx.inventoryService.getLowStockProducts().size();
        lowStock.setText(String.valueOf(low));
        inventoryValue.setText(Money.format(ctx.inventoryService.getInventoryValue()));
        alertLabel.setText(low > 0 ? "\u26A0 " + low + " product(s) are low on stock!" : " ");
    }

    private void open(JFrame frame) {
        openFrames.add(frame);
        frame.setVisible(true);
    }

    private void logout() {
        for (JFrame f : openFrames) f.dispose();
        ctx.backgroundTasks.setLowStockListener(null);
        ctx.saveAll();
        dispose();
        new LoginFrame(ctx).setVisible(true);
    }

    private static JLabel valueLabel() {
        JLabel label = new JLabel("0");
        label.setFont(new Font("SansSerif", Font.BOLD, 18));
        return label;
    }

    private void addStat(JPanel panel, String name, JLabel value) {
        JLabel nameLabel = new JLabel(name + " :");
        nameLabel.setFont(new Font("SansSerif", Font.PLAIN, 18));
        panel.add(nameLabel);
        panel.add(value);
    }

    private void addButton(JPanel panel, String text, Runnable action) {
        JButton button = new JButton(text);
        button.setFont(new Font("SansSerif", Font.BOLD, 14));
        button.addActionListener(e -> action.run());
        panel.add(button);
    }
}
