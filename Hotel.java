package f304assignment2;

import java.io.*;
import java.time.LocalDate;
import java.util.*;

public class Hotel {

    private Map<String, Room> rooms = new LinkedHashMap<>();
    private Map<String, Customer> customers = new LinkedHashMap<>();
    private Map<String, Booking> bookings = new LinkedHashMap<>();
    private Map<String, Bill> bills = new LinkedHashMap<>();

    private static final String ROOMS_FILE = "rooms.csv";
    private static final String CUSTOMERS_FILE = "customers.csv";
    private static final String BOOKINGS_FILE = "bookings.csv";
    private static final String BILLS_FILE = "bills.csv";

    public Hotel() {
        loadAll();
    }

    // ---------------- ROOM METHODS ----------------
    public void addRoom(Room room) {
        rooms.put(room.getId(), room);
        saveRooms();
    }

    public Collection<Room> getAllRooms() {
        return rooms.values();
    }

    public Room getRoom(String id) {
        return rooms.get(id);
    }

    // ---------------- CUSTOMER METHODS ----------------
    public void addCustomer(Customer customer) {
        customers.put(customer.getId(), customer);
        saveCustomers();
    }

    public Customer getCustomer(String id) {
        return customers.get(id);
    }

    public Collection<Customer> getAllCustomers() {
        return customers.values();
    }

    // ---------------- BOOKING METHODS ----------------
    public Booking makeReservation(String bookingId, String roomId, String custId,
                                   LocalDate checkIn, LocalDate checkOut) {
        Room room = rooms.get(roomId);
        if (room == null || !room.isAvailable()) {
            System.out.println("Room not available.");
            return null;
        }

        Booking booking = new Booking(bookingId, roomId, custId, checkIn, checkOut);
        bookings.put(bookingId, booking);
        room.setAvailable(false);
        saveRooms();
        saveBookings();

        Customer cust = customers.get(custId);
        double charge = room.getPricePerNight() * booking.getTotalNights();
        boolean isPrem = cust != null && cust.isPremium();
        Bill bill = new Bill(generateBillId(), bookingId, charge, isPrem);
        bills.put(bill.getBillId(), bill);
        saveBills();

        if (cust instanceof PremiumCustomer pc) {
            pc.addPoints(10);
            saveCustomers();
        }

        System.out.println("Reservation confirmed!");
        System.out.println(booking);
        System.out.println(bill);

        return booking;
    }

    public void cancelReservation(String bookingId) {
        Booking b = bookings.get(bookingId);
        if (b == null) {
            System.out.println("No booking found.");
            return;
        }

        b.cancel();
        Room room = rooms.get(b.getRoomId());
        if (room != null) room.setAvailable(true);

        saveRooms();
        saveBookings();
        System.out.println("Reservation cancelled: " + bookingId);
    }

    public Collection<Booking> getAllBookings() {
        return bookings.values();
    }

    public Bill getBillForBooking(String bookingId) {
        for (Bill b : bills.values()) {
            if (b.getBookingId().equals(bookingId)) return b;
        }
        return null;
    }

    // ---------------- ID GENERATION METHODS ----------------
    public String generateRoomId() {
        int maxId = 0;
        for (String id : rooms.keySet()) {
            try {
                int num = Integer.parseInt(id.substring(1));
                if (num > maxId) maxId = num;
            } catch (Exception ignored) {}
        }
        return "R" + String.format("%03d", maxId + 1);
    }

    public String generateCustomerId() {
        int maxId = 0;
        for (String id : customers.keySet()) {
            try {
                int num = Integer.parseInt(id.substring(1));
                if (num > maxId) maxId = num;
            } catch (Exception ignored) {}
        }
        return "C" + String.format("%03d", maxId + 1);
    }

    public String generateBookingId() {
        int maxId = 0;
        for (String id : bookings.keySet()) {
            try {
                int num = Integer.parseInt(id.substring(1));
                if (num > maxId) maxId = num;
            } catch (Exception ignored) {}
        }
        return "B" + String.format("%03d", maxId + 1);
    }

    public String generateBillId() {
        int maxId = 0;
        for (String id : bills.keySet()) {
            try {
                int num = Integer.parseInt(id.substring(2));
                if (num > maxId) maxId = num;
            } catch (Exception ignored) {}
        }
        return "BL" + String.format("%03d", maxId + 1);
    }

    // ---------------- LOAD AND SAVE METHODS ----------------
    private void loadAll() {
        loadRooms();
        loadCustomers();
        loadBookings();
        loadBills();
    }

    private void loadRooms() {
        rooms.clear();
        File f = new File(ROOMS_FILE);
        if (!f.exists()) return;

        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String line;
            boolean isFirst = true;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                if (isFirst) { isFirst = false; continue; } // skip header
                Room r = Room.fromCSV(line);
                rooms.put(r.getId(), r);
            }
        } catch (Exception e) {
            System.err.println("Error loading rooms: " + e.getMessage());
        }
    }

    private void saveRooms() {
        File f = new File(ROOMS_FILE);
        try (PrintWriter pw = new PrintWriter(new FileWriter(f, false))) {
            pw.println("ID,Type,PricePerNight,Available");
            for (Room r : rooms.values()) pw.println(r.toCSV());
        } catch (Exception e) {
            System.err.println("Error saving rooms: " + e.getMessage());
        }
    }

    private void loadCustomers() {
        customers.clear();
        File f = new File(CUSTOMERS_FILE);
        if (!f.exists()) return;

        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String line;
            boolean isFirst = true;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                if (isFirst) { isFirst = false; continue; }
                Customer c = Customer.fromCSV(line);
                customers.put(c.getId(), c);
            }
        } catch (Exception e) {
            System.err.println("Error loading customers: " + e.getMessage());
        }
    }

    private void saveCustomers() {
        File f = new File(CUSTOMERS_FILE);
        try (PrintWriter pw = new PrintWriter(new FileWriter(f, false))) {
            pw.println("ID,Name,Email,Premium,Points");
            for (Customer c : customers.values()) pw.println(c.toCSV());
        } catch (Exception e) {
            System.err.println("Error saving customers: " + e.getMessage());
        }
    }

    private void loadBookings() {
        bookings.clear();
        File f = new File(BOOKINGS_FILE);
        if (!f.exists()) return;

        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String line;
            boolean isFirst = true;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                if (isFirst) { isFirst = false; continue; }
                Booking b = Booking.fromCSV(line);
                bookings.put(b.getBookingId(), b);
            }
        } catch (Exception e) {
            System.err.println("Error loading bookings: " + e.getMessage());
        }
    }

    private void saveBookings() {
        File f = new File(BOOKINGS_FILE);
        try (PrintWriter pw = new PrintWriter(new FileWriter(f, false))) {
            pw.println("BookingID,RoomID,CustomerID,CheckIn,CheckOut,Cancelled");
            for (Booking b : bookings.values()) pw.println(b.toCSV());
        } catch (Exception e) {
            System.err.println("Error saving bookings: " + e.getMessage());
        }
    }

    private void loadBills() {
        bills.clear();
        File f = new File(BILLS_FILE);
        if (!f.exists()) return;

        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String line;
            boolean isFirst = true;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                if (isFirst) { isFirst = false; continue; }
                Bill b = Bill.fromCSV(line);
                bills.put(b.getBillId(), b);
            }
        } catch (Exception e) {
            System.err.println("Error loading bills: " + e.getMessage());
        }
    }

    private void saveBills() {
        File f = new File(BILLS_FILE);
        try (PrintWriter pw = new PrintWriter(new FileWriter(f, false))) {
            pw.println("BillID,BookingID,Amount,Discount,Total");
            for (Bill b : bills.values()) pw.println(b.toCSV());
        } catch (Exception e) {
            System.err.println("Error saving bills: " + e.getMessage());
        }
    }
}
