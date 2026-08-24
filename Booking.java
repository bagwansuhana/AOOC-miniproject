public class Booking {
    private int bookingId;
    private String customerName;
    private String boatName;
    private int seatsBooked;
    private double amount;
    private String paymentMethod;
    private String timeSlot;
    private String createdByEmail;

    public Booking(int bookingId, String customerName, String boatName, int seatsBooked, double amount, String paymentMethod, String timeSlot, String createdByEmail) {
        this.bookingId = bookingId;
        this.customerName = customerName;
        this.boatName = boatName;
        this.seatsBooked = seatsBooked;
        this.amount = amount;
        this.paymentMethod = paymentMethod;
        this.timeSlot = timeSlot;
        this.createdByEmail = createdByEmail;
    }

    public int getBookingId() {
        return bookingId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getBoatName() {
        return boatName;
    }

    public void setBoatName(String boatName) {
        this.boatName = boatName;
    }

    public int getSeatsBooked() {
        return seatsBooked;
    }

    public void setSeatsBooked(int seatsBooked) {
        this.seatsBooked = seatsBooked;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getTimeSlot() {
        return timeSlot;
    }

    public void setTimeSlot(String timeSlot) {
        this.timeSlot = timeSlot;
    }

    public String getCreatedByEmail() {
        return createdByEmail;
    }

    public void setCreatedByEmail(String createdByEmail) {
        this.createdByEmail = createdByEmail;
    }
}

