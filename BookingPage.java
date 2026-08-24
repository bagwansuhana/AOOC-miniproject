import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;

public class BookingPage extends JFrame {
    private static final Color LIGHT_BLUE_BACKGROUND = new Color(197, 224, 230);
    private static final Color DARK_NAVY = new Color(18, 40, 63);
    private static final Color CARD_WHITE = new Color(248, 250, 252);
    private static final Color HOVER_BLUE = new Color(156, 200, 209);

    private final Dashboard parent;
    private final UserDashboard userParent;
    private JComboBox<String> boatCombo;
    private JTextField customerField;
    private JComboBox<String> seatsCombo;
    private JComboBox<String> timeCombo;
    private JComboBox<String> paymentCombo;

    public BookingPage(Dashboard parent, String selectedBoatName) {
        this.parent = parent;
        this.userParent = null;
        initialize(selectedBoatName);
    }

    public BookingPage(UserDashboard parent, String selectedBoatName) {
        this.parent = null;
        this.userParent = parent;
        initialize(selectedBoatName);
    }

    private void initialize(String selectedBoatName) {
        setTitle("Boat Ticket Booking");
        setSize(520, 640);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel root = new JPanel();
        root.setBackground(LIGHT_BLUE_BACKGROUND);
        root.setLayout(null);

        JPanel card = new JPanel(null);
        card.setBounds(30, 20, 460, 580);
        card.setBackground(CARD_WHITE);
        card.setBorder(new CompoundBorder(new LineBorder(HOVER_BLUE, 1, true), new EmptyBorder(24, 24, 24, 24)));

        JLabel header = new JLabel("Boat Ticket Booking");
        header.setBounds(24, 20, 360, 32);
        header.setFont(new Font("Segoe UI", Font.BOLD, 26));
        header.setForeground(DARK_NAVY);

        JLabel boatLabel = new JLabel("Boat");
        boatLabel.setBounds(24, 80, 200, 22);
        boatLabel.setFont(new Font("Segoe UI", Font.BOLD, 15));
        boatLabel.setForeground(DARK_NAVY);

        boatCombo = new JComboBox<>();
        boatCombo.setBounds(24, 110, 412, 46);
        boatCombo.setFont(new Font("Segoe UI", Font.PLAIN, 15));

        JLabel customerLabel = new JLabel("Customer Name");
        customerLabel.setBounds(24, 170, 200, 22);
        customerLabel.setFont(new Font("Segoe UI", Font.BOLD, 15));
        customerLabel.setForeground(DARK_NAVY);

        customerField = new JTextField();
        customerField.setBounds(24, 200, 412, 46);
        customerField.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        customerField.setBorder(new CompoundBorder(new LineBorder(HOVER_BLUE, 1, true), new EmptyBorder(0, 14, 0, 14)));
        if (isUserBooking()) {
            customerField.setText(currentUserName());
            customerField.setEditable(false);
        }

        JLabel seatsLabel = new JLabel("Number of Seats");
        seatsLabel.setBounds(24, 260, 200, 22);
        seatsLabel.setFont(new Font("Segoe UI", Font.BOLD, 15));
        seatsLabel.setForeground(DARK_NAVY);

        seatsCombo = new JComboBox<>(new String[]{"1", "2", "3", "4", "5", "6", "7", "8"});
        seatsCombo.setBounds(24, 290, 412, 46);
        seatsCombo.setFont(new Font("Segoe UI", Font.PLAIN, 15));

        JLabel timeLabel = new JLabel("Time Slot");
        timeLabel.setBounds(24, 350, 200, 22);
        timeLabel.setFont(new Font("Segoe UI", Font.BOLD, 15));
        timeLabel.setForeground(DARK_NAVY);

        timeCombo = new JComboBox<>(new String[]{"9 AM", "11 AM", "1 PM", "3 PM", "5 PM"});
        timeCombo.setBounds(24, 380, 412, 46);
        timeCombo.setFont(new Font("Segoe UI", Font.PLAIN, 15));

        JLabel paymentLabel = new JLabel("Payment Method");
        paymentLabel.setBounds(24, 440, 200, 22);
        paymentLabel.setFont(new Font("Segoe UI", Font.BOLD, 15));
        paymentLabel.setForeground(DARK_NAVY);

        paymentCombo = new JComboBox<>(new String[]{"Cash", "UPI", "Card"});
        paymentCombo.setBounds(24, 470, 412, 46);
        paymentCombo.setFont(new Font("Segoe UI", Font.PLAIN, 15));

        JButton confirmButton = new JButton("Generate Ticket");
        confirmButton.setBounds(110, 526, 240, 46);
        confirmButton.setBackground(DARK_NAVY);
        confirmButton.setForeground(Color.WHITE);
        confirmButton.setFont(new Font("Segoe UI", Font.BOLD, 16));
        confirmButton.setFocusPainted(false);
        confirmButton.setBorder(new EmptyBorder(10, 20, 10, 20));
        confirmButton.addActionListener(e -> createBooking());

        card.add(header);
        card.add(boatLabel);
        card.add(boatCombo);
        card.add(customerLabel);
        card.add(customerField);
        card.add(seatsLabel);
        card.add(seatsCombo);
        card.add(timeLabel);
        card.add(timeCombo);
        card.add(paymentLabel);
        card.add(paymentCombo);
        card.add(confirmButton);

        root.add(card);
        add(root);

        loadBoats(selectedBoatName);
        setVisible(true);
    }

    private void loadBoats(String selectedBoatName) {
        boatCombo.removeAllItems();
        for (Boat boat : DataStore.boats) {
            if (boat.getSeats() > 0 && boat.isAvailability()) {
                boatCombo.addItem(boat.getBoatName());
            }
        }
        if (selectedBoatName != null) {
            boatCombo.setSelectedItem(selectedBoatName);
        }
    }

    private void createBooking() {
        String customerName = customerField.getText().trim();
        String boatName = (String) boatCombo.getSelectedItem();
        String seatsText = (String) seatsCombo.getSelectedItem();
        String timeSlot = (String) timeCombo.getSelectedItem();
        String paymentMethod = (String) paymentCombo.getSelectedItem();

        if (customerName.isEmpty() || boatName == null || seatsText == null || timeSlot == null || paymentMethod == null) {
            JOptionPane.showMessageDialog(this, "Please fill out every field to complete the booking.", "Incomplete Form", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int seatsBooked = Integer.parseInt(seatsText);
        Boat boat = DataStore.getBoatByName(boatName);
        if (boat == null) {
            JOptionPane.showMessageDialog(this, "Selected boat does not exist.", "Booking Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!boat.isAvailability() || boat.getSeats() < seatsBooked) {
            JOptionPane.showMessageDialog(this, "Not enough seats available. Please select another boat or fewer seats.", "Booking Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        double amount = boat.getPrice() * seatsBooked;
        boolean booked = DataStore.bookSeats(boatName, seatsBooked);
        if (!booked) {
            JOptionPane.showMessageDialog(this, "Unable to complete booking. Please try again.", "Booking Failed", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (isUserBooking()) {
            customerName = currentUserName();
        }
        String createdByEmail = currentUserEmail();
        Booking booking = DataStore.addBooking(customerName, boatName, seatsBooked, amount, paymentMethod, timeSlot, createdByEmail);
        if (parent != null) {
            parent.refreshAllPanels();
        }
        if (userParent != null) {
            userParent.refreshAllPanels();
        }

        JOptionPane.showMessageDialog(this, "Your boat is booked successfully \uD83D\uDEA4", "Booking Successful", JOptionPane.INFORMATION_MESSAGE);
        showReceipt(booking);
        dispose();
    }

    private void showReceipt(Booking booking) {
        JTextArea receipt = new JTextArea();
        receipt.setEditable(false);
        receipt.setFont(new Font("Monospaced", Font.PLAIN, 15));
        receipt.setBackground(Color.WHITE);
        receipt.setBorder(new EmptyBorder(18, 18, 18, 18));
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
        JOptionPane.showMessageDialog(this, receipt, "Professional Booking Receipt", JOptionPane.INFORMATION_MESSAGE);
    }

    private boolean isUserBooking() {
        return userParent != null || (parent != null && !parent.hasAdminRole());
    }

    private String currentUserName() {
        if (userParent != null) {
            return userParent.getCurrentUser();
        }
        return parent != null ? parent.getCurrentUser() : "";
    }

    private String currentUserEmail() {
        if (userParent != null) {
            return userParent.getCurrentUserEmail();
        }
        return parent != null ? parent.getCurrentUserEmail() : "";
    }
}
