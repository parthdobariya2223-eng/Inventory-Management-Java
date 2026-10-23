package gui;

import model.Product;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

/** Small helper methods shared by all screens. */
public class GuiUtil {

    public static void info(Component parent, String message) {
        JOptionPane.showMessageDialog(parent, message, "Information", JOptionPane.INFORMATION_MESSAGE);
    }

    public static void error(Component parent, String message) {
        JOptionPane.showMessageDialog(parent, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    /** A table model whose cells cannot be edited by typing. */
    public static DefaultTableModel tableModel(String... columns) {
        return new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
    }

    public static String status(Product p) {
        return p.isLowStock() ? "LOW STOCK" : "OK";
    }

    /** Paints rows red when the Status column says LOW STOCK. */
    public static void highlightLowStock(JTable table, final int statusColumn) {
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object value, boolean selected,
                                                           boolean focus, int row, int col) {
                Component c = super.getTableCellRendererComponent(t, value, selected, focus, row, col);
                if (!selected) {
                    boolean low = "LOW STOCK".equals(t.getValueAt(row, statusColumn));
                    c.setBackground(low ? new Color(255, 215, 215) : Color.WHITE);
                    c.setForeground(Color.BLACK);
                }
                return c;
            }
        });
    }

    public static JLabel title(String text) {
        JLabel label = new JLabel(text, SwingConstants.CENTER);
        label.setFont(new Font("SansSerif", Font.BOLD, 22));
        label.setBorder(BorderFactory.createEmptyBorder(12, 10, 12, 10));
        return label;
    }
}
