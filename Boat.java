public class Boat {
    private int boatId;
    private String boatName;
    private double price;
    private int seats;
    private String type;
    private boolean availability;
    private int totalBoats;
    private int availableBoats;

    public Boat(int boatId, String boatName, double price, int seats, String type, boolean availability, int totalBoats) {
        this.boatId = boatId;
        this.boatName = boatName;
        this.price = price;
        this.seats = seats;
        this.type = type;
        this.availability = availability;
        this.totalBoats = totalBoats;
        this.availableBoats = totalBoats;
    }

    public int getBoatId() {
        return boatId;
    }

    public void setBoatId(int boatId) {
        this.boatId = boatId;
    }

    public String getBoatName() {
        return boatName;
    }

    public void setBoatName(String boatName) {
        this.boatName = boatName;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getSeats() {
        return seats;
    }

    public void setSeats(int seats) {
        this.seats = seats;
        this.availability = seats > 0;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isAvailability() {
        return availability;
    }

    public void setAvailability(boolean availability) {
        this.availability = availability;
    }

    public int getTotalBoats() {
        return totalBoats;
    }

    public void setTotalBoats(int totalBoats) {
        this.totalBoats = totalBoats;
    }

    public int getAvailableBoats() {
        return availableBoats;
    }

    public void setAvailableBoats(int availableBoats) {
        this.availableBoats = availableBoats;
        this.availability = availableBoats > 0;
    }

    public String getStatusText() {
        return (availableBoats > 0) ? "Available" : "Booked";
    }
}

