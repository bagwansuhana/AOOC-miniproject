import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

public class AdminDashboard extends JFrame {
    private static final Color LIGHT_BACKGROUND = new Color(197, 224, 230);
    private static final Color CARD_WHITE = new Color(248, 250, 252);
    private static final Color SIDEBAR_COLOR = new Color(18, 40, 63);
    private static final Color SIDEBAR_HOVER = new Color(156, 200, 209);
    private static final Color BORDER_COLOR = new Color(156, 200, 209);
    private static final Color AVAILABLE_COLOR = new Color(22, 163, 74);
    private static final Color BOOKED_COLOR = new Color(220, 38, 38);

    private final String currentUser;
    private final String currentUserEmail;
    private final ArrayList<JButton> sidebarButtons = new ArrayList<>();
    private CardLayout cardLayout;
    private JPanel contentPanel;
    private String activeCardName = "DASHBOARD";
    private DefaultTableModel manageBoatsModel;
    private DefaultTableModel bookingsModel;
    private DefaultTableModel usersModel;
    private JTable manageBoatsTable;
    private JTextField boatNameField;
    private JTextField boatPriceField;
    private JTextField boatSeatsField;
    private JTextField boatTypeField;
    private JTextField boatTotalBoatsField;
    private JCheckBox availabilityToggle;
    private JLabel totalBoatsValue;
    private JLabel ticketsSoldValue;
    private JLabel revenueValue;
    private JLabel availableBoatsValue;
    private JLabel bookedBoatsValue;
    private JLabel reportRevenueValue;
    private JLabel reportTicketsValue;
    private JLabel reportAvailableValue;
    private JLabel reportBookedValue;
    private RevenueChartPanel revenueChartPanel;
    private BookingChartPanel bookingChartPanel;
    private int selectedBoatId = -1;

    public AdminDashboard(UserAccount currentUser) {
        this(
                currentUser == null ? "Admin" : currentUser.getName(),
                currentUser == null ? "" : currentUser.getEmail()
        );
    }

    public AdminDashboard(String userName, String userEmail) {
        currentUser = userName == null || userName.trim().isEmpty() ? "Admin" : userName.trim();
        currentUserEmail = userEmail == null ? "" : userEmail.trim();

        setTitle("Rankala Lake Admin Dashboard");
        setSize(1480, 900);
        setMinimumSize(new Dimension(1100, 720));
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(LIGHT_BACKGROUND);
        root.add(createSidebar(), BorderLayout.WEST);
        root.add(createTopBar(), BorderLayout.NORTH);

        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setBackground(LIGHT_BACKGROUND);
        contentPanel.add(buildDashboardPanel(), "DASHBOARD");
        contentPanel.add(buildManageBoatsPanel(), "BOATS");
        contentPanel.add(buildBookingsPanel(), "BOOKINGS");
        contentPanel.add(buildUsersPanel(), "USERS");
        contentPanel.add(buildReportsPanel(), "REPORTS");
        contentPanel.add(buildSettingsPanel(), "SETTINGS");
        root.add(contentPanel, BorderLayout.CENTER);

        add(root);
        refreshAllPanels();
        showCard("DASHBOARD");
    }

    private JPanel createSidebar() {
        JPanel sidebar = new JPanel(new GridBagLayout());
        sidebar.setBackground(LIGHT_BACKGROUND);
        sidebar.setPreferredSize(new Dimension(252, 900));

        JPanel stack = new JPanel();
        stack.setBackground(LIGHT_BACKGROUND);
        stack.setLayout(new BoxLayout(stack, BoxLayout.Y_AXIS));
        stack.setBorder(new EmptyBorder(0, 24, 0, 24));

        JLabel logo = new JLabel("Rankala Lake");
        logo.setFont(new Font("Segoe UI", Font.BOLD, 25));
        logo.setForeground(SIDEBAR_COLOR);
        logo.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel subtitle = new JLabel("Admin Panel");
        subtitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        subtitle.setForeground(SIDEBAR_COLOR);
        subtitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        stack.add(Box.createVerticalStrut(28));
        stack.add(logo);
        stack.add(Box.createVerticalStrut(8));
        stack.add(subtitle);
        stack.add(Box.createVerticalStrut(34));
        stack.add(createSidebarButton("Dashboard", "DASHBOARD"));
        stack.add(Box.createVerticalStrut(14));
        stack.add(createSidebarButton("Manage Boats", "BOATS"));
        stack.add(Box.createVerticalStrut(14));
        stack.add(createSidebarButton("Bookings", "BOOKINGS"));
        stack.add(Box.createVerticalStrut(14));
        stack.add(createSidebarButton("Users", "USERS"));
        stack.add(Box.createVerticalStrut(14));
        stack.add(createSidebarButton("Reports", "REPORTS"));
        stack.add(Box.createVerticalStrut(14));
        stack.add(createSidebarButton("Settings", "SETTINGS"));
        stack.add(Box.createVerticalStrut(14));
        stack.add(createSidebarButton("Logout", "LOGOUT"));

        sidebar.add(stack);
        return sidebar;
    }

    private JButton createSidebarButton(String title, String cardName) {
        JButton button = new JButton(title);
        button.setAlignmentX(Component.LEFT_ALIGNMENT);
        button.setMaximumSize(new Dimension(204, 54));
        button.setPreferredSize(new Dimension(204, 54));
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setFocusPainted(false);
        button.setBackground(SIDEBAR_COLOR);
        button.setForeground(Color.WHITE);
        button.setBorder(new RoundedLineBorder(SIDEBAR_COLOR, 20, 0, 15, 22, 15, 22));
        button.setFont(new Font("Segoe UI", Font.BOLD, 16));
        button.addActionListener(e -> {
            if ("LOGOUT".equals(cardName)) {
                logout();
            } else {
                showCard(cardName);
            }
        });
        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                if (!cardName.equals(activeCardName)) {
                    button.setBackground(SIDEBAR_HOVER);
                    button.setForeground(SIDEBAR_COLOR);
                }
            }

            public void mouseExited(MouseEvent e) {
                updateSidebarSelection();
            }
        });
        button.putClientProperty("cardName", cardName);
        sidebarButtons.add(button);
        return button;
    }

    private JPanel createTopBar() {
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBorder(new EmptyBorder(18, 32, 18, 34));
        topBar.setBackground(CARD_WHITE);

        JLabel title = new JLabel("Rankala Lake Admin Dashboard");
        title.setFont(new Font("Segoe UI", Font.BOLD, 26));
        title.setForeground(SIDEBAR_COLOR);

        JLabel userLabel = new JLabel("<html><b>Welcome, " + currentUser + "</b><br>Role: Admin</html>");
        userLabel.setForeground(SIDEBAR_COLOR);
        userLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        userLabel.setBorder(new CompoundBorder(new RoundedLineBorder(BORDER_COLOR, 18, 1, 0, 0, 0, 0), new EmptyBorder(10, 16, 10, 16)));

        topBar.add(title, BorderLayout.WEST);
        topBar.add(userLabel, BorderLayout.EAST);
        return topBar;
    }

    private JPanel buildDashboardPanel() {
        JPanel panel = createPagePanel();
        JPanel header = new JPanel(new BorderLayout(0, 20));
        header.setOpaque(false);
        JLabel title = createPageTitle("Dashboard");
        JPanel stats = new JPanel(new GridLayout(1, 5, 18, 18));
        stats.setOpaque(false);
        totalBoatsValue = addStatCard(stats, "Total Boats", "0");
        ticketsSoldValue = addStatCard(stats, "Tickets Sold", "0");
        revenueValue = addStatCard(stats, "Revenue", "Rs 0");
        availableBoatsValue = addStatCard(stats, "Available Boats", "0");
        bookedBoatsValue = addStatCard(stats, "Booked Boats", "0");
        header.add(title, BorderLayout.NORTH);
        header.add(stats, BorderLayout.CENTER);

        JPanel body = new JPanel(new GridLayout(1, 2, 24, 24));
        body.setOpaque(false);
        JPanel revenueCard = createCardPanel();
        revenueCard.setLayout(new BorderLayout());
        revenueChartPanel = new RevenueChartPanel();
        revenueCard.add(revenueChartPanel, BorderLayout.CENTER);
        JPanel bookingCard = createCardPanel();
        bookingCard.setLayout(new BorderLayout());
        bookingChartPanel = new BookingChartPanel();
        bookingCard.add(bookingChartPanel, BorderLayout.CENTER);
        body.add(revenueCard);
        body.add(bookingCard);

        panel.add(header, BorderLayout.NORTH);
        panel.add(body, BorderLayout.CENTER);
        return panel;
    }

    private JPanel buildManageBoatsPanel() {
        JPanel panel = createPagePanel();
        JLabel heading = createPageTitle("Manage Boats");

        manageBoatsModel = new DefaultTableModel(new String[]{"Boat ID", "Boat Name", "Price", "Seats", "Type", "Total Boats", "Available Boats", "Status"}, 0) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        manageBoatsTable = createTable(manageBoatsModel);
        manageBoatsTable.getColumnModel().getColumn(7).setCellRenderer(new StatusCellRenderer());
        manageBoatsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        manageBoatsTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                populateBoatFields();
            }
        });

        JPanel tableCard = createCardPanel();
        tableCard.setLayout(new BorderLayout());
        tableCard.add(new JScrollPane(manageBoatsTable), BorderLayout.CENTER);

        JPanel formCard = createCardPanel();
        formCard.setLayout(null);
        formCard.setPreferredSize(new Dimension(430, 650));
        boatNameField = createTextField();
        boatPriceField = createTextField();
        boatSeatsField = createTextField();
        boatTypeField = createTextField();
        boatTotalBoatsField = createTextField();
        availabilityToggle = new JCheckBox("Available");
        availabilityToggle.setBounds(24, 434, 160, 30);
        availabilityToggle.setSelected(true);
        availabilityToggle.setBackground(CARD_WHITE);
        availabilityToggle.setForeground(SIDEBAR_COLOR);
        availabilityToggle.setFont(new Font("Segoe UI", Font.BOLD, 14));

        addLabelAndComponent(formCard, "Boat Name", boatNameField, 24, 24);
        addLabelAndComponent(formCard, "Price", boatPriceField, 24, 112);
        addLabelAndComponent(formCard, "Seats", boatSeatsField, 24, 200);
        addLabelAndComponent(formCard, "Type", boatTypeField, 24, 288);
        addLabelAndComponent(formCard, "Total Boats", boatTotalBoatsField, 24, 376);

        JButton addButton = new JButton("Add Boat");
        JButton updateButton = new JButton("Edit Boat");
        JButton deleteButton = new JButton("Delete Boat");
        JButton clearButton = new JButton("Clear");
        addButton.setBounds(24, 490, 180, 44);
        updateButton.setBounds(222, 490, 180, 44);
        deleteButton.setBounds(24, 550, 180, 44);
        clearButton.setBounds(222, 550, 180, 44);
        stylePrimaryButton(addButton);
        stylePrimaryButton(updateButton);
        styleDangerButton(deleteButton);
        styleSecondaryButton(clearButton);
        addButton.addActionListener(e -> addBoat());
        updateButton.addActionListener(e -> updateBoat());
        deleteButton.addActionListener(e -> deleteBoat());
        clearButton.addActionListener(e -> clearBoatForm());

        formCard.add(availabilityToggle);
        formCard.add(addButton);
        formCard.add(updateButton);
        formCard.add(deleteButton);
        formCard.add(clearButton);

        JPanel body = new JPanel(new BorderLayout(24, 0));
        body.setOpaque(false);
        body.add(tableCard, BorderLayout.CENTER);
        body.add(formCard, BorderLayout.EAST);

        panel.add(heading, BorderLayout.NORTH);
        panel.add(body, BorderLayout.CENTER);
        return panel;
    }

    private JPanel buildBookingsPanel() {
        JPanel panel = createPagePanel();
        panel.add(createPageTitle("Bookings"), BorderLayout.NORTH);
        bookingsModel = new DefaultTableModel(new String[]{"Booking ID", "Customer Name", "Boat Name", "Seats", "Amount", "Payment Method", "Time Slot"}, 0) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        panel.add(wrapTable(createTable(bookingsModel)), BorderLayout.CENTER);
        return panel;
    }

    private JPanel buildUsersPanel() {
        JPanel panel = createPagePanel();
        panel.add(createPageTitle("Users"), BorderLayout.NORTH);
        usersModel = new DefaultTableModel(new String[]{"Name", "Email", "Role"}, 0) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        panel.add(wrapTable(createTable(usersModel)), BorderLayout.CENTER);
        return panel;
    }

    private JPanel buildReportsPanel() {
        JPanel panel = createPagePanel();
        JLabel heading = createPageTitle("Reports");
        JPanel body = new JPanel(new BorderLayout(0, 24));
        body.setOpaque(false);

        JPanel stats = new JPanel(new GridLayout(1, 4, 18, 18));
        stats.setOpaque(false);
        reportRevenueValue = addStatCard(stats, "Total Revenue", "Rs 0");
        reportTicketsValue = addStatCard(stats, "Total Boats", "0");
        reportAvailableValue = addStatCard(stats, "Available Boats", "0");
        reportBookedValue = addStatCard(stats, "Booked Boats", "0");

        JPanel charts = new JPanel(new GridLayout(2, 1, 20, 20));
        charts.setOpaque(false);
        JPanel revenueCard = createCardPanel();
        revenueCard.setLayout(new BorderLayout());
        revenueCard.add(new RevenueChartPanel(), BorderLayout.CENTER);
        JPanel bookingCard = createCardPanel();
        bookingCard.setLayout(new BorderLayout());
        bookingCard.add(new BookingChartPanel(), BorderLayout.CENTER);
        charts.add(revenueCard);
        charts.add(bookingCard);

        body.add(stats, BorderLayout.NORTH);
        body.add(charts, BorderLayout.CENTER);
        panel.add(heading, BorderLayout.NORTH);
        panel.add(body, BorderLayout.CENTER);
        return panel;
    }

    private JPanel buildSettingsPanel() {
        JPanel panel = createPagePanel();
        panel.add(createPageTitle("Settings"), BorderLayout.NORTH);
        JPanel card = createCardPanel();
        card.setLayout(null);

        JLabel theme = new JLabel("Theme: Current premium dashboard theme");
        theme.setBounds(34, 34, 480, 28);
        theme.setFont(new Font("Segoe UI", Font.BOLD, 16));
        theme.setForeground(SIDEBAR_COLOR);
        JLabel account = new JLabel("<html>Account: " + currentUser + "<br>Email: " + currentUserEmail + "<br>Role: Admin</html>");
        account.setBounds(34, 86, 480, 90);
        account.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        account.setForeground(SIDEBAR_COLOR);
        JButton logout = new JButton("Logout");
        logout.setBounds(34, 210, 180, 46);
        styleDangerButton(logout);
        logout.addActionListener(e -> logout());

        card.add(theme);
        card.add(account);
        card.add(logout);
        panel.add(card, BorderLayout.CENTER);
        return panel;
    }

    private void showCard(String cardName) {
        activeCardName = cardName;
        refreshAllPanels();
        cardLayout.show(contentPanel, cardName);
        updateSidebarSelection();
    }

    private void updateSidebarSelection() {
        for (JButton button : sidebarButtons) {
            String cardName = String.valueOf(button.getClientProperty("cardName"));
            boolean selected = cardName.equals(activeCardName);
            button.setBackground(selected ? SIDEBAR_HOVER : SIDEBAR_COLOR);
            button.setForeground(selected ? SIDEBAR_COLOR : Color.WHITE);
            button.setBorder(new RoundedLineBorder(selected ? SIDEBAR_HOVER : SIDEBAR_COLOR, 20, 0, 15, 22, 15, 22));
        }
    }

    private void refreshAllPanels() {
        refreshStats();
        refreshManageBoats();
        refreshBookings();
        refreshUsers();
        refreshReports();
        repaint();
    }

    private void refreshStats() {
        if (totalBoatsValue == null) {
            return;
        }
        totalBoatsValue.setText(String.valueOf(DataStore.boats.size()));
        ticketsSoldValue.setText(String.valueOf(DataStore.totalTicketsSold()));
        revenueValue.setText(String.format("Rs %.0f", DataStore.totalRevenue()));
        availableBoatsValue.setText(String.valueOf(DataStore.availableBoatsCount()));
        bookedBoatsValue.setText(String.valueOf(DataStore.bookedBoatsCount()));
    }

    private void refreshManageBoats() {
        if (manageBoatsModel == null) {
            return;
        }
        manageBoatsModel.setRowCount(0);
        for (Boat boat : DataStore.boats) {
            manageBoatsModel.addRow(new Object[]{
                    boat.getBoatId(),
                    boat.getBoatName(),
                    String.format("Rs %.0f", boat.getPrice()),
                    boat.getSeats(),
                    boat.getType(),
                    boat.getTotalBoats(),
                    boat.getAvailableBoats(),
                    boat.getStatusText()
            });
        }
        restoreBoatSelection();
    }

    private void refreshBookings() {
        if (bookingsModel == null) {
            return;
        }
        bookingsModel.setRowCount(0);
        for (Booking booking : DataStore.bookings) {
            bookingsModel.addRow(new Object[]{
                    booking.getBookingId(),
                    booking.getCustomerName(),
                    booking.getBoatName(),
                    booking.getSeatsBooked(),
                    String.format("Rs %.0f", booking.getAmount()),
                    booking.getPaymentMethod(),
                    booking.getTimeSlot()
            });
        }
    }

    private void refreshUsers() {
        if (usersModel == null) {
            return;
        }
        usersModel.setRowCount(0);
        for (UserAccount user : DataStore.users) {
            usersModel.addRow(new Object[]{user.getName(), user.getEmail(), user.getRole()});
        }
    }

    private void refreshReports() {
        if (reportRevenueValue == null) {
            return;
        }
        reportRevenueValue.setText(String.format("Rs %.0f", DataStore.totalRevenue()));
        reportTicketsValue.setText(String.valueOf(DataStore.totalBoatsCount()));
        reportAvailableValue.setText(String.valueOf(DataStore.availableBoatsCount()));
        reportBookedValue.setText(String.valueOf(DataStore.bookedBoatsCount()));
        if (revenueChartPanel != null) {
            revenueChartPanel.repaint();
        }
        if (bookingChartPanel != null) {
            bookingChartPanel.repaint();
        }
    }

    private void populateBoatFields() {
        int row = manageBoatsTable.getSelectedRow();
        if (row < 0) {
            return;
        }
        selectedBoatId = Integer.parseInt(String.valueOf(manageBoatsModel.getValueAt(row, 0)));
        Boat boat = DataStore.getBoatById(selectedBoatId);
        if (boat == null) {
            return;
        }
        boatNameField.setText(boat.getBoatName());
        boatPriceField.setText(String.format("%.0f", boat.getPrice()));
        boatSeatsField.setText(String.valueOf(boat.getSeats()));
        boatTypeField.setText(boat.getType());
        boatTotalBoatsField.setText(String.valueOf(boat.getTotalBoats()));
        availabilityToggle.setSelected(boat.isAvailability());
    }

    private void restoreBoatSelection() {
        if (selectedBoatId < 0 || manageBoatsTable == null) {
            return;
        }
        for (int row = 0; row < manageBoatsModel.getRowCount(); row++) {
            int boatId = Integer.parseInt(String.valueOf(manageBoatsModel.getValueAt(row, 0)));
            if (boatId == selectedBoatId) {
                manageBoatsTable.setRowSelectionInterval(row, row);
                return;
            }
        }
        selectedBoatId = -1;
    }

    private void addBoat() {
        BoatFormData data = readBoatForm();
        if (data == null) {
            return;
        }
        DataStore.addBoat(data.name, data.price, data.seats, data.type, data.available, data.totalBoats);
        clearBoatForm();
        refreshAllPanels();
        JOptionPane.showMessageDialog(this, "Boat added successfully.", "Boat Added", JOptionPane.INFORMATION_MESSAGE);
    }

    private void updateBoat() {
        if (selectedBoatId < 0) {
            JOptionPane.showMessageDialog(this, "Select a boat first.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }
        BoatFormData data = readBoatForm();
        if (data == null) {
            return;
        }
        DataStore.updateBoat(selectedBoatId, data.name, data.price, data.seats, data.type, data.available, data.totalBoats);
        refreshAllPanels();
        JOptionPane.showMessageDialog(this, "Boat updated successfully.", "Boat Updated", JOptionPane.INFORMATION_MESSAGE);
    }

    private void deleteBoat() {
        if (selectedBoatId < 0) {
            JOptionPane.showMessageDialog(this, "Select a boat first.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this, "Delete selected boat?", "Delete Boat", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            DataStore.deleteBoat(selectedBoatId);
            clearBoatForm();
            refreshAllPanels();
        }
    }

    private BoatFormData readBoatForm() {
        String name = boatNameField.getText().trim();
        String priceText = boatPriceField.getText().trim().replace("Rs", "").trim();
        String seatsText = boatSeatsField.getText().trim();
        String type = boatTypeField.getText().trim();
        String totalBoatsText = boatTotalBoatsField.getText().trim();

        if (name.isEmpty() || priceText.isEmpty() || seatsText.isEmpty() || type.isEmpty() || totalBoatsText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill all boat fields.", "Missing Data", JOptionPane.WARNING_MESSAGE);
            return null;
        }
        if (!isNumeric(priceText) || !isInteger(seatsText) || !isInteger(totalBoatsText)) {
            JOptionPane.showMessageDialog(this, "Price must be numeric, seats and total boats must be integers.", "Invalid Data", JOptionPane.WARNING_MESSAGE);
            return null;
        }
        double price = Double.parseDouble(priceText);
        int seats = Integer.parseInt(seatsText);
        int totalBoats = Integer.parseInt(totalBoatsText);
        if (price < 0 || seats < 0 || totalBoats < 0) {
            JOptionPane.showMessageDialog(this, "Price, seats and total boats cannot be negative.", "Invalid Data", JOptionPane.WARNING_MESSAGE);
            return null;
        }
        return new BoatFormData(name, price, seats, type, availabilityToggle.isSelected() && seats > 0, totalBoats);
    }

    private void clearBoatForm() {
        selectedBoatId = -1;
        if (manageBoatsTable != null) {
            manageBoatsTable.clearSelection();
        }
        boatNameField.setText("");
        boatPriceField.setText("");
        boatSeatsField.setText("");
        boatTypeField.setText("");
        boatTotalBoatsField.setText("");
        availabilityToggle.setSelected(true);
    }

    private void logout() {
        int choice = JOptionPane.showConfirmDialog(this, "Are you sure you want to logout?", "Logout", JOptionPane.YES_NO_OPTION);
        if (choice == JOptionPane.YES_OPTION) {
            dispose();
            new LoginPage();
        }
    }

    private JPanel createPagePanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 30));
        panel.setBackground(LIGHT_BACKGROUND);
        panel.setBorder(new EmptyBorder(32, 36, 32, 36));
        return panel;
    }

    private JLabel createPageTitle(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 34));
        label.setForeground(SIDEBAR_COLOR);
        return label;
    }

    private JPanel createCardPanel() {
        JPanel panel = new RoundedPanel(24);
        panel.setBackground(CARD_WHITE);
        panel.setBorder(new CompoundBorder(
                new RoundedLineBorder(BORDER_COLOR, 24, 1, 0, 0, 0, 0),
                new EmptyBorder(26, 26, 26, 26)
        ));
        return panel;
    }

    private JLabel addStatCard(JPanel parent, String titleText, String valueText) {
        JPanel card = createCardPanel();
        card.setLayout(new GridLayout(2, 1));
        JLabel title = new JLabel(titleText);
        title.setFont(new Font("Segoe UI", Font.BOLD, 15));
        title.setForeground(SIDEBAR_COLOR);
        JLabel value = new JLabel(valueText);
        value.setFont(new Font("Segoe UI", Font.BOLD, 28));
        value.setForeground(SIDEBAR_COLOR);
        card.add(title);
        card.add(value);
        parent.add(card);
        return value;
    }

    private JTable createTable(DefaultTableModel model) {
        JTable table = new JTable(model);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        table.setRowHeight(38);
        table.setBackground(CARD_WHITE);
        table.setForeground(SIDEBAR_COLOR);
        table.setGridColor(BORDER_COLOR);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 15));
        table.getTableHeader().setBackground(CARD_WHITE);
        table.getTableHeader().setForeground(SIDEBAR_COLOR);
        table.getTableHeader().setReorderingAllowed(false);
        return table;
    }

    private JScrollPane wrapTable(JTable table) {
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(new EmptyBorder(16, 0, 0, 0));
        scrollPane.getViewport().setBackground(CARD_WHITE);
        return scrollPane;
    }

    private JTextField createTextField() {
        JTextField field = new JTextField();
        field.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        field.setBorder(new CompoundBorder(new RoundedLineBorder(BORDER_COLOR, 16, 1, 0, 0, 0, 0), new EmptyBorder(0, 14, 0, 14)));
        return field;
    }

    private void addLabelAndComponent(JPanel parent, String labelText, Component component, int x, int y) {
        JLabel label = new JLabel(labelText);
        label.setBounds(x, y, 200, 24);
        label.setFont(new Font("Segoe UI", Font.BOLD, 14));
        label.setForeground(SIDEBAR_COLOR);
        component.setBounds(x, y + 32, 380, 48);
        component.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        parent.add(label);
        parent.add(component);
    }

    private void stylePrimaryButton(JButton button) {
        button.setBackground(SIDEBAR_COLOR);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setFont(new Font("Segoe UI", Font.BOLD, 16));
        button.setBorder(new RoundedLineBorder(SIDEBAR_COLOR, 18, 0, 12, 22, 12, 22));
    }

    private void styleSecondaryButton(JButton button) {
        button.setBackground(SIDEBAR_HOVER);
        button.setForeground(SIDEBAR_COLOR);
        button.setFocusPainted(false);
        button.setFont(new Font("Segoe UI", Font.BOLD, 16));
        button.setBorder(new RoundedLineBorder(SIDEBAR_HOVER, 18, 0, 12, 22, 12, 22));
    }

    private void styleDangerButton(JButton button) {
        button.setBackground(BOOKED_COLOR);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setFont(new Font("Segoe UI", Font.BOLD, 16));
        button.setBorder(new RoundedLineBorder(BOOKED_COLOR, 18, 0, 12, 22, 12, 22));
    }

    private boolean isNumeric(String value) {
        try {
            Double.parseDouble(value);
            return true;
        } catch (NumberFormatException ex) {
            return false;
        }
    }

    private boolean isInteger(String value) {
        try {
            Integer.parseInt(value);
            return true;
        } catch (NumberFormatException ex) {
            return false;
        }
    }

    private static class BoatFormData {
        private final String name;
        private final double price;
        private final int seats;
        private final String type;
        private final boolean available;
        private final int totalBoats;

        private BoatFormData(String name, double price, int seats, String type, boolean available, int totalBoats) {
            this.name = name;
            this.price = price;
            this.seats = seats;
            this.type = type;
            this.available = available;
            this.totalBoats = totalBoats;
        }
    }

    private class StatusCellRenderer extends DefaultTableCellRenderer {
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            Component component = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            String status = value == null ? "" : value.toString();
            if (!isSelected) {
                component.setBackground(CARD_WHITE);
            }
            component.setForeground("Available".equalsIgnoreCase(status) ? AVAILABLE_COLOR : BOOKED_COLOR);
            component.setFont(new Font("Segoe UI", Font.BOLD, 14));
            return component;
        }
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
            g2.setColor(new Color(SIDEBAR_COLOR.getRed(), SIDEBAR_COLOR.getGreen(), SIDEBAR_COLOR.getBlue(), 20));
            g2.fillRoundRect(3, 5, getWidth() - 8, getHeight() - 9, radius, radius);
            g2.setColor(CARD_WHITE);
            g2.fillRoundRect(0, 0, getWidth() - 8, getHeight() - 10, radius, radius);
            g2.dispose();
            super.paintComponent(graphics);
        }
    }

    private static class RoundedLineBorder extends EmptyBorder {
        private final Color color;
        private final int radius;
        private final int thickness;

        private RoundedLineBorder(Color color, int radius, int thickness, int top, int left, int bottom, int right) {
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
