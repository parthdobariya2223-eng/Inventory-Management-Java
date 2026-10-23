package gui;

import service.AppContext;

import javax.swing.*;
import java.awt.*;

/** Screen 1: admin login. */
public class LoginFrame extends JFrame {

    public LoginFrame(AppContext ctx) {
        super("Inventory Management System - Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JTextField userField = new JTextField(15);
        JPasswordField passField = new JPasswordField(15);
        JButton loginButton = new JButton("LOGIN");

        JPanel form = new JPanel(new GridLayout(3, 2, 10, 10));
        form.setBorder(BorderFactory.createEmptyBorder(10, 30, 10, 30));
        form.add(new JLabel("Username:"));
        form.add(userField);
        form.add(new JLabel("Password:"));
        form.add(passField);
        form.add(new JLabel(""));
        form.add(loginButton);

        JLabel hint = new JLabel("TD Has Default login info ", SwingConstants.CENTER);
        hint.setForeground(Color.GRAY);
        hint.setBorder(BorderFactory.createEmptyBorder(0, 0, 12, 0));

        add(GuiUtil.title("INVENTORY MANAGEMENT SYSTEM"), BorderLayout.NORTH);
        add(form, BorderLayout.CENTER);
        add(hint, BorderLayout.SOUTH);

        // Event handling: what happens when LOGIN is clicked
        loginButton.addActionListener(e -> {
            String username = userField.getText().trim();
            String password = new String(passField.getPassword());

            if (ctx.userService.login(username, password)) {
                dispose();
                new DashboardFrame(ctx).setVisible(true);
            } else {
                GuiUtil.error(this, "Invalid username or password");
                passField.setText("");
            }
        });
        getRootPane().setDefaultButton(loginButton);    // pressing Enter = click LOGIN

        pack();
        setSize(480, getHeight() + 20);
        setLocationRelativeTo(null);
    }
}
