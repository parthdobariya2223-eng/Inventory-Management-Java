package gui;

import exception.InvalidProductException;
import model.Product;
import service.AppContext;
import service.ProductService;
import util.Money;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

/** Screen 3: add / view / search / sort / update / delete products. */
public class ProductFrame extends JFrame {
    private final AppContext ctx;
    private final ProductService ps;

    private final JTextField idField = new JTextField();
    private final JTextField nameField = new JTextField();
    private final JTextField categoryField = new JTextField();
    private final JTextField priceField = new JTextField();
    private final JTextField quantityField = new JTextField();
    private final JTextField supplierField = new JTextField();
    private final JTextField minField = new JTextField();

    private final JComboBox<String> searchBy = new JComboBox<>(ProductService.SEARCH_OPTIONS);
    private final JTextField searchField = new JTextField(15);
    private final JComboBox<String> sortBy = new JComboBox<>(ProductService.SORT_OPTIONS);

    private final DefaultTableModel model = GuiUtil.tableModel(
            "ID", "Name", "Category", "Price", "Quantity", "Supplier", "Min Stock", "Status");
    private final JTable table = new JTable(model);

    private ArrayList<Product> currentList = new ArrayList<>();

    public ProductFrame(AppContext ctx) {
        super("Product Management");
        this.ctx = ctx;
        this.ps = ctx.productService;
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        add(GuiUtil.title("PRODUCT MANAGEMENT"), BorderLayout.NORTH);

        JPanel top = new JPanel(new BorderLayout());
        top.add(buildForm(), BorderLayout.NORTH);
        top.add(buildSearchBar(), BorderLayout.SOUTH);

        GuiUtil.highlightLowStock(table, 7);
        table.setRowHeight(24);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) loadSelectedRow();
        });

        JPanel center = new JPanel(new BorderLayout());
        center.add(top, BorderLayout.NORTH);
        center.add(new JScrollPane(table), BorderLayout.CENTER);
        add(center, BorderLayout.CENTER);

        sortBy.addActionListener(e -> showProducts(currentList));   // re-sort when changed
        showProducts(ps.getAllProducts());

        setSize(1050, 650);
        setLocationRelativeTo(null);
    }

    // ---------------- building the screen ----------------
    private JPanel buildForm() {
        JPanel fields = new JPanel(new GridLayout(4, 4, 10, 8));
        fields.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        fields.add(new JLabel("Product ID:"));      fields.add(idField);
        fields.add(new JLabel("Product Name:"));    fields.add(nameField);
        fields.add(new JLabel("Category:"));        fields.add(categoryField);
        fields.add(new JLabel("Price:"));           fields.add(priceField);
        fields.add(new JLabel("Quantity:"));        fields.add(quantityField);
        fields.add(new JLabel("Supplier:"));        fields.add(supplierField);
        fields.add(new JLabel("Minimum Stock:"));   fields.add(minField);
        fields.add(new JLabel(""));                 fields.add(new JLabel(""));

        JButton addButton = new JButton("ADD PRODUCT");
        JButton updateButton = new JButton("UPDATE PRODUCT");
        JButton deleteButton = new JButton("DELETE");
        JButton clearButton = new JButton("CLEAR");

        addButton.addActionListener(new AddButtonListener());       // inner class listener
        updateButton.addActionListener(e -> updateProduct());       // lambda listeners
        deleteButton.addActionListener(e -> deleteProduct());
        clearButton.addActionListener(e -> clearForm());

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 8));
        buttons.add(addButton);
        buttons.add(updateButton);
        buttons.add(deleteButton);
        buttons.add(clearButton);

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(fields, BorderLayout.CENTER);
        panel.add(buttons, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel buildSearchBar() {
        JButton searchButton = new JButton("SEARCH");
        JButton showAllButton = new JButton("SHOW ALL");
        searchButton.addActionListener(e -> searchProducts());
        showAllButton.addActionListener(e -> showProducts(ps.getAllProducts()));

        JPanel bar = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
        bar.setBorder(BorderFactory.createTitledBorder("Search / Sort"));
        bar.add(new JLabel("Search by:"));
        bar.add(searchBy);
        bar.add(searchField);
        bar.add(searchButton);
        bar.add(showAllButton);
        bar.add(new JLabel("   Sort by:"));
        bar.add(sortBy);
        return bar;
    }

    // ---------------- inner class event listener ----------------
    private class AddButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            addProduct();
        }
    }

    // ---------------- actions ----------------
    private void addProduct() {
        try {
            Product p = ps.buildProduct(idField.getText(), nameField.getText(), categoryField.getText(),
                    priceField.getText(), quantityField.getText(), supplierField.getText(), minField.getText());
            ps.addProduct(p);
            GuiUtil.info(this, "Product added successfully!");
            clearForm();
            showProducts(ps.getAllProducts());
        } catch (InvalidProductException ex) {
            GuiUtil.error(this, ex.getMessage());
        }
    }

    private void updateProduct() {
        try {
            ps.updateProduct(idField.getText(), nameField.getText(), categoryField.getText(),
                    priceField.getText(), supplierField.getText(), minField.getText());
            GuiUtil.info(this, "Product updated successfully!\n(Use the Stock screen to change quantity.)");
            clearForm();
            showProducts(ps.getAllProducts());
        } catch (InvalidProductException ex) {
            GuiUtil.error(this, ex.getMessage());
        }
    }

    private void deleteProduct() {
        String id = idField.getText().trim();
        if (id.isEmpty()) {
            GuiUtil.error(this, "Select a product in the table (or type its Product ID) first");
            return;
        }
        int answer = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete this product?", "Confirm Delete",
                JOptionPane.YES_NO_OPTION);
        if (answer == JOptionPane.YES_OPTION) {
            try {
                ps.deleteProduct(id);
                GuiUtil.info(this, "Product deleted.");
                clearForm();
                showProducts(ps.getAllProducts());
            } catch (InvalidProductException ex) {
                GuiUtil.error(this, ex.getMessage());
            }
        }
    }

    private void searchProducts() {
        ArrayList<Product> result = ps.search((String) searchBy.getSelectedItem(), searchField.getText());
        if (result.isEmpty()) {
            GuiUtil.info(this, "No products found.");
        }
        showProducts(result);
    }

    // ---------------- table + form helpers ----------------
    private void showProducts(ArrayList<Product> list) {
        currentList = list;
        ps.sort(list, (String) sortBy.getSelectedItem());
        model.setRowCount(0);
        for (Product p : list) {
            model.addRow(new Object[]{
                    p.getProductId(), p.getName(), p.getCategory(), Money.format(p.getPrice()),
                    p.getQuantity(), p.getSupplier(), p.getMinimumStock(), GuiUtil.status(p)});
        }
    }

    /** When a table row is clicked, copy its values into the form. */
    private void loadSelectedRow() {
        int row = table.getSelectedRow();
        if (row < 0) return;
        Product p = ps.findById((String) model.getValueAt(row, 0));
        if (p == null) return;
        idField.setText(p.getProductId());
        nameField.setText(p.getName());
        categoryField.setText(p.getCategory());
        priceField.setText(String.valueOf(p.getPrice()));
        quantityField.setText(String.valueOf(p.getQuantity()));
        supplierField.setText(p.getSupplier());
        minField.setText(String.valueOf(p.getMinimumStock()));
        idField.setEditable(false);          // ID and quantity cannot be changed while updating
        quantityField.setEditable(false);
    }

    private void clearForm() {
        idField.setText("");
        nameField.setText("");
        categoryField.setText("");
        priceField.setText("");
        quantityField.setText("");
        supplierField.setText("");
        minField.setText("");
        idField.setEditable(true);
        quantityField.setEditable(true);
        table.clearSelection();
    }
}
