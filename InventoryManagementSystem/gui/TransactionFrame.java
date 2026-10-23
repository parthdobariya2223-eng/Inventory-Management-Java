package gui;

import model.Transaction;
import service.AppContext;
import util.Money;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;

/** Screen 6: transaction history with a PURCHASE / SALE filter. */
public class TransactionFrame extends JFrame {
    private final AppContext ctx;
    private final JComboBox<String> filter = new JComboBox<>(new String[]{"ALL", "PURCHASE", "SALE"});
    private final DefaultTableModel model = GuiUtil.tableModel(
            "Transaction ID", "Product ID", "Product Name", "Type", "Quantity", "Amount", "Date");

    public TransactionFrame(AppContext ctx) {
        super("Transaction History");
        this.ctx = ctx;
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JButton refresh = new JButton("REFRESH");
        refresh.addActionListener(e -> loadTransactions());
        filter.addActionListener(e -> loadTransactions());

        JPanel bar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
        bar.add(new JLabel("Show:"));
        bar.add(filter);
        bar.add(refresh);

        JTable table = new JTable(model);
        table.setRowHeight(24);

        JPanel north = new JPanel(new BorderLayout());
        north.add(GuiUtil.title("TRANSACTION HISTORY"), BorderLayout.NORTH);
        north.add(bar, BorderLayout.SOUTH);

        add(north, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);

        loadTransactions();
        setSize(850, 520);
        setLocationRelativeTo(null);
    }

    private void loadTransactions() {
        String selected = (String) filter.getSelectedItem();
        ArrayList<Transaction> list = selected.equals("ALL")
                ? ctx.transactionService.getAll()
                : ctx.transactionService.getByType(Transaction.Type.valueOf(selected));

        model.setRowCount(0);
        for (Transaction t : list) {
            model.addRow(new Object[]{
                    t.getTransactionId(), t.getProductId(), t.getProductName(), t.getType(),
                    t.getQuantity(), Money.format(t.getTotalAmount()), t.getDateString()});
        }
    }
}
