import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagLayout;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.prefs.Preferences;

public class LoginPage extends JFrame {
    private static final Color BACKGROUND = new Color(197, 224, 230);
    private static final Color DARK_NAVY = new Color(18, 40, 63);
    private static final Color CARD_WHITE = new Color(248, 250, 252);
    private static final Color HOVER_BLUE = new Color(156, 200, 209);
    private static final Color SOFT_TEXT = new Color(69, 97, 120);

    private final JTextField nameField;
    private final JTextField emailField;
    private final JPasswordField passwordField;
    private final Preferences preferences;
    private JButton userPanelButton;
    private JButton adminPanelButton;
    private String selectedPanel = "User";

   public LoginPage() {

    DataStore.seedIfNeeded();

    preferences = Preferences.userRoot().node(LoginPage.class.getName());
    
        setTitle("Rankala Lake Boat Ticket Sales System");
        setSize(980, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel background = new JPanel(new BorderLayout());
        background.setBackground(BACKGROUND);

        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setOpaque(false);

        RoundedPanel loginCard = new RoundedPanel(28);
        loginCard.setLayout(null);
        loginCard.setPreferredSize(new Dimension(500, 650));
        loginCard.setBackground(CARD_WHITE);
        loginCard.setBorder(new CompoundBorder(
                new RoundedBorder(HOVER_BLUE, 28, 1, 0, 0, 0, 0),
                new EmptyBorder(34, 34, 34, 34)
        ));

        JLabel boatIcon = new JLabel("\uD83D\uDEA4", SwingConstants.CENTER);
        boatIcon.setBounds(0, 26, 500, 36);
        boatIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 30));

        JLabel heading = new JLabel("Rankala Lake", SwingConstants.CENTER);
        heading.setBounds(40, 66, 420, 38);
        heading.setFont(new Font("Segoe UI", Font.BOLD, 32));
        heading.setForeground(DARK_NAVY);

        JLabel systemLabel = new JLabel("Boat Ticket Sales System", SwingConstants.CENTER);
        systemLabel.setBounds(40, 106, 420, 28);
        systemLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        systemLabel.setForeground(DARK_NAVY);

        JLabel subtitle = new JLabel("Easy and Smart Boat Booking Experience", SwingConstants.CENTER);
        subtitle.setBounds(40, 138, 420, 24);
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        subtitle.setForeground(SOFT_TEXT);

        JLabel quote = new JLabel("Welcome! Book.Ride.Enjoy", SwingConstants.CENTER);
        quote.setBounds(34, 168, 432, 34);
        quote.setFont(new Font("Segoe UI", Font.ITALIC, 14));
        quote.setForeground(SOFT_TEXT);

        JLabel panelLabel = createLabel("Choose Panel", 54, 206);

        userPanelButton = new JButton("User Panel");
        userPanelButton.setBounds(54, 234, 184, 46);
        userPanelButton.addActionListener(e -> selectPanel("User"));

        adminPanelButton = new JButton("Admin Panel");
        adminPanelButton.setBounds(262, 234, 184, 46);
        adminPanelButton.addActionListener(e -> selectPanel("Admin"));

        JLabel nameLabel = createLabel("Name", 54, 300);
        nameField = createField();
        nameField.setBounds(54, 328, 392, 48);

        JLabel emailLabel = createLabel("Email", 54, 394);
        emailField = createField();
        emailField.setBounds(54, 422, 392, 48);

        JLabel passwordLabel = createLabel("Password", 54, 488);
        passwordField = new JPasswordField();
        passwordField.setBounds(54, 516, 392, 48);
        passwordField.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        passwordField.setBorder(new CompoundBorder(
                new RoundedBorder(HOVER_BLUE, 18, 1, 0, 0, 0, 0),
                new EmptyBorder(0, 16, 0, 16)
        ));

        JButton loginButton = new JButton("Login");
        loginButton.setBounds(54, 588, 184, 52);
        stylePrimaryButton(loginButton);
        loginButton.addActionListener(e -> login());

        JButton signupButton = new JButton("Signup");
        signupButton.setBounds(262, 588, 184, 52);
        styleSecondaryButton(signupButton);
        signupButton.addActionListener(e -> signup());

        loginCard.add(boatIcon);
        loginCard.add(heading);
        loginCard.add(systemLabel);
        loginCard.add(subtitle);
        loginCard.add(quote);
        loginCard.add(panelLabel);
        loginCard.add(userPanelButton);
        loginCard.add(adminPanelButton);
        loginCard.add(nameLabel);
        loginCard.add(nameField);
        loginCard.add(emailLabel);
        loginCard.add(emailField);
        loginCard.add(passwordLabel);
        loginCard.add(passwordField);
        loginCard.add(loginButton);
        loginCard.add(signupButton);

        centerPanel.add(loginCard);
        background.add(centerPanel, BorderLayout.CENTER);
        add(background);

        loadPreferences();
        selectPanel("User");
        setVisible(true);
    }

    private JLabel createLabel(String text, int x, int y) {
        JLabel label = new JLabel(text);
        label.setBounds(x, y, 160, 22);
        label.setFont(new Font("Segoe UI", Font.BOLD, 15));
        label.setForeground(DARK_NAVY);
        return label;
    }

    private JTextField createField() {
        JTextField field = new JTextField();
        field.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        field.setBorder(new CompoundBorder(
                new RoundedBorder(HOVER_BLUE, 18, 1, 0, 0, 0, 0),
                new EmptyBorder(0, 16, 0, 16)
        ));
        return field;
    }

    private void stylePrimaryButton(JButton button) {
        button.setBackground(DARK_NAVY);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setFont(new Font("Segoe UI", Font.BOLD, 16));
        button.setBorder(new RoundedBorder(DARK_NAVY, 20, 0, 12, 24, 12, 24));
        addButtonHover(button, DARK_NAVY, new Color(28, 55, 82), Color.WHITE, Color.WHITE);
    }

    private void styleSecondaryButton(JButton button) {
        button.setBackground(HOVER_BLUE);
        button.setForeground(DARK_NAVY);
        button.setFocusPainted(false);
        button.setFont(new Font("Segoe UI", Font.BOLD, 16));
        button.setBorder(new RoundedBorder(HOVER_BLUE, 20, 0, 12, 24, 12, 24));
        addButtonHover(button, HOVER_BLUE, BACKGROUND, DARK_NAVY, DARK_NAVY);
    }

    private void addButtonHover(JButton button, Color normalBg, Color hoverBg, Color normalFg, Color hoverFg) {
        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                button.setBackground(hoverBg);
                button.setForeground(hoverFg);
            }

            public void mouseExited(MouseEvent e) {
                button.setBackground(normalBg);
                button.setForeground(normalFg);
            }
        });
    }

    private void selectPanel(String panel) {
        selectedPanel = panel;
        boolean userSelected = "User".equalsIgnoreCase(panel);
        stylePanelButton(userPanelButton, userSelected);
        stylePanelButton(adminPanelButton, !userSelected);
    }

    private void stylePanelButton(JButton button, boolean selected) {
        if (button == null) {
            return;
        }
        button.setBackground(selected ? DARK_NAVY : HOVER_BLUE);
        button.setForeground(selected ? Color.WHITE : DARK_NAVY);
        button.setFocusPainted(false);
        button.setFont(new Font("Segoe UI", Font.BOLD, 15));
        button.setBorder(new RoundedBorder(selected ? DARK_NAVY : HOVER_BLUE, 18, 0, 10, 20, 10, 20));
    }

    private void loadPreferences() {
        nameField.setText(preferences.get("userName", ""));
        emailField.setText(preferences.get("userEmail", ""));
    }

    private void login() {
        DataStore.seedIfNeeded();

        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();

        if (email.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter email and password.", "Missing Data", JOptionPane.WARNING_MESSAGE);
            return;
        }

        UserAccount currentUser = DataStore.validateUser(email, password);
        if (currentUser != null) {
            if (!currentUser.getRole().equalsIgnoreCase(selectedPanel)) {
                JOptionPane.showMessageDialog(this,
                        "This account belongs to the " + currentUser.getRole() + " panel. Please choose the correct panel.",
                        "Wrong Panel",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            preferences.put("userName", currentUser.getName());
            preferences.put("userEmail", email);

            JOptionPane.showMessageDialog(this, selectedPanel + " Login Successful", "Welcome", JOptionPane.INFORMATION_MESSAGE);
            dispose();

            if (currentUser.getRole().equalsIgnoreCase("Admin")) {
                AdminDashboard dashboard = new AdminDashboard(currentUser);
                dashboard.setVisible(true);
            } else {
                UserDashboard dashboard = new UserDashboard(currentUser);
                dashboard.setVisible(true);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Invalid Email or Password", "Login Failed", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void signup() {
        DataStore.seedIfNeeded();

        String name = nameField.getText().trim();
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();

        if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter name, email, and password.", "Missing Data", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if ("Admin".equalsIgnoreCase(selectedPanel)) {
            JOptionPane.showMessageDialog(this,
                    "Admin account already exists. Use admin@rankala.com / admin123 to open the Admin Panel.",
                    "Admin Signup Disabled",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        if (DataStore.emailExists(email)) {
            JOptionPane.showMessageDialog(this, "Account already exists. Please login instead.", "Signup Failed", JOptionPane.WARNING_MESSAGE);
            return;
        }

        DataStore.registerUser(name, email, password, "User");
        preferences.put("userName", name);
        preferences.put("userEmail", email);
        passwordField.setText("");
        JOptionPane.showMessageDialog(this, "Signup complete. You can now login.", "Signup Successful", JOptionPane.INFORMATION_MESSAGE);
    }

    private static class RoundedPanel extends JPanel {
        private final int radius;

        private RoundedPanel(int radius) {
            this.radius = radius;
            setOpaque(false);
        }

        protected void paintComponent(Graphics graphics) {
            Graphics2D g2 = (Graphics2D) graphics.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(new Color(DARK_NAVY.getRed(), DARK_NAVY.getGreen(), DARK_NAVY.getBlue(), 20));
            g2.fillRoundRect(6, 8, getWidth() - 12, getHeight() - 12, radius, radius);
            g2.setColor(getBackground());
            g2.fillRoundRect(0, 0, getWidth() - 12, getHeight() - 14, radius, radius);
            g2.dispose();
            super.paintComponent(graphics);
        }
    }

    private static class RoundedBorder extends EmptyBorder {
        private final Color color;
        private final int radius;
        private final int thickness;

        private RoundedBorder(Color color, int radius, int thickness, int top, int left, int bottom, int right) {
            super(top, left, bottom, right);
            this.color = color;
            this.radius = radius;
            this.thickness = thickness;
        }

        public void paintBorder(Component component, Graphics graphics, int x, int y, int width, int height) {
            if (thickness <= 0) {
                return;
            }
            Graphics2D g2 = (Graphics2D) graphics.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color);
            for (int i = 0; i < thickness; i++) {
                g2.drawRoundRect(x + i, y + i, width - 1 - i * 2, height - 1 - i * 2, radius, radius);
            }
            g2.dispose();
        }
    }
}
