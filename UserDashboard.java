import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;

public class UserDashboard extends JFrame {
    private static final Color LIGHT_BACKGROUND = new Color(197, 224, 230);
    private static final Color CARD_WHITE = new Color(248, 250, 252);
    private static final Color SIDEBAR_COLOR = new Color(18, 40, 63);
    private static final Color SIDEBAR_HOVER = new Color(156, 200, 209);
    private static final Color BORDER_COLOR = new Color(156, 200, 209);
    private static final Color ACCENT_COLOR = new Color(18, 40, 63);
    private static final Color LIGHT_TEXT = new Color(18, 40, 63);
    private static final Color DARK_TEXT = Color.WHITE;
    private static final Color LIGHT_MUTED = new Color(18, 40, 63);
    private static final Color DARK_MUTED = new Color(197, 224, 230);
    private static final Color AVAILABLE_COLOR = new Color(22, 163, 74);
    private static final Color BOOKED_COLOR = new Color(220, 38, 38);
    private static final Color HOVER_BLUE = new Color(156, 200, 209);

    private final String currentUser;
    private final String currentUserEmail;

    private final CardLayout cardLayout;
    private final JPanel contentPanel;

    private JPanel homeContent;
    private JPanel userBookingsPanel;

    private JLabel homeTotalBoatsValue;
    private JLabel homeTicketsSoldValue;
    private JLabel homeRevenueValue;
    private JLabel homeAvailableBoatsValue;

    private DefaultTableModel bookingsModel;
    private JTable bookingsTable;

    private JTextField searchBoatField;

    private boolean darkMode;

    public UserDashboard(UserAccount currentUser) {
        this(
                currentUser == null ? "User" : currentUser.getName(),
                currentUser == null ? "" : currentUser.getEmail()
        );
    }

    public UserDashboard(String userName, String userEmail) {
        this.currentUser = (userName == null || userName.trim().isEmpty()) ? "User" : userName.trim();
        this.currentUserEmail = userEmail == null ? "" : userEmail.trim();

        // Keep UI theme consistent with existing dashboard: start in light mode.
        this.darkMode = false;

        setTitle("Rankala Lake User Dashboard");
        setSize(1480, 900);
        setMinimumSize(new Dimension(1100, 720));
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(backgroundColor());

        root.add(createSidebar(), BorderLayout.WEST);
        root.add(createTopBar(), BorderLayout.NORTH);

        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setBackground(backgroundColor());

        contentPanel.add(buildHomePanel(), "HOME");
        contentPanel.add(buildBookingPanel(), "BOOKING");
        contentPanel.add(buildBoatsPanel(), "BOATS");
        contentPanel.add(buildMyBookingsPanel(), "MYBOOKINGS");
        contentPanel.add(buildSettingsPanel(), "SETTINGS");

        root.add(contentPanel, BorderLayout.CENTER);
        add(root);

        refreshAllPanels();
        cardLayout.show(contentPanel, "HOME");

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

        JLabel subtitle = new JLabel("User Dashboard");
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
        stack.add(createSidebarButton("Book Ticket", "BOOKING"));
        stack.add(Box.createVerticalStrut(14));
        stack.add(createSidebarButton("Boat Inventory", "BOATS"));
        stack.add(Box.createVerticalStrut(14));
        stack.add(createSidebarButton("My Bookings", "MYBOOKINGS"));
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
                cardLayout.show(contentPanel, cardName);
            }
            if ("MYBOOKINGS".equals(cardName) || "HOME".equals(cardName) || "BOATS".equals(cardName) || "BOOKING".equals(cardName)) {
                refreshAllPanels();
            }
        });

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) {
                button.setBackground(SIDEBAR_HOVER);
                button.setForeground(SIDEBAR_COLOR);
            }

            public void mouseExited(java.awt.event.MouseEvent e) {
                button.setBackground(SIDEBAR_COLOR);
                button.setForeground(Color.WHITE);
            }
        });

        return button;
    }

    private JPanel createTopBar() {
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBorder(new EmptyBorder(18, 32, 18, 34));
        topBar.setBackground(CARD_WHITE);

        JLabel title = new JLabel("Rankala Lake User Dashboard");
        title.setFont(new Font("Segoe UI", Font.BOLD, 26));
        title.setForeground(SIDEBAR_COLOR);

        JLabel userLabel = new JLabel("<html><b>Welcome, " + currentUser + "</b><br>Role: User</html>");
        userLabel.setForeground(SIDEBAR_COLOR);
        userLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        userLabel.setBorder(new CompoundBorder(new RoundedLineBorder(BORDER_COLOR, 18, 1, 0, 0, 0, 0), new EmptyBorder(10, 16, 10, 16)));

        topBar.add(title, BorderLayout.WEST);
        topBar.add(userLabel, BorderLayout.EAST);
        return topBar;
    }

    private JPanel buildHomePanel() {
        JPanel panel = createPagePanel();

        JPanel header = new JPanel(new BorderLayout(0, 20));
        header.setOpaque(false);

        JPanel heading = new JPanel(new BorderLayout());
        heading.setOpaque(false);

        JLabel title = createPageTitle("Home");
        JLabel description = new JLabel("Explore boats and start ticket booking.");
        description.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        description.setForeground(mutedTextColor());

        heading.add(title, BorderLayout.NORTH);
        heading.add(description, BorderLayout.SOUTH);

        JPanel summary = new JPanel(new GridLayout(1, 4, 18, 18));
        summary.setOpaque(false);

        homeTotalBoatsValue = addStatCard(summary, "Total Boats", "0");
        homeTicketsSoldValue = addStatCard(summary, "My Tickets", "0");
        homeRevenueValue = addStatCard(summary, "My Spending", "Rs 0");
        homeAvailableBoatsValue = addStatCard(summary, "Available Boats", "0");

        header.add(heading, BorderLayout.NORTH);
        header.add(summary, BorderLayout.CENTER);

        homeContent = new JPanel(new GridLayout(0, 2, 32, 32));
        homeContent.setOpaque(false);

        JScrollPane scrollPane = new JScrollPane(homeContent);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(backgroundColor());

        panel.add(header, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }

    private JComboBox<String> bookingBoatCombo;
    private JTextField bookingCustomerField;
    private JComboBox<Integer> bookingSeatsCombo;
    private JComboBox<String> bookingTimeCombo;
    private JComboBox<String> bookingPaymentCombo;
    private JLabel bookingTotalAmountLabel;

    private JPanel buildBookingPanel() {
        JPanel panel = createPagePanel();
        panel.add(createPageTitle("Book Ticket"), BorderLayout.NORTH);

        JPanel card = createCardPanel();
        card.setLayout(null);
        card.setPreferredSize(new Dimension(900, 520));

        // Customer Name
        JLabel customerLabel = new JLabel("Customer Name");
        customerLabel.setBounds(34, 30, 200, 22);
        customerLabel.setFont(new Font("Segoe UI", Font.BOLD, 15));
        customerLabel.setForeground(textColor());
        
        bookingCustomerField = new JTextField(currentUser);
        bookingCustomerField.setBounds(34, 60, 400, 40);
        bookingCustomerField.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        bookingCustomerField.setEditable(false);

        // Boat Selection
        JLabel boatLabel = new JLabel("Select Boat");
        boatLabel.setBounds(34, 120, 200, 22);
        boatLabel.setFont(new Font("Segoe UI", Font.BOLD, 15));
        boatLabel.setForeground(textColor());
        
        bookingBoatCombo = new JComboBox<>();
        bookingBoatCombo.setBounds(34, 150, 400, 40);
        bookingBoatCombo.setFont(new Font("Segoe UI", Font.PLAIN, 15));

        // Number of Seats
        JLabel seatsLabel = new JLabel("Number of Seats");
        seatsLabel.setBounds(34, 210, 200, 22);
        seatsLabel.setFont(new Font("Segoe UI", Font.BOLD, 15));
        seatsLabel.setForeground(textColor());
        
        bookingSeatsCombo = new JComboBox<>(new Integer[]{1, 2, 3, 4, 5, 6, 7, 8});
        bookingSeatsCombo.setBounds(34, 240, 400, 40);
        bookingSeatsCombo.setFont(new Font("Segoe UI", Font.PLAIN, 15));

        // Time Slot
        JLabel timeLabel = new JLabel("Time Slot");
        timeLabel.setBounds(34, 300, 200, 22);
        timeLabel.setFont(new Font("Segoe UI", Font.BOLD, 15));
        timeLabel.setForeground(textColor());
        
        bookingTimeCombo = new JComboBox<>(new String[]{"9 AM", "11 AM", "1 PM", "3 PM", "5 PM"});
        bookingTimeCombo.setBounds(34, 330, 400, 40);
        bookingTimeCombo.setFont(new Font("Segoe UI", Font.PLAIN, 15));

        // Payment Method
        JLabel paymentLabel = new JLabel("Payment Method");
        paymentLabel.setBounds(34, 390, 200, 22);
        paymentLabel.setFont(new Font("Segoe UI", Font.BOLD, 15));
        paymentLabel.setForeground(textColor());
        
        bookingPaymentCombo = new JComboBox<>(new String[]{"Cash", "UPI", "Card"});
        bookingPaymentCombo.setBounds(34, 420, 400, 40);
        bookingPaymentCombo.setFont(new Font("Segoe UI", Font.PLAIN, 15));

        // Total Amount
        JLabel totalLabel = new JLabel("Total Amount");
        totalLabel.setBounds(500, 30, 200, 22);
        totalLabel.setFont(new Font("Segoe UI", Font.BOLD, 15));
        totalLabel.setForeground(textColor());
        
        bookingTotalAmountLabel = new JLabel("Rs 0");
        bookingTotalAmountLabel.setBounds(500, 60, 200, 40);
        bookingTotalAmountLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        bookingTotalAmountLabel.setForeground(ACCENT_COLOR);

        // Update total amount when boat or seats change
        bookingBoatCombo.addActionListener(e -> updateBookingTotal());
        bookingSeatsCombo.addActionListener(e -> updateBookingTotal());

        // Confirm Booking Button
        JButton confirmButton = new JButton("Confirm Booking");
        confirmButton.setBounds(170, 480, 240, 46);
        confirmButton.setBackground(ACCENT_COLOR);
        confirmButton.setForeground(Color.WHITE);
        confirmButton.setFont(new Font("Segoe UI", Font.BOLD, 16));
        confirmButton.setFocusPainted(false);
        confirmButton.setBorder(new RoundedLineBorder(ACCENT_COLOR, 18, 0, 12, 22, 12, 22));
        confirmButton.addActionListener(e -> confirmBooking());

        card.add(customerLabel);
        card.add(bookingCustomerField);
        card.add(boatLabel);
        card.add(bookingBoatCombo);
        card.add(seatsLabel);
        card.add(bookingSeatsCombo);
        card.add(timeLabel);
        card.add(bookingTimeCombo);
        card.add(paymentLabel);
        card.add(bookingPaymentCombo);
        card.add(totalLabel);
        card.add(bookingTotalAmountLabel);
        card.add(confirmButton);

        panel.add(card, BorderLayout.CENTER);
        return panel;
    }

    private void updateBookingTotal() {
        String boatName = (String) bookingBoatCombo.getSelectedItem();
        if (boatName == null) {
            bookingTotalAmountLabel.setText("Rs 0");
            return;
        }
        
        Boat boat = DataStore.getBoatByName(boatName);
        if (boat == null) {
            bookingTotalAmountLabel.setText("Rs 0");
            return;
        }
        
        int seats = (Integer) bookingSeatsCombo.getSelectedItem();
        double total = boat.getPrice() * seats;
        bookingTotalAmountLabel.setText(String.format("Rs %.0f", total));
    }

    private void confirmBooking() {
        String boatName = (String) bookingBoatCombo.getSelectedItem();
        if (boatName == null) {
            JOptionPane.showMessageDialog(this, "Please select a boat.", "Invalid Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        Boat boat = DataStore.getBoatByName(boatName);
        if (boat == null || boat.getSeats() <= 0) {
            JOptionPane.showMessageDialog(this, "Selected boat is not available.", "Boat Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        int seats = (Integer) bookingSeatsCombo.getSelectedItem();
        String timeSlot = (String) bookingTimeCombo.getSelectedItem();
        String paymentMethod = (String) bookingPaymentCombo.getSelectedItem();
        
        if (boat.getSeats() < seats) {
            JOptionPane.showMessageDialog(this, "Not enough seats available.", "Booking Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        double amount = boat.getPrice() * seats;
        boolean booked = DataStore.bookSeats(boatName, seats);
        if (!booked) {
            JOptionPane.showMessageDialog(this, "Unable to complete booking.", "Booking Failed", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        Booking booking = DataStore.addBooking(currentUser, boatName, seats, amount, paymentMethod, timeSlot, currentUserEmail);
        refreshAllPanels();
        
        JOptionPane.showMessageDialog(this, "Booking confirmed successfully! Booking ID: " + booking.getBookingId(), "Success", JOptionPane.INFORMATION_MESSAGE);
    }

    private JPanel buildBoatsPanel() {
        JPanel panel = createPagePanel();

        JPanel topBar = new JPanel(new BorderLayout(16, 0));
        topBar.setOpaque(false);
        topBar.add(createPageTitle("Boat Inventory"), BorderLayout.WEST);

        searchBoatField = new JTextField();
        searchBoatField.setPreferredSize(new Dimension(280, 42));
        searchBoatField.addActionListener(e -> refreshBoatsCards());
        JButton refreshButton = new JButton("Refresh");
        refreshButton.setPreferredSize(new Dimension(130, 42));
        refreshButton.setBackground(ACCENT_COLOR);
        refreshButton.setForeground(Color.WHITE);
        refreshButton.setFocusPainted(false);
        refreshButton.setFont(new Font("Segoe UI", Font.BOLD, 16));
        refreshButton.setBorder(new RoundedLineBorder(ACCENT_COLOR, 18, 0, 12, 22, 12, 22));
        refreshButton.addActionListener(e -> refreshBoatsCards());

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        searchPanel.setOpaque(false);
        searchPanel.add(searchBoatField);
        searchPanel.add(refreshButton);
        topBar.add(searchPanel, BorderLayout.EAST);

        JPanel boatsGrid = new JPanel(new GridLayout(0, 2, 32, 32));
        boatsGrid.setOpaque(false);

        JScrollPane scrollPane = new JScrollPane(boatsGrid);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(backgroundColor());

        refreshBoatsCards(boatsGrid);

        JButton backToHome = new JButton("View Home");
        backToHome.setBackground(HOVER_BLUE);
        backToHome.setForeground(SIDEBAR_COLOR);
        backToHome.setFocusPainted(false);
        backToHome.setFont(new Font("Segoe UI", Font.BOLD, 16));
        backToHome.setBorder(new RoundedLineBorder(HOVER_BLUE, 18, 0, 12, 22, 12, 22));
        backToHome.addActionListener(e -> cardLayout.show(contentPanel, "HOME"));

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottom.setOpaque(false);
        bottom.add(backToHome);

        panel.add(topBar, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(bottom, BorderLayout.SOUTH);

        // Store for repainting by rebuilding content in refreshBoatsCards.
        this.userBookingsPanel = boatsGrid;
        return panel;
    }

    private void refreshBoatsCards() {
        if (userBookingsPanel == null) return;
        refreshBoatsCards((JPanel) userBookingsPanel);
    }

    private void refreshBoatsCards(JPanel grid) {
        grid.removeAll();
        String query = searchBoatField == null ? "" : searchBoatField.getText().trim();
        ArrayList<Boat> filtered = DataStore.searchBoats(query);
        for (Boat boat : filtered) {
            grid.add(createBoatCard(boat));
        }
        grid.revalidate();
        grid.repaint();
    }

    private JPanel buildMyBookingsPanel() {
        JPanel panel = createPagePanel();
        panel.add(createPageTitle("My Bookings"), BorderLayout.NORTH);

        bookingsModel = new DefaultTableModel(new String[]{"Boat Name", "Seats", "Amount", "Payment", "Time Slot"}, 0) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        bookingsTable = createTable(bookingsModel);

        JScrollPane scrollPane = new JScrollPane(bookingsTable);
        scrollPane.setBorder(new EmptyBorder(16, 0, 0, 0));
        scrollPane.getViewport().setBackground(CARD_WHITE);

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);
        wrapper.add(scrollPane, BorderLayout.CENTER);

        panel.add(wrapper, BorderLayout.CENTER);
        return panel;
    }

    private JPanel buildSettingsPanel() {
        JPanel panel = createPagePanel();

        JLabel heading = createPageTitle("Settings");
        panel.add(heading, BorderLayout.NORTH);

        JPanel card = createCardPanel();
        card.setLayout(null);

        JLabel info = new JLabel("Account: " + currentUser + " (User)");
        info.setBounds(34, 34, 480, 28);
        info.setFont(new Font("Segoe UI", Font.BOLD, 16));
        info.setForeground(SIDEBAR_COLOR);

        JLabel email = new JLabel("Email: " + currentUserEmail);
        email.setBounds(34, 86, 480, 26);
        email.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        email.setForeground(SIDEBAR_COLOR);

        JButton logoutBtn = new JButton("Logout");
        logoutBtn.setBounds(34, 150, 180, 46);
        logoutBtn.setBackground(BOOKED_COLOR);
        logoutBtn.setForeground(Color.WHITE);
        logoutBtn.setFocusPainted(false);
        logoutBtn.setFont(new Font("Segoe UI", Font.BOLD, 16));
        logoutBtn.setBorder(new RoundedLineBorder(BOOKED_COLOR, 18, 0, 12, 22, 12, 22));
        logoutBtn.addActionListener(e -> logout());

        card.add(info);
        card.add(email);
        card.add(logoutBtn);

        panel.add(card, BorderLayout.CENTER);
        return panel;
    }

    public void refreshAllPanels() {
        refreshHomeCards();
        refreshHomeSummary();
        refreshBookingsTable();
        refreshBookingBoatCombo();
    }

    public String getCurrentUser() {
        return currentUser;
    }

    public String getCurrentUserEmail() {
        return currentUserEmail;
    }

    private void refreshBookingBoatCombo() {
        if (bookingBoatCombo == null) return;
        bookingBoatCombo.removeAllItems();
        for (Boat boat : DataStore.boats) {
            if (boat.getSeats() > 0 && boat.isAvailability()) {
                bookingBoatCombo.addItem(boat.getBoatName());
            }
        }
        updateBookingTotal();
    }

    private void refreshHomeSummary() {
        if (homeTotalBoatsValue == null) return;

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

    private void refreshHomeCards() {
        if (homeContent == null) return;
        homeContent.removeAll();
        for (Boat boat : DataStore.boats) {
            homeContent.add(createBoatCard(boat));
        }
        homeContent.revalidate();
        homeContent.repaint();
    }

    private void refreshBookingsTable() {
        if (bookingsModel == null) return;
        bookingsModel.setRowCount(0);

        for (Booking booking : DataStore.bookings) {
            if (!isCurrentUserBooking(booking)) continue;

            bookingsModel.addRow(new Object[]{
                    booking.getBoatName(),
                    booking.getSeatsBooked(),
                    String.format("Rs %.0f", booking.getAmount()),
                    booking.getPaymentMethod(),
                    booking.getTimeSlot()
            });
        }
    }

    private boolean isCurrentUserBooking(Booking booking) {
        if (booking == null) return false;
        String bookingEmail = booking.getCreatedByEmail();
        if (bookingEmail != null && !bookingEmail.trim().isEmpty() && !currentUserEmail.isEmpty()) {
            return bookingEmail.equalsIgnoreCase(currentUserEmail);
        }
        return booking.getCustomerName() != null && booking.getCustomerName().equalsIgnoreCase(currentUser);
    }

    private JPanel createBoatCard(Boat boat) {
        JPanel card = createCardPanel();
        card.setLayout(null);
        card.setPreferredSize(new Dimension(510, 318));

        JLabel name = new JLabel(boat.getBoatName());
        name.setBounds(32, 28, 350, 36);
        name.setFont(new Font("Segoe UI", Font.BOLD, 25));
        name.setForeground(textColor());

        JLabel type = new JLabel((boat.getType() == null ? "" : boat.getType()) + " Boat");
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
        detailsButton.setBackground(HOVER_BLUE);
        detailsButton.setForeground(SIDEBAR_COLOR);
        detailsButton.setFocusPainted(false);
        detailsButton.setFont(new Font("Segoe UI", Font.BOLD, 16));
        detailsButton.setBorder(new RoundedLineBorder(HOVER_BLUE, 18, 0, 12, 22, 12, 22));
        detailsButton.addActionListener(e -> showBoatDetailsDialog(boat));

        JButton bookButton = new JButton("Book");
        bookButton.setBounds(266, 260, 132, 46);
        bookButton.setBackground(ACCENT_COLOR);
        bookButton.setForeground(Color.WHITE);
        bookButton.setFocusPainted(false);
        bookButton.setFont(new Font("Segoe UI", Font.BOLD, 16));
        bookButton.setBorder(new RoundedLineBorder(ACCENT_COLOR, 18, 0, 12, 22, 12, 22));
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
        card.setBackground(CARD_WHITE);

        JLabel title = new JLabel(boat.getBoatName());
        title.setBounds(28, 24, 340, 36);
        title.setFont(new Font("Segoe UI", Font.BOLD, 28));
        title.setForeground(textColor());

        JLabel type = new JLabel("Type: " + (boat.getType() == null ? "" : boat.getType()));
        type.setBounds(32, 90, 340, 26);
        type.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        type.setForeground(textColor());

        JLabel capacity = new JLabel("Capacity: " + boat.getSeats());
        capacity.setBounds(32, 126, 340, 26);
        capacity.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        capacity.setForeground(textColor());

        JLabel price = new JLabel(String.format("Price: Rs %.0f", boat.getPrice()));
        price.setBounds(32, 162, 340, 26);
        price.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        price.setForeground(textColor());

        JLabel status = new JLabel("Status: " + boat.getStatusText());
        status.setBounds(32, 198, 340, 26);
        status.setFont(new Font("Segoe UI", Font.BOLD, 16));
        status.setForeground(statusColor(boat));

        JButton close = new JButton("Close");
        close.setBounds(132, 262, 140, 42);
        close.setBackground(ACCENT_COLOR);
        close.setForeground(Color.WHITE);
        close.setFocusPainted(false);
        close.setFont(new Font("Segoe UI", Font.BOLD, 16));
        close.setBorder(new RoundedLineBorder(ACCENT_COLOR, 18, 0, 12, 22, 12, 22));
        close.addActionListener(e -> dialog.dispose());

        card.add(title);
        card.add(type);
        card.add(capacity);
        card.add(price);
        card.add(status);
        card.add(close);

        dialog.add(card);
        dialog.setVisible(true);
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
        title.setForeground(textColor());

        JLabel value = new JLabel(valueText);
        value.setFont(new Font("Segoe UI", Font.BOLD, 28));
        value.setForeground(textColor());

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

    private Color backgroundColor() {
        return darkMode ? SIDEBAR_COLOR : LIGHT_BACKGROUND;
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
