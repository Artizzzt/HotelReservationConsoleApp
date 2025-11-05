import java.time.LocalDate;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        Hotel hotel = new Hotel();

        while (true) {
            System.out.println("\n===== Hotel Reservation System =====");
            System.out.println("1. Add Room");
            System.out.println("2. Add Customer");
            System.out.println("3. Make Reservation");
            System.out.println("4. Cancel Reservation");
            System.out.println("5. Display Rooms");
            System.out.println("6. Display Customers");
            System.out.println("7. Display Bookings");
            System.out.println("8. Show Bill by Booking ID");
            System.out.println("9. Exit");
            System.out.print("Choose option: ");

            int choice = Integer.parseInt(sc.nextLine());

            switch (choice) {
                case 1 -> {
                    String roomId = hotel.generateRoomId();
                    System.out.print("Enter Room Type: ");
                    String type = sc.nextLine();
                    System.out.print("Enter Price per Night: ");
                    double price = Double.parseDouble(sc.nextLine());
                    Room.RoomType roomType = Room.RoomType.valueOf(type.toUpperCase());
                    hotel.addRoom(new Room(roomId, roomType, price));
                    System.out.println("Room added with ID: " + roomId);
                }

                case 2 -> {
                    String custId = hotel.generateCustomerId();
                    System.out.print("Enter Customer Name: ");
                    String name = sc.nextLine();
                    System.out.print("Enter Customer Email: ");
                    String email = sc.nextLine();
                    System.out.print("Enter Customer Phone: ");
                    String phone = sc.nextLine();
                    System.out.print("Premium Customer? (yes/no): ");
                    String ans = sc.nextLine();

                    if (ans.equalsIgnoreCase("yes")) {
                        hotel.addCustomer(new PremiumCustomer(custId, name, email, phone));
                    } else {
                        hotel.addCustomer(new Customer(custId, name, email, phone));
                    }

                    System.out.println("Customer added with ID: " + custId);
                }

                case 3 -> {
                    String bookingId = hotel.generateBookingId();
                    System.out.print("Enter Room ID: ");
                    String roomId = sc.nextLine();
                    System.out.print("Enter Customer ID: ");
                    String custId = sc.nextLine();
                    System.out.print("Enter Check-in Date (yyyy-mm-dd): ");
                    LocalDate in = LocalDate.parse(sc.nextLine());
                    System.out.print("Enter Check-out Date (yyyy-mm-dd): ");
                    LocalDate out = LocalDate.parse(sc.nextLine());
                    hotel.makeReservation(bookingId, roomId, custId, in, out);
                }

                case 4 -> {
                    System.out.print("Enter Booking ID to cancel: ");
                    String bid = sc.nextLine();
                    System.out.print("Cancellation confirm (yes/no): ");
                    String confirm = sc.nextLine();

                    if (confirm.equalsIgnoreCase("yes")) {
                        hotel.cancelReservation(bid);
                    } else {
                        System.out.println("Cancellation aborted.");
                    }
                }

                case 5 -> {
                    System.out.println("\n--- Rooms ---");
                    System.out.printf("%-8s | %-10s | %-8s | %-10s%n", "Room ID", "Type", "Price", "Status");
                    System.out.println("-----------------------------------------------");
                    for (Room r : hotel.getAllRooms()) {
                        String status = r.isAvailable() ? "Available" : "Booked";
                        System.out.printf("%-8s | %-10s | %-8.2f | %-10s%n",
                                r.getId(), r.getType(), r.getPricePerNight(), status);
                    }
                }

                case 6 -> {
                    System.out.println("\n--- Customers ---");
                    System.out.printf("%-12s | %-12s | %-20s | %-10s%n",
                            "Customer ID", "Name", "Email", "Type");
                    System.out.println("-----------------------------------------------------------");
                    for (Customer c : hotel.getAllCustomers()) {
                        String type = c.isPremium() ? "Premium" : "Regular";
                        System.out.printf("%-12s | %-12s | %-20s | %-10s%n",
                                c.getId(), c.getName(), c.getEmail(), type);
                    }
                }

                case 7 -> {
                    System.out.println("\n--- Bookings ---");
                    System.out.printf("%-10s | %-8s | %-12s | %-12s | %-12s | %-10s%n",
                            "Booking ID", "Room ID", "Customer", "Check-in", "Check-out", "Status");
                    System.out.println("-----------------------------------------------------------------------");

                    for (Booking b : hotel.getAllBookings()) {
                        Customer cust = hotel.getCustomer(b.getCustomerId());
                        String custName = cust != null ? cust.getName() : "Unknown";
                        String status = b.getStatus() == Booking.Status.CANCELLED ? "Cancelled" : "Active";

                        System.out.printf("%-10s | %-8s | %-12s | %-12s | %-12s | %-10s%n",
                                b.getBookingId(), b.getRoomId(), custName,
                                b.getCheckInDate(), b.getCheckOutDate(), status);
                    }
                }

                case 8 -> {
                    System.out.print("Enter Booking ID to view bill: ");
                    String bid = sc.nextLine();
                    Bill bill = hotel.getBillForBooking(bid);

                    if (bill != null) {
                        System.out.println("\n--- Bill Details ---");
                        System.out.println(bill);

                        Booking booking = hotel.getAllBookings()
                                .stream()
                                .filter(b -> b.getBookingId().equalsIgnoreCase(bid))
                                .findFirst()
                                .orElse(null);

                        if (booking != null) {
                            Customer cust = hotel.getCustomer(booking.getCustomerId());
                            if (cust != null) {
                                System.out.println("Customer Name: " + cust.getName());
                                System.out.println("Premium: " + (cust.isPremium() ? "Yes" : "No"));
                                if (cust instanceof PremiumCustomer pc) {
                                    System.out.println("Loyalty Points: " + pc.getLoyaltyPoints());
                                }
                            }
                        }
                    } else {
                        System.out.println("No bill found for Booking ID: " + bid);
                    }
                }

                case 9 -> {
                    System.out.println("Exiting...");
                    sc.close();
                    return;
                }

                default -> System.out.println("Invalid choice!");
            }
        }
    }
}
