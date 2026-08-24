import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
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
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.prefs.Preferences;

public class Dashboard extends JFrame {
    private static final Color LIGHT_BACKGROUND = new Color(197, 224, 230);
    private static final Color DARK_BACKGROUND = new Color(18, 40, 63);
    private static final Color LIGHT_CARD = new Color(248, 250, 252);
    private static final Color DARK_CARD = new Color(18, 40, 63);
    private static final Color SIDEBAR_COLOR = new Color(18, 40, 63);
    private static final Color SIDEBAR_HOVER = new Color(156, 200, 209);
    private static final Color ACCENT_COLOR = new Color(18, 40, 63);
    private static final Color LIGHT_TEXT = new Color(18, 40, 63);
    private static final Color DARK_TEXT = Color.WHITE;
    private static final Color LIGHT_MUTED = new Color(18, 40, 63);
    private static final Color DARK_MUTED = new Color(197, 224, 230);
    private static final Color BORDER_COLOR = new Color(156, 200, 209);
    private static final Color HOVER_BLUE = new Color(156, 200, 209);
    private static final Color AVAILABLE_COLOR = new Color(22, 163, 74);
    private static final Color BOOKED_COLOR = new Color(220, 38, 38);

    private final String currentUser;
    private final String currentUserEmail;
    private final String currentRole;
    private final Preferences preferences;
    private CardLayout cardLayout;
    private JPanel rootContainer;
    private JPanel contentPanel;
    private JPanel sidebarPanel;
    private JPanel topBarPanel;
    private JPanel homeContent;
    private JPanel homeSummaryPanel;
    private DefaultTableModel boatsInventoryModel;
    private DefaultTableModel adminBoatModel;
    private DefaultTableModel userTableModel;
    private JTable boatsInventoryTable;
    private JTable adminBoatTable;
    private JTable userTable;
    private JLabel homeTotalBoatsValue;
    private JLabel homeTicketsSoldValue;
    private JLabel homeRevenueValue;
    private JLabel homeAvailableBoatsValue;
    private JLabel statTicketsValue;
    private JLabel statRevenueValue;
    private JLabel statAvailableValue;
    private JLabel statBookedValue;
    private JLabel userEmptyLabel;
    private RevenueChartPanel revenueChartPanel;
    private JTextField searchBoatField;
    private JTextField adminBoatNameField;
    private JTextField adminBoatPriceField;
    private JTextField adminBoatSeatsField;
    private JTextField adminBoatTypeField;
    private JTextField adminBoatTotalBoatsField;
    private JTextField bookingCustomerField;
    private JComboBox<String> bookingBoatCombo;
    private JComboBox<String> bookingSeatsCombo;
    private JComboBox<String> bookingTimeCombo;
    private JComboBox<String> bookingPaymentCombo;
    private JTextField settingAdminNameField;
    private JCheckBox notificationToggle;
    private JCheckBox darkModeToggle;
    private boolean darkMode;
    private int selectedAdminBoatId = -1;
    private final ArrayList<JButton> sidebarButtons = new ArrayList<>();
    private String activeCardName = "HOME";

    public Dashboard(String userName) {
        this(userName, "", "User");
    }

    public Dashboard(String userName, String role) {
        this(userName, "", role);
    }

    public Dashboard(String userName, String userEmail, String role) {
        currentUser = userName == null || userName.trim().isEmpty() ? "User" : userName.trim();
        currentUserEmail = userEmail == null ? "" : userEmail.trim();
        currentRole = "User";
        activeCardName = "HOME";
        System.out.println("USER DASHBOARD ROLE = " + currentRole);
        preferences = Preferences.userRoot().node(Dashboard.class.getName());
        darkMode = preferences.getBoolean("darkMode", false);

        setTitle("Rankala Lake User Dashboard");
        setSize(1480, 900);
        setMinimumSize(new Dimension(1100, 720));
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        rootContainer = new JPanel(new BorderLayout());
        rootContainer.setBackground(backgroundColor());
        sidebarPanel = createSidebar();
        topBarPanel = createTopBar();
        rootContainer.add(sidebarPanel, BorderLayout.WEST);
        rootContainer.add(topBarPanel, BorderLayout.NORTH);

        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setBackground(backgroundColor());
        contentPanel.add(buildHomePanel(), "HOME");
        contentPanel.add(buildBookingPanel(), "BOOKING");
        contentPanel.add(buildBoatsPanel(), "BOATS");
        contentPanel.add(buildSettingsPanel(), "SETTINGS");
        contentPanel.add(buildUserPanel(), "USER");

        rootContainer.add(contentPanel, BorderLayout.CENTER);
        add(rootContainer);

        loadSettings();
        applyTheme();
        refreshAllPanels();
        cardLayout.show(contentPanel, activeCardName);
        updateSidebarSelection();
        setVisible(true);
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

        JLabel subtitle = new JLabel("Boat Ticket System");
        subtitle.setFont(new Font("Segoe UI", Font.BOLD, 14));
        subtitle.setForeground(SIDEBAR_COLOR);
        subtitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        stack.add(Box.createVerticalStrut(28));
        stack.add(logo);
        stack.add(Box.createVerticalStrut(8));
        stack.add(subtitle);
        stack.add(Box.createVerticalStrut(34));
        stack.add(createSidebarButton("Home", "HOME"));
        stack.add(Box.createVerticalStrut(14));
        stack.add(createSidebarButton("Booking", "BOOKING"));
        stack.add(Box.createVerticalStrut(14));
        stack.add(createSidebarButton("Boats", "BOATS"));
        if (isAdmin()) {
            stack.add(Box.createVerticalStrut(14));
            stack.add(createSidebarButton("Reports", "REPORTS"));
        }
        stack.add(Box.createVerticalStrut(14));
        stack.add(createSidebarButton("My Bookings", "USER"));
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
        topBar.setBackground(cardColor());

        JLabel title = new JLabel("Rankala Lake User Dashboard");
        title.setFont(new Font("Segoe UI", Font.BOLD, 26));
        title.setForeground(textColor());

        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 14, 0));
        rightPanel.setOpaque(false);

        JTextField quickSearch = new JTextField();
        quickSearch.setPreferredSize(new Dimension(300, 44));
        quickSearch.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        quickSearch.setBorder(new CompoundBorder(new RoundedLineBorder(HOVER_BLUE, 18, 1, 0, 0, 0, 0), new EmptyBorder(0, 16, 0, 16)));
        quickSearch.addActionListener(e -> {
            if (searchBoatField != null) {
                searchBoatField.setText(quickSearch.getText().trim());
                showCard("BOATS");
            }
        });

        JLabel userLabel = new JLabel("<html><b>Welcome, " + currentUser + " " + roleIcon() + "</b><br>Role: " + displayRole() + "</html>");
        userLabel.setForeground(mutedTextColor());
        userLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        userLabel.setBorder(new CompoundBorder(new RoundedLineBorder(HOVER_BLUE, 18, 1, 0, 0, 0, 0), new EmptyBorder(10, 16, 10, 16)));

        rightPanel.add(userLabel);
        rightPanel.add(quickSearch);
        topBar.add(title, BorderLayout.WEST);
        topBar.add(rightPanel, BorderLayout.EAST);
        return topBar;
    }

    private JPanel buildHomePanel() {
        JPanel panel = createPagePanel();

        JPanel header = new JPanel(new BorderLayout(0, 20));
        header.setOpaque(false);
        JPanel heading = new JPanel(new BorderLayout());
        heading.setOpaque(false);
        JLabel title = createPageTitle("Home");
        JLabel description = new JLabel("Explore boats and start ticket booking from a premium dashboard.");
        description.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        description.setForeground(mutedTextColor());
        heading.add(title, BorderLayout.NORTH);
        heading.add(description, BorderLayout.SOUTH);

        homeSummaryPanel = new JPanel(new GridLayout(1, 4, 18, 18));
        homeSummaryPanel.setOpaque(false);
        homeTotalBoatsValue = addStatCard(homeSummaryPanel, "Total Boats", "0");
        homeTicketsSoldValue = addStatCard(homeSummaryPanel, "My Tickets", "0");
        homeRevenueValue = addStatCard(homeSummaryPanel, "My Spending", "Rs 0");
        homeAvailableBoatsValue = addStatCard(homeSummaryPanel, "Available Boats", "0");

        header.add(heading, BorderLayout.NORTH);
        header.add(homeSummaryPanel, BorderLayout.CENTER);

        homeContent = new JPanel(new GridLayout(0, 2, 32, 32));
        homeContent.setOpaque(false);

        JScrollPane scrollPane = new JScrollPane(homeContent);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(backgroundColor());

        panel.add(header, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }

    private JPanel buildBookingPanel() {
        JPanel panel = createPagePanel();
        JLabel heading = createPageTitle("Booking");

        JPanel formCard = createCardPanel();
        formCard.setLayout(null);

        bookingCustomerField = createTextField();
        bookingCustomerField.setText(currentUser);
        bookingCustomerField.setEditable(false);
        bookingBoatCombo = new JComboBox<>();
        bookingSeatsCombo = new JComboBox<>(new String[]{"1", "2", "3", "4", "5", "6", "7", "8"});
        bookingTimeCombo = new JComboBox<>(new String[]{"9 AM", "11 AM", "1 PM", "3 PM", "5 PM"});
        bookingPaymentCombo = new JComboBox<>(new String[]{"Cash", "UPI", "Card"});

        addLabelAndComponent(formCard, "Customer Name", bookingCustomerField, 30, 30);
        addLabelAndComponent(formCard, "Boat Selection", bookingBoatCombo, 30, 120);
        addLabelAndComponent(formCard, "Number of Seats", bookingSeatsCombo, 30, 210);
        addLabelAndComponent(formCard, "Time Slot", bookingTimeCombo, 30, 300);
        addLabelAndComponent(formCard, "Payment Method", bookingPaymentCombo, 30, 390);

        JButton confirmBooking = new JButton("Confirm Booking");
        confirmBooking.setBounds(110, 490, 260, 48);
        stylePrimaryButton(confirmBooking);
        confirmBooking.addActionListener(e -> processBooking());
        formCard.add(confirmBooking);

        panel.add(heading, BorderLayout.NORTH);
        panel.add(formCard, BorderLayout.CENTER);
        return panel;
    }

    private JPanel buildBoatsPanel() {
        JPanel panel = createPagePanel();
        JPanel topBar = new JPanel(new BorderLayout(16, 0));
        topBar.setOpaque(false);
        topBar.add(createPageTitle("Boats Inventory"), BorderLayout.WEST);

        searchBoatField = createTextField();
        searchBoatField.setPreferredSize(new Dimension(280, 42));
        searchBoatField.addActionListener(e -> refreshBoatsPanel());

        JButton refreshButton = new JButton("Refresh");
        refreshButton.setPreferredSize(new Dimension(130, 42));
        stylePrimaryButton(refreshButton);
        refreshButton.addActionListener(e -> refreshBoatsPanel());

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        searchPanel.setOpaque(false);
        searchPanel.add(searchBoatField);
        searchPanel.add(refreshButton);
        topBar.add(searchPanel, BorderLayout.EAST);

        boatsInventoryModel = new DefaultTableModel(new String[]{"Boat ID", "Boat Name", "Price", "Seats", "Type", "Total Boats", "Available Boats", "Status"}, 0) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        boatsInventoryTable = createTable(boatsInventoryModel);
        boatsInventoryTable.getColumnModel().getColumn(7).setCellRenderer(new StatusCellRenderer());
        if (isAdmin()) {
            boatsInventoryTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            boatsInventoryTable.getSelectionModel().addListSelectionListener(e -> {
                if (!e.getValueIsAdjusting()) {
                    populateAdminFieldsFromSelection();
                }
            });
        }

        panel.add(topBar, BorderLayout.NORTH);
        if (isAdmin()) {
            JPanel body = new JPanel(new BorderLayout(24, 0));
            body.setOpaque(false);
            body.add(wrapTable(boatsInventoryTable), BorderLayout.CENTER);
            body.add(buildBoatManagementPanel(), BorderLayout.EAST);
            panel.add(body, BorderLayout.CENTER);
        } else {
            panel.add(wrapTable(boatsInventoryTable), BorderLayout.CENTER);
        }
        return panel;
    }

    private JPanel buildBoatManagementPanel() {
        JPanel formCard = createCardPanel();
        formCard.setLayout(null);
        formCard.setPreferredSize(new Dimension(420, 620));

        adminBoatNameField = createTextField();
        adminBoatPriceField = createTextField();
        adminBoatSeatsField = createTextField();
        adminBoatTypeField = createTextField();
        adminBoatTotalBoatsField = createTextField();

        addLabelAndComponent(formCard, "Boat Name", adminBoatNameField, 24, 24);
        addLabelAndComponent(formCard, "Price", adminBoatPriceField, 24, 112);
        addLabelAndComponent(formCard, "Seats", adminBoatSeatsField, 24, 200);
        addLabelAndComponent(formCard, "Boat Type", adminBoatTypeField, 24, 288);
        addLabelAndComponent(formCard, "Total Boats", adminBoatTotalBoatsField, 24, 376);

        JButton addButton = new JButton("Add Boat");
        JButton updateButton = new JButton("Update Boat");
        JButton deleteButton = new JButton("Delete Boat");
        JButton clearButton = new JButton("Clear");

        addButton.setBounds(24, 450, 176, 44);
        updateButton.setBounds(220, 450, 176, 44);
        deleteButton.setBounds(24, 510, 176, 44);
        clearButton.setBounds(220, 510, 176, 44);

        stylePrimaryButton(addButton);
        stylePrimaryButton(updateButton);
        styleDangerButton(deleteButton);
        styleSecondaryButton(clearButton);

        addButton.addActionListener(e -> addAdminBoat());
        updateButton.addActionListener(e -> updateAdminBoat());
        deleteButton.addActionListener(e -> deleteAdminBoat());
        clearButton.addActionListener(e -> clearAdminFields());

        formCard.add(addButton);
        formCard.add(updateButton);
        formCard.add(deleteButton);
        formCard.add(clearButton);
        return formCard;
    }

    private JPanel buildReportsPanel() {
        JPanel panel = createPagePanel();
        JLabel heading = createPageTitle("Reports");

        JPanel body = new JPanel(new BorderLayout(0, 24));
        body.setOpaque(false);

        JPanel statsGrid = new JPanel(new GridLayout(1, 4, 18, 18));
        statsGrid.setOpaque(false);
        statTicketsValue = addStatCard(statsGrid, "Total Tickets Sold", "0");
        statRevenueValue = addStatCard(statsGrid, "Total Revenue", "Rs 0");
        statAvailableValue = addStatCard(statsGrid, "Available Boats", "0");
        statBookedValue = addStatCard(statsGrid, "Booked Boats", "0");

        JPanel chartCard = createCardPanel();
        chartCard.setLayout(new BorderLayout());
        JLabel chartTitle = new JLabel("Booking Revenue Chart");
        chartTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        chartTitle.setForeground(textColor());
        JLabel friendlyText = new JLabel("Enjoy your journey at Rankala Lake \u2728");
        friendlyText.setFont(new Font("Segoe UI", Font.ITALIC, 15));
        friendlyText.setForeground(mutedTextColor());
        revenueChartPanel = new RevenueChartPanel();
        JPanel chartHeader = new JPanel(new BorderLayout());
        chartHeader.setOpaque(false);
        chartHeader.add(chartTitle, BorderLayout.NORTH);
        chartHeader.add(friendlyText, BorderLayout.SOUTH);
        chartCard.add(chartHeader, BorderLayout.NORTH);
        chartCard.add(revenueChartPanel, BorderLayout.CENTER);

        body.add(statsGrid, BorderLayout.NORTH);
        body.add(chartCard, BorderLayout.CENTER);
        panel.add(heading, BorderLayout.NORTH);
        panel.add(body, BorderLayout.CENTER);
        return panel;
    }

    private JPanel buildSettingsPanel() {
        JPanel panel = createPagePanel();
        JLabel heading = createPageTitle("Settings");

        JPanel card = createCardPanel();
        card.setLayout(null);

        settingAdminNameField = createTextField();
        notificationToggle = new JCheckBox("Enable Notifications");
        notificationToggle.setBounds(40, 132, 240, 30);
        notificationToggle.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        darkModeToggle = new JCheckBox("Dark Mode");
        darkModeToggle.setBounds(40, 172, 240, 30);
        darkModeToggle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        darkModeToggle.addActionListener(e -> {
            darkMode = darkModeToggle.isSelected();
            preferences.putBoolean("darkMode", darkMode);
            applyTheme();
            refreshAllPanels();
        });

        addLabelAndComponent(card, "Account Name", settingAdminNameField, 40, 40);

        JButton saveButton = new JButton("Save Settings");
        saveButton.setBounds(40, 240, 220, 48);
        stylePrimaryButton(saveButton);
        saveButton.addActionListener(e -> saveSettings());

        JButton logoutButton = new JButton("Logout");
        logoutButton.setBounds(280, 240, 220, 48);
        styleDangerButton(logoutButton);
        logoutButton.addActionListener(e -> logout());

        card.add(notificationToggle);
        card.add(darkModeToggle);
        card.add(saveButton);
        card.add(logoutButton);
        panel.add(heading, BorderLayout.NORTH);
        panel.add(card, BorderLayout.CENTER);
        return panel;
    }

    private JPanel buildUserPanel() {
        JPanel panel = createPagePanel();
        JLabel heading = createPageTitle("My Bookings");

        userTableModel = new DefaultTableModel(new String[]{"Customer Name", "Boat Name", "Seats", "Amount"}, 0) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        userTable = createTable(userTableModel);

        userEmptyLabel = new JLabel("No bookings yet.", SwingConstants.CENTER);
        userEmptyLabel.setFont(new Font("Segoe UI", Font.ITALIC, 16));
        userEmptyLabel.setForeground(mutedTextColor());
        userEmptyLabel.setBorder(new EmptyBorder(14, 0, 0, 0));

        JPanel historyPanel = new JPanel(new BorderLayout());
        historyPanel.setOpaque(false);
        historyPanel.add(wrapTable(userTable), BorderLayout.CENTER);
        historyPanel.add(userEmptyLabel, BorderLayout.SOUTH);

        panel.add(heading, BorderLayout.NORTH);
        panel.add(historyPanel, BorderLayout.CENTER);
        return panel;
    }

    private JPanel buildAdminPanel() {
        JPanel panel = createPagePanel();
        JLabel heading = createPageTitle("Admin Panel");

        adminBoatModel = new DefaultTableModel(new String[]{"Boat ID", "Boat Name", "Price", "Seats", "Type", "Total Boats", "Available Boats", "Status"}, 0) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        adminBoatTable = createTable(adminBoatModel);
        adminBoatTable.getColumnModel().getColumn(7).setCellRenderer(new StatusCellRenderer());
        adminBoatTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        adminBoatTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                populateAdminFieldsFromSelection();
            }
        });

        JPanel tableCard = createCardPanel();
        tableCard.setLayout(new BorderLayout());
        tableCard.add(new JScrollPane(adminBoatTable), BorderLayout.CENTER);

        JPanel formCard = createCardPanel();
        formCard.setLayout(null);
        formCard.setPreferredSize(new Dimension(420, 560));

        adminBoatNameField = createTextField();
        adminBoatPriceField = createTextField();
        adminBoatSeatsField = createTextField();
        adminBoatTypeField = createTextField();

        addLabelAndComponent(formCard, "Boat Name", adminBoatNameField, 24, 24);
        addLabelAndComponent(formCard, "Price", adminBoatPriceField, 24, 112);
        addLabelAndComponent(formCard, "Seats", adminBoatSeatsField, 24, 200);
        addLabelAndComponent(formCard, "Boat Type", adminBoatTypeField, 24, 288);

        JButton addButton = new JButton("Add");
        JButton updateButton = new JButton("Update");
        JButton deleteButton = new JButton("Delete");
        JButton clearButton = new JButton("Clear");

        addButton.setBounds(24, 392, 176, 44);
        updateButton.setBounds(220, 392, 176, 44);
        deleteButton.setBounds(24, 452, 176, 44);
        clearButton.setBounds(220, 452, 176, 44);

        stylePrimaryButton(addButton);
        stylePrimaryButton(updateButton);
        styleDangerButton(deleteButton);
        styleSecondaryButton(clearButton);

        addButton.addActionListener(e -> addAdminBoat());
        updateButton.addActionListener(e -> updateAdminBoat());
        deleteButton.addActionListener(e -> deleteAdminBoat());
        clearButton.addActionListener(e -> clearAdminFields());

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

    private void showCard(String name) {
        if ("REPORTS".equals(name) && !isAdmin()) {
            JOptionPane.showMessageDialog(this, "Access Denied. Admin privileges required.", "Access Denied", JOptionPane.WARNING_MESSAGE);
            return;
        }
        activeCardName = name;
        refreshAllPanels();
        cardLayout.show(contentPanel, name);
        updateSidebarSelection();
    }

    private void updateSidebarSelection() {
        for (JButton button : sidebarButtons) {
            String cardName = String.valueOf(button.getClientProperty("cardName"));
            boolean selected = cardName.equals(activeCardName);
            button.setBackground(selected ? HOVER_BLUE : SIDEBAR_COLOR);
            button.setForeground(selected ? SIDEBAR_COLOR : Color.WHITE);
            button.setBorder(new RoundedLineBorder(selected ? HOVER_BLUE : SIDEBAR_COLOR, 20, 0, 15, 22, 15, 22));
        }
    }

    public void refreshAllPanels() {
        refreshHomePanel();
        refreshBookingCombo();
        refreshBoatsPanel();
        refreshAdminBoats();
        refreshReportsPanel();
        refreshUserPanel();
        refreshAdminPanel();
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    public void refreshHomePanel() {
        if (homeContent == null) {
            return;
        }
        updateHomeSummaryCards();
        homeContent.removeAll();
        for (Boat boat : DataStore.boats) {
            homeContent.add(createBoatCard(boat));
        }
        homeContent.revalidate();
        homeContent.repaint();
    }

    public void refreshAdminBoats() {
        if (adminBoatModel == null) {
            return;
        }
        adminBoatModel.setRowCount(0);
        for (Boat boat : DataStore.boats) {
            adminBoatModel.addRow(new Object[]{
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
        if (selectedAdminBoatId > 0) {
            restoreAdminSelection();
        }
    }

    public void refreshBoatsPanel() {
        if (boatsInventoryModel == null) {
            return;
        }
        boatsInventoryModel.setRowCount(0);
        String query = searchBoatField == null ? "" : searchBoatField.getText().trim();
        ArrayList<Boat> filteredBoats = DataStore.searchBoats(query);
        for (Boat boat : filteredBoats) {
            boatsInventoryModel.addRow(new Object[]{
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
        if (selectedAdminBoatId > 0) {
            restoreAdminSelection();
        }
    }

    public void refreshReportsPanel() {
        if (statTicketsValue == null) {
            return;
        }
        statTicketsValue.setText(String.valueOf(DataStore.totalTicketsSold()));
        statRevenueValue.setText(String.format("Rs %.0f", DataStore.totalRevenue()));
        statAvailableValue.setText(String.valueOf(DataStore.availableBoatsCount()));
        statBookedValue.setText(String.valueOf(DataStore.bookedBoatsCount()));
        if (revenueChartPanel != null) {
            revenueChartPanel.repaint();
        }
    }

    private void updateHomeSummaryCards() {
        if (homeTotalBoatsValue == null) {
            return;
        }
        homeTotalBoatsValue.setText(String.valueOf(DataStore.boats.size()));
        int myTickets = 0;
        double mySpending = 0;
        for (Booking booking : DataStore.bookings) {
            if (isCurrentUserBooking(booking)) {
                myTickets += booking.getSeatsBooked();
                mySpending += booking.getAmount();
            }
        }
        homeTicketsSoldValue.setText(String.valueOf(myTickets));
        homeRevenueValue.setText(String.format("Rs %.0f", mySpending));
        homeAvailableBoatsValue.setText(String.valueOf(DataStore.availableBoatsCount()));
    }

    private void refreshUserPanel() {
    if (userTableModel == null) {
        return;
    }

    userTableModel.setRowCount(0);

    boolean hasBookings = false;

    for (Booking booking : DataStore.bookings) {

        if (!isCurrentUserBooking(booking)) {
            continue;
        }

        hasBookings = true;

        userTableModel.addRow(new Object[]{
                booking.getCustomerName(),
                booking.getBoatName(),
                booking.getSeatsBooked(),
                String.format("Rs %.0f", booking.getAmount())
        });
    }

    if (userEmptyLabel != null) {
        userEmptyLabel.setVisible(!hasBookings);
    }
}

    private boolean isCurrentUserBooking(Booking booking) {
        if (booking == null) {
            return false;
        }
        String bookingEmail = booking.getCreatedByEmail();
        if (bookingEmail != null && !bookingEmail.trim().isEmpty() && !currentUserEmail.isEmpty()) {
            return bookingEmail.equalsIgnoreCase(currentUserEmail);
        }
        return booking.getCustomerName() != null && booking.getCustomerName().equalsIgnoreCase(currentUser);
    }

    private void refreshAdminPanel() {
        refreshBoatsPanel();
    }

    private void refreshBookingCombo() {
        if (bookingBoatCombo == null) {
            return;
        }
        Object selected = bookingBoatCombo.getSelectedItem();
        bookingBoatCombo.removeAllItems();
        for (Boat boat : DataStore.boats) {
            if (boat.getSeats() > 0 && boat.isAvailability()) {
                bookingBoatCombo.addItem(boat.getBoatName());
            }
        }
        if (selected != null) {
            bookingBoatCombo.setSelectedItem(selected);
        }
    }

    private JPanel createBoatCard(Boat boat) {
        JPanel card = createCardPanel();
        card.setLayout(null);
        card.setPreferredSize(new Dimension(510, 318));

        JLabel name = new JLabel(boat.getBoatName());
        name.setBounds(32, 28, 350, 36);
        name.setFont(new Font("Segoe UI", Font.BOLD, 25));
        name.setForeground(textColor());

        JLabel type = new JLabel(boat.getType() + " Boat");
        type.setBounds(32, 70, 280, 24);
        type.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        type.setForeground(mutedTextColor());

        JLabel price = new JLabel(String.format("Price: Rs %.0f", boat.getPrice()));
        JLabel seats = new JLabel("Seats: " + boat.getSeats());
        JLabel location = new JLabel("Location: Rankala Lake");
        JLabel available = new JLabel("Available: " + boat.getAvailableBoats() + " / " + boat.getTotalBoats() + " Boats");
        price.setBounds(32, 120, 280, 26);
        seats.setBounds(32, 154, 280, 26);
        location.setBounds(32, 188, 280, 26);
        available.setBounds(32, 222, 280, 26);

        for (JLabel label : new JLabel[]{price, seats, location}) {
            label.setFont(new Font("Segoe UI", Font.PLAIN, 16));
            label.setForeground(textColor());
        }
        available.setFont(new Font("Segoe UI", Font.BOLD, 16));
        available.setForeground(statusColor(boat));

        JButton detailsButton = new JButton("Details");
        detailsButton.setBounds(112, 260, 132, 46);
        styleSecondaryButton(detailsButton);
        detailsButton.addActionListener(e -> showBoatDetailsDialog(boat));

        JButton bookButton = new JButton("Book");
        bookButton.setBounds(266, 260, 132, 46);
        stylePrimaryButton(bookButton);
        bookButton.setEnabled(boat.getAvailableBoats() > 0);
        bookButton.addActionListener(e -> new BookingPage(this, boat.getBoatName()));

        card.add(name);
        card.add(type);
        card.add(price);
        card.add(seats);
        card.add(location);
        card.add(available);
        card.add(detailsButton);
        card.add(bookButton);
        return card;
    }

    private void showBoatDetailsDialog(Boat boat) {
        JDialog dialog = new JDialog(this, "Boat Details", true);
        dialog.setSize(430, 360);
        dialog.setLocationRelativeTo(this);
        dialog.setResizable(false);

        JPanel card = createCardPanel();
        card.setLayout(null);
        card.setBackground(cardColor());

        JLabel title = new JLabel(boat.getBoatName());
        title.setBounds(28, 24, 340, 36);
        title.setFont(new Font("Segoe UI", Font.BOLD, 28));
        title.setForeground(textColor());

        JLabel type = detailLine("Type: " + boat.getType(), 78);
        JLabel capacity = detailLine("Capacity: " + boat.getSeats(), 112);
        JLabel duration = detailLine("Ride Duration: " + rideDurationFor(boat) + " mins", 146);
        JLabel price = detailLine(String.format("Price: Rs %.0f", boat.getPrice()), 180);
        JLabel status = detailLine("Status: " + boat.getStatusText(), 214);
        status.setFont(new Font("Segoe UI", Font.BOLD, 16));
        status.setForeground(statusColor(boat));

        JButton close = new JButton("Close");
        close.setBounds(132, 262, 140, 42);
        stylePrimaryButton(close);
        close.addActionListener(e -> dialog.dispose());

        card.add(title);
        card.add(type);
        card.add(capacity);
        card.add(duration);
        card.add(price);
        card.add(status);
        card.add(close);
        dialog.add(card);
        dialog.setVisible(true);
    }

    private JLabel detailLine(String text, int y) {
        JLabel label = new JLabel(text);
        label.setBounds(32, y, 340, 26);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        label.setForeground(textColor());
        return label;
    }

    private void processBooking() {
        String customerName = bookingCustomerField.getText().trim();
        String boatName = (String) bookingBoatCombo.getSelectedItem();
        String seatsText = (String) bookingSeatsCombo.getSelectedItem();
        String timeSlot = (String) bookingTimeCombo.getSelectedItem();
        String paymentMethod = (String) bookingPaymentCombo.getSelectedItem();

        if (customerName.isEmpty() || boatName == null || seatsText == null || timeSlot == null || paymentMethod == null) {
            JOptionPane.showMessageDialog(this, "Please complete the booking form.", "Missing Information", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int seatsRequested = Integer.parseInt(seatsText);
        Boat selectedBoat = DataStore.getBoatByName(boatName);
        if (selectedBoat == null || selectedBoat.getSeats() < seatsRequested || !selectedBoat.isAvailability()) {
            JOptionPane.showMessageDialog(this, "Not enough seats available for this boat.", "Booking Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        double amount = selectedBoat.getPrice() * seatsRequested;
        if (!DataStore.bookSeats(boatName, seatsRequested)) {
            JOptionPane.showMessageDialog(this, "Unable to complete booking. Please choose a different boat or reduce seats.", "Booking Failed", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!isAdmin()) {
            customerName = currentUser;
        }

        Booking booking = DataStore.addBooking(customerName, boatName, seatsRequested, amount, paymentMethod, timeSlot, currentUserEmail);
        refreshAllPanels();
        JOptionPane.showMessageDialog(this, "Your boat is booked successfully \uD83D\uDEA4", "Booking Successful", JOptionPane.INFORMATION_MESSAGE);
        showReceiptDialog(booking);
        bookingCustomerField.setText(currentUser);
    }

    public void showReceiptDialog(Booking booking) {
        JTextArea receipt = new JTextArea();
        receipt.setEditable(false);
        receipt.setFont(new Font("Monospaced", Font.PLAIN, 15));
        receipt.setBackground(cardColor());
        receipt.setForeground(textColor());
        receipt.setText(String.format(
                "===== BOAT TICKET =====%n%n" +
                        "Booking ID: %d%n" +
                        "Customer: %s%n" +
                        "Boat: %s%n" +
                        "Seats: %d%n" +
                        "Time: %s%n" +
                        "Payment: %s%n%n" +
                        "Total: Rs %.0f%n%n" +
                        "# Enjoy your ride!",
                booking.getBookingId(),
                booking.getCustomerName(),
                booking.getBoatName(),
                booking.getSeatsBooked(),
                booking.getTimeSlot(),
                booking.getPaymentMethod(),
                booking.getAmount()));
        receipt.setBorder(new EmptyBorder(18, 18, 18, 18));
        JOptionPane.showMessageDialog(this, receipt, "Professional Booking Receipt", JOptionPane.INFORMATION_MESSAGE);
    }

    private void addAdminBoat() {
        BoatFormData data = readBoatForm();
        if (data == null) {
            return;
        }
        DataStore.addBoat(data.name, data.price, data.seats, data.type, data.seats > 0, data.totalBoats);
        clearAdminFields();
        refreshAllPanels();
        JOptionPane.showMessageDialog(this, "Boat added successfully.", "Boat Added", JOptionPane.INFORMATION_MESSAGE);
    }

    private void updateAdminBoat() {
        if (selectedAdminBoatId < 0) {
            JOptionPane.showMessageDialog(this, "Select a boat row in the admin table first.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }
        BoatFormData data = readBoatForm();
        if (data == null) {
            return;
        }
        DataStore.updateBoat(selectedAdminBoatId, data.name, data.price, data.seats, data.type, data.seats > 0, data.totalBoats);
        refreshAllPanels();
        JOptionPane.showMessageDialog(this, "Boat updated successfully.", "Boat Updated", JOptionPane.INFORMATION_MESSAGE);
    }

    private void deleteAdminBoat() {
        if (selectedAdminBoatId < 0) {
            JOptionPane.showMessageDialog(this, "Select a boat row in the admin table first.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this, "Delete selected boat?", "Delete Boat", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            DataStore.deleteBoat(selectedAdminBoatId);
            clearAdminFields();
            refreshAllPanels();
            JOptionPane.showMessageDialog(this, "Boat removed.", "Boat Deleted", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private BoatFormData readBoatForm() {
        String name = adminBoatNameField.getText().trim();
        String priceText = adminBoatPriceField.getText().trim().replace("Rs", "").trim();
        String seatsText = adminBoatSeatsField.getText().trim();
        String type = adminBoatTypeField.getText().trim();
        String totalBoatsText = adminBoatTotalBoatsField.getText().trim();

        if (name.isEmpty() || priceText.isEmpty() || seatsText.isEmpty() || type.isEmpty() || totalBoatsText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill all boat fields.", "Incomplete Data", JOptionPane.WARNING_MESSAGE);
            return null;
        }
        if (!isNumeric(priceText) || !isInteger(seatsText) || !isInteger(totalBoatsText)) {
            JOptionPane.showMessageDialog(this, "Boat price must be a number and seats and total boats must be integers.", "Invalid Input", JOptionPane.WARNING_MESSAGE);
            return null;
        }

        double price = Double.parseDouble(priceText);
        int seats = Integer.parseInt(seatsText);
        int totalBoats = Integer.parseInt(totalBoatsText);
        if (price < 0 || seats < 0 || totalBoats < 0) {
            JOptionPane.showMessageDialog(this, "Price, seats and total boats cannot be negative.", "Invalid Input", JOptionPane.WARNING_MESSAGE);
            return null;
        }
        return new BoatFormData(name, price, seats, type, totalBoats);
    }

    private void populateAdminFieldsFromSelection() {
        if (boatsInventoryTable == null || boatsInventoryModel == null) {
            return;
        }
        int row = boatsInventoryTable.getSelectedRow();
        if (row < 0) {
            return;
        }
        selectedAdminBoatId = Integer.parseInt(String.valueOf(boatsInventoryModel.getValueAt(row, 0)));
        Boat selectedBoat = DataStore.getBoatById(selectedAdminBoatId);
        if (selectedBoat == null) {
            return;
        }
        adminBoatNameField.setText(selectedBoat.getBoatName());
        adminBoatPriceField.setText(String.format("%.0f", selectedBoat.getPrice()));
        adminBoatSeatsField.setText(String.valueOf(selectedBoat.getSeats()));
        adminBoatTypeField.setText(selectedBoat.getType());
        adminBoatTotalBoatsField.setText(String.valueOf(selectedBoat.getTotalBoats()));
    }

    private void restoreAdminSelection() {
        if (boatsInventoryTable == null || boatsInventoryModel == null) {
            return;
        }
        for (int row = 0; row < boatsInventoryModel.getRowCount(); row++) {
            int boatId = Integer.parseInt(String.valueOf(boatsInventoryModel.getValueAt(row, 0)));
            if (boatId == selectedAdminBoatId) {
                boatsInventoryTable.setRowSelectionInterval(row, row);
                return;
            }
        }
        selectedAdminBoatId = -1;
    }

    private void clearAdminFields() {
        selectedAdminBoatId = -1;
        if (boatsInventoryTable != null) {
            boatsInventoryTable.clearSelection();
        }
        if (adminBoatNameField != null) {
            adminBoatNameField.setText("");
        }
        if (adminBoatPriceField != null) {
            adminBoatPriceField.setText("");
        }
        if (adminBoatSeatsField != null) {
            adminBoatSeatsField.setText("");
        }
        if (adminBoatTypeField != null) {
            adminBoatTypeField.setText("");
        }
        if (adminBoatTotalBoatsField != null) {
            adminBoatTotalBoatsField.setText("");
        }
    }

    private void saveSettings() {
        preferences.put("adminName", settingAdminNameField.getText().trim());
        preferences.putBoolean("notifications", notificationToggle.isSelected());
        preferences.putBoolean("darkMode", darkMode);
        JOptionPane.showMessageDialog(this, "Settings saved.", "Settings", JOptionPane.INFORMATION_MESSAGE);
    }

    private void loadSettings() {
        if (settingAdminNameField != null) {
            settingAdminNameField.setText(preferences.get("adminName", currentUser));
        }
        if (notificationToggle != null) {
            notificationToggle.setSelected(preferences.getBoolean("notifications", true));
        }
        if (darkModeToggle != null) {
            darkModeToggle.setSelected(darkMode);
        }
    }

    private boolean isAdmin() {
        return "Admin".equalsIgnoreCase(currentRole);
    }

    public boolean hasAdminRole() {
        return isAdmin();
    }

    private String normalizeRole(String role) {
        if (role == null) {
            return "User";
        }
        return "Admin".equalsIgnoreCase(role.trim()) ? "Admin" : "User";
    }

    public String getCurrentUser() {
        return currentUser;
    }

    public String getCurrentUserEmail() {
        return currentUserEmail;
    }

    private String displayRole() {
        return isAdmin() ? "Admin" : "User";
    }

    private String roleIcon() {
        return isAdmin() ? "\uD83D\uDC4B" : "\uD83C\uDF0A";
    }

    private void logout() {
        int choice = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to logout?",
                "Logout",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );
        if (choice == JOptionPane.YES_OPTION) {
            dispose();
            new LoginPage();
        }
    }

    private JPanel createPagePanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 30));
        panel.setBackground(backgroundColor());
        panel.setBorder(new EmptyBorder(32, 36, 32, 36));
        return panel;
    }

    private JLabel createPageTitle(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.BOLD, 34));
        label.setForeground(textColor());
        return label;
    }

    private JPanel createCardPanel() {
        JPanel panel = new RoundedPanel(24);
        panel.setBackground(cardColor());
        panel.setBorder(new CompoundBorder(
                new RoundedLineBorder(darkMode ? HOVER_BLUE : BORDER_COLOR, 24, 1, 0, 0, 0, 0),
                new EmptyBorder(26, 26, 26, 26)
        ));
        return panel;
    }

    private JTable createTable(DefaultTableModel model) {
        JTable table = new JTable(model);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        table.setRowHeight(38);
        table.setBackground(cardColor());
        table.setForeground(textColor());
        table.setGridColor(HOVER_BLUE);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 15));
        table.getTableHeader().setBackground(cardColor());
        table.getTableHeader().setForeground(textColor());
        table.getTableHeader().setReorderingAllowed(false);
        return table;
    }

    private JScrollPane wrapTable(JTable table) {
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(new EmptyBorder(16, 0, 0, 0));
        scrollPane.getViewport().setBackground(cardColor());
        return scrollPane;
    }

    private JTextField createTextField() {
        JTextField field = new JTextField();
        field.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        field.setBorder(new CompoundBorder(new RoundedLineBorder(HOVER_BLUE, 16, 1, 0, 0, 0, 0), new EmptyBorder(0, 14, 0, 14)));
        return field;
    }

    private void addLabelAndComponent(JPanel parent, String labelText, Component component, int x, int y) {
        JLabel label = new JLabel(labelText);
        label.setBounds(x, y, 200, 24);
        label.setFont(new Font("Segoe UI", Font.BOLD, 14));
        label.setForeground(textColor());
        component.setBounds(x, y + 32, 380, 48);
        component.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        parent.add(label);
        parent.add(component);
    }

    private JLabel addStatCard(JPanel parent, String titleText, String valueText) {
        JPanel card = createCardPanel();
        card.setLayout(new GridLayout(2, 1));

        JLabel title = new JLabel(titleText);
        title.setFont(new Font("Segoe UI", Font.BOLD, 17));
        title.setForeground(textColor());

        JLabel value = new JLabel(valueText);
        value.setFont(new Font("Segoe UI", Font.BOLD, 30));
        value.setForeground(ACCENT_COLOR);

        card.add(title);
        card.add(value);
        parent.add(card);
        return value;
    }

    private void stylePrimaryButton(JButton button) {
        button.setBackground(ACCENT_COLOR);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setFont(new Font("Segoe UI", Font.BOLD, 16));
        button.setBorder(new RoundedLineBorder(ACCENT_COLOR, 18, 0, 12, 22, 12, 22));
        addButtonHover(button, ACCENT_COLOR, HOVER_BLUE, Color.WHITE, SIDEBAR_COLOR);
    }

    private void styleSecondaryButton(JButton button) {
        button.setBackground(HOVER_BLUE);
        button.setForeground(SIDEBAR_COLOR);
        button.setFocusPainted(false);
        button.setFont(new Font("Segoe UI", Font.BOLD, 16));
        button.setBorder(new RoundedLineBorder(HOVER_BLUE, 18, 0, 12, 22, 12, 22));
        addButtonHover(button, HOVER_BLUE, LIGHT_BACKGROUND, SIDEBAR_COLOR, SIDEBAR_COLOR);
    }

    private void styleDangerButton(JButton button) {
        button.setBackground(BOOKED_COLOR);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setFont(new Font("Segoe UI", Font.BOLD, 16));
        button.setBorder(new RoundedLineBorder(BOOKED_COLOR, 18, 0, 12, 22, 12, 22));
        addButtonHover(button, BOOKED_COLOR, HOVER_BLUE, Color.WHITE, SIDEBAR_COLOR);
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

    private void applyTheme() {
        if (rootContainer == null) {
            return;
        }
        applyThemeToComponent(rootContainer);
        if (topBarPanel != null) {
            topBarPanel.setBackground(cardColor());
        }
        if (contentPanel != null) {
            contentPanel.setBackground(backgroundColor());
        }
        if (darkModeToggle != null) {
            darkModeToggle.setSelected(darkMode);
        }
        rootContainer.revalidate();
        rootContainer.repaint();
    }

    private void applyThemeToComponent(Component component) {
        if (component instanceof JPanel && component != sidebarPanel) {
            JPanel panel = (JPanel) component;
            if (panel.isOpaque()) {
                panel.setBackground(isCard(panel) ? cardColor() : backgroundColor());
            }
        } else if (component instanceof JLabel) {
            JLabel label = (JLabel) component;
            if (!Color.WHITE.equals(label.getForeground()) && !ACCENT_COLOR.equals(label.getForeground())) {
                label.setForeground(textColor());
            }
        } else if (component instanceof JCheckBox) {
            JCheckBox checkBox = (JCheckBox) component;
            checkBox.setBackground(cardColor());
            checkBox.setForeground(textColor());
        } else if (component instanceof JTextField) {
            JTextField field = (JTextField) component;
            field.setBackground(darkMode ? DARK_BACKGROUND : LIGHT_CARD);
            field.setForeground(textColor());
            field.setCaretColor(textColor());
        } else if (component instanceof JTable) {
            JTable table = (JTable) component;
            table.setBackground(cardColor());
            table.setForeground(textColor());
            table.setGridColor(HOVER_BLUE);
        } else if (component instanceof JScrollPane) {
            JScrollPane scrollPane = (JScrollPane) component;
            scrollPane.getViewport().setBackground(backgroundColor());
        }

        if (component instanceof JPanel) {
            Component[] children = ((JPanel) component).getComponents();
            for (Component child : children) {
                applyThemeToComponent(child);
            }
        } else if (component instanceof JScrollPane) {
            applyThemeToComponent(((JScrollPane) component).getViewport().getView());
        }
    }

    private boolean isCard(JPanel panel) {
        return panel.getBorder() instanceof CompoundBorder;
    }

    private Color backgroundColor() {
        return darkMode ? DARK_BACKGROUND : LIGHT_BACKGROUND;
    }

    private Color cardColor() {
        return darkMode ? DARK_CARD : LIGHT_CARD;
    }

    private Color textColor() {
        return darkMode ? DARK_TEXT : LIGHT_TEXT;
    }

    private Color mutedTextColor() {
        return darkMode ? DARK_MUTED : LIGHT_MUTED;
    }

    private Color statusColor(Boat boat) {
        return boat.getSeats() > 0 && boat.isAvailability() ? AVAILABLE_COLOR : BOOKED_COLOR;
    }

    private int rideDurationFor(Boat boat) {
        String type = boat.getType() == null ? "" : boat.getType().toLowerCase();
        if (type.contains("luxury") || type.contains("family")) {
            return 30;
        }
        if (type.contains("speed")) {
            return 20;
        }
        return 25;
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
        private final int totalBoats;

        private BoatFormData(String name, double price, int seats, String type, int totalBoats) {
            this.name = name;
            this.price = price;
            this.seats = seats;
            this.type = type;
            this.totalBoats = totalBoats;
        }
    }

    private class StatusCellRenderer extends DefaultTableCellRenderer {
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            Component component = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            String status = value == null ? "" : value.toString();
            if (!isSelected) {
                component.setBackground(cardColor());
            }
            component.setForeground("Available".equalsIgnoreCase(status) ? AVAILABLE_COLOR : BOOKED_COLOR);
            component.setFont(new Font("Segoe UI", Font.BOLD, 14));
            return component;
        }
    }

    private class RevenueChartPanel extends JPanel {
        private RevenueChartPanel() {
            setOpaque(false);
            setPreferredSize(new Dimension(800, 360));
        }

        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int width = getWidth();
            int height = getHeight();
            int left = 60;
            int bottom = height - 54;
            int top = 40;
            int chartHeight = Math.max(1, bottom - top);

            g2.setColor(HOVER_BLUE);
            g2.drawLine(left, top, left, bottom);
            g2.drawLine(left, bottom, width - 30, bottom);

            if (DataStore.bookings.isEmpty()) {
                g2.setColor(mutedTextColor());
                g2.setFont(new Font("Segoe UI", Font.PLAIN, 16));
                g2.drawString("No booking data yet", left + 20, top + 40);
                g2.dispose();
                return;
            }

            double max = 0;
            for (Booking booking : DataStore.bookings) {
                if (booking.getAmount() > max) {
                    max = booking.getAmount();
                }
            }

            int count = DataStore.bookings.size();
            int availableWidth = Math.max(1, width - left - 70);
            int gap = 14;
            int barWidth = Math.max(28, Math.min(70, (availableWidth - (count + 1) * gap) / count));
            int x = left + gap;

            g2.setFont(new Font("Segoe UI", Font.BOLD, 12));
            for (Booking booking : DataStore.bookings) {
                int barHeight = (int) ((booking.getAmount() / max) * (chartHeight - 20));
                int y = bottom - barHeight;
                g2.setColor(ACCENT_COLOR);
                g2.fillRoundRect(x, y, barWidth, barHeight, 10, 10);
                g2.setColor(textColor());
                g2.drawString("Rs " + String.format("%.0f", booking.getAmount()), x - 2, y - 8);
                g2.setColor(mutedTextColor());
                g2.drawString("B" + booking.getBookingId(), x + 4, bottom + 22);
                x += barWidth + gap;
            }
            g2.dispose();
        }
    }

    private class RoundedPanel extends JPanel {
        private final int radius;

        private RoundedPanel(int radius) {
            this.radius = radius;
            setOpaque(false);
        }

        protected void paintComponent(Graphics graphics) {
            Graphics2D g2 = (Graphics2D) graphics.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(new Color(SIDEBAR_COLOR.getRed(), SIDEBAR_COLOR.getGreen(), SIDEBAR_COLOR.getBlue(), darkMode ? 32 : 20));
            g2.fillRoundRect(3, 5, getWidth() - 8, getHeight() - 9, radius, radius);
            g2.setColor(cardColor());
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
