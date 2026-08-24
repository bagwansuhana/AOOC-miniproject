import java.util.ArrayList;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.prefs.Preferences;

public class DataStore {
    public static final ArrayList<Boat> boats = new ArrayList<>();
    public static final ArrayList<Booking> bookings = new ArrayList<>();
    public static final ArrayList<UserAccount> users = new ArrayList<>();
    private static final Preferences preferences = Preferences.userRoot().node(DataStore.class.getName());
    private static final String USERS_KEY = "registeredUsers";

    private static int boatIdCounter = 1;
    private static int bookingIdCounter = 1;
    private static boolean seeded = false;

    public static void seedIfNeeded() {
        if (seeded) {
            ensureAdminAccount();
            return;
        }
        boats.clear();
        bookings.clear();
        users.clear();
        boatIdCounter = 1;
        bookingIdCounter = 1;
        loadUsers();

        addBoat("Paddle Boat", 100, 4, "Paddle", true, 3);
        addBoat("Speed Boat", 250, 2, "Speed", true, 2);
        addBoat("Family Boat", 400, 6, "Family", true, 1);
        addBoat("Luxury Boat", 700, 8, "Luxury", true, 1);
        addBoat("Kids Boat", 80, 2, "Kids", true, 2);
        addBoat("Couple Boat", 300, 2, "Couple", true, 2);
        registerUser("Admin", "admin@rankala.com", "admin123", "Admin");

        seeded = true;
    }

    private static void ensureAdminAccount() {
        UserAccount admin = getUserByEmail("admin@123");
        if (admin == null) {
            registerUser("Admin", "admin@123", "1234", "Admin");
        } else if (!"Admin".equalsIgnoreCase(admin.getRole()) || !"1234".equals(admin.getPassword())) {
            users.remove(admin);
            registerUser("Admin", "admin@123", "1234", "Admin");
        }
    }

    public static synchronized boolean registerUser(String name, String email, String password, String role) {
        if (emailExists(email)) return false;
        users.add(new UserAccount(name, email, password, role));
        saveUsers();
        return true;
    }

    public static boolean emailExists(String email) {
        return getUserByEmail(email) != null;
    }

    public static UserAccount getUserByEmail(String email) {
        if (email == null) return null;
        for (UserAccount user : users) {
            if (user.getEmail().equalsIgnoreCase(email.trim())) {
                return user;
            }
        }
        return null;
    }

    public static UserAccount validateUser(String email, String password) {
        UserAccount user = getUserByEmail(email);
        if (user == null) return null;
        if (!user.getPassword().equals(password)) return null;
        return user;
    }

    public static synchronized Boat addBoat(String boatName, double price, int seats, String type, boolean availability, int totalBoats) {
        int id = boatIdCounter++;
        Boat boat = new Boat(id, boatName, price, seats, type, availability, totalBoats);
        boat.setSeats(seats);
        boat.setAvailability(availability);
        boat.setAvailableBoats(totalBoats);
        boats.add(boat);
        return boat;
    }

    public static synchronized void updateBoat(int boatId, String boatName, double price, int seats, String type, boolean availability, int totalBoats) {
        Boat boat = getBoatById(boatId);
        if (boat == null) return;
        boat.setBoatName(boatName);
        boat.setPrice(price);
        boat.setType(type);
        boat.setSeats(seats);
        boat.setAvailability(availability);
        boat.setTotalBoats(totalBoats);
        boat.setAvailableBoats(Math.min(boat.getAvailableBoats(), totalBoats)); // adjust available if total decreased
    }

    public static synchronized void deleteBoat(int boatId) {
        Boat boat = getBoatById(boatId);
        if (boat == null) return;
        boats.remove(boat);
    }

    public static Boat getBoatById(int boatId) {
        for (Boat boat : boats) {
            if (boat.getBoatId() == boatId) return boat;
        }
        return null;
    }

    public static Boat getBoatByName(String boatName) {
        for (Boat boat : boats) {
            if (boat.getBoatName().equalsIgnoreCase(boatName)) return boat;
        }
        return null;
    }

    public static ArrayList<Boat> searchBoats(String query) {
        ArrayList<Boat> result = new ArrayList<>();
        String lowerQuery = query == null ? "" : query.trim().toLowerCase();
        for (Boat boat : boats) {
            if (lowerQuery.isEmpty()
                    || boat.getBoatName().toLowerCase().contains(lowerQuery)
                    || boat.getType().toLowerCase().contains(lowerQuery)
                    || boat.getStatusText().toLowerCase().contains(lowerQuery)) {
                result.add(boat);
            }
        }
        return result;
    }

    public static synchronized Booking addBooking(String customerName, String boatName, int seatsBooked, double amount, String paymentMethod, String timeSlot, String createdByEmail) {
        int id = bookingIdCounter++;
        Booking booking = new Booking(id, customerName, boatName, seatsBooked, amount, paymentMethod, timeSlot, createdByEmail);
        bookings.add(booking);
        return booking;
    }

    public static synchronized Booking addBooking(String customerName, String boatName, int seatsBooked, double amount, String paymentMethod, String timeSlot) {
        return addBooking(customerName, boatName, seatsBooked, amount, paymentMethod, timeSlot, "");
    }

    public static synchronized boolean bookSeats(String boatName, int seatsToBook) {
        Boat boat = getBoatByName(boatName);
        if (boat == null) return false;
        if (seatsToBook <= 0) return false;
        if (boat.getAvailableBoats() <= 0 || boat.getSeats() < seatsToBook) return false;

        boat.setSeats(boat.getSeats() - seatsToBook);
        boat.setAvailableBoats(boat.getAvailableBoats() - 1);
        return true;
    }

    public static int totalTicketsSold() {
        int total = 0;
        for (Booking booking : bookings) {
            total += booking.getSeatsBooked();
        }
        return total;
    }

    public static double totalRevenue() {
        double sum = 0;
        for (Booking booking : bookings) {
            sum += booking.getAmount();
        }
        return sum;
    }

    public static int totalBoatsCount() {
        int count = 0;
        for (Boat boat : boats) {
            count += boat.getTotalBoats();
        }
        return count;
    }

    public static int availableBoatsCount() {
        int count = 0;
        for (Boat boat : boats) {
            count += boat.getAvailableBoats();
        }
        return count;
    }

    public static int bookedBoatsCount() {
        int count = 0;
        for (Boat boat : boats) {
            count += (boat.getTotalBoats() - boat.getAvailableBoats());
        }
        return count;
    }

    private static void loadUsers() {
        String savedUsers = preferences.get(USERS_KEY, "");
        if (savedUsers.trim().isEmpty()) {
            return;
        }

        String[] rows = savedUsers.split("\\R");
        for (String row : rows) {
            if (row.trim().isEmpty()) {
                continue;
            }

            String[] fields = row.split(",", -1);
            if (fields.length != 4) {
                continue;
            }

            String name = decode(fields[0]);
            String email = decode(fields[1]);
            String password = decode(fields[2]);
            String role = decode(fields[3]);
            if (!email.trim().isEmpty() && getUserByEmail(email) == null) {
                users.add(new UserAccount(name, email, password, role));
            }
        }
    }

    private static void saveUsers() {
        StringBuilder data = new StringBuilder();
        for (UserAccount user : users) {
            if (data.length() > 0) {
                data.append(System.lineSeparator());
            }
            data.append(encode(user.getName())).append(",")
                    .append(encode(user.getEmail())).append(",")
                    .append(encode(user.getPassword())).append(",")
                    .append(encode(user.getRole()));
        }
        preferences.put(USERS_KEY, data.toString());
    }

    private static String encode(String value) {
        String safeValue = value == null ? "" : value;
        return Base64.getEncoder().encodeToString(safeValue.getBytes(StandardCharsets.UTF_8));
    }

    private static String decode(String value) {
        try {
            return new String(Base64.getDecoder().decode(value), StandardCharsets.UTF_8);
        } catch (IllegalArgumentException ex) {
            return "";
        }
    }
}


