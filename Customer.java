package f304assignment2;

import java.util.Objects;

public class Customer {
    private String id;
    private String name;
    private String email;
    private String phone;
    private boolean isPremium;

    // ---------- Constructor ----------
    public Customer(String id, String name, String email, String phone) {
        this.id = Objects.requireNonNull(id).trim();
        this.name = Objects.requireNonNull(name).trim();
        this.email = Objects.requireNonNull(email).trim();
        this.phone = Objects.requireNonNull(phone).trim();
        this.isPremium = false;
    }

    // ---------- Getters ----------
    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public boolean isPremium() {
        return isPremium;
    }

    // ---------- Setters ----------
    public void setName(String name) {
        this.name = name.trim();
    }

    public void setEmail(String email) {
        this.email = email.trim();
    }

    public void setPhone(String phone) {
        this.phone = phone.trim();
    }

    protected void setPremium(boolean premium) {
        this.isPremium = premium;
    }

    // ---------- CSV Methods ----------
    public String toCSV() {
        return String.join(",",
                escape(id),
                escape(name),
                escape(email),
                escape(phone),
                Boolean.toString(isPremium),
                "0" // default loyalty points (PremiumCustomer will override)
        );
    }

    public static Customer fromCSV(String csvLine) {
        if (csvLine == null)
            throw new IllegalArgumentException("CSV line is null");

        String[] parts = csvLine.split(",", -1);
        if (parts.length < 6)
            throw new IllegalArgumentException("Invalid customer CSV: " + csvLine);

        String id = parts[0].trim();
        String name = parts[1].trim();
        String email = parts[2].trim();
        String phone = parts[3].trim();
        boolean isPremium = Boolean.parseBoolean(parts[4].trim());
        int loyalty = Integer.parseInt(parts[5].trim());

        if (isPremium) {
            PremiumCustomer pc = new PremiumCustomer(id, name, email, phone);
            pc.setLoyaltyPoints(loyalty);
            return pc;
        } else {
            return new Customer(id, name, email, phone);
        }
    }

    private static String escape(String s) {
        return s.replace("\n", " ").replace("\r", " ").trim();
    }

    @Override
    public String toString() {
        return String.format("Customer %s | %s | %s | Premium: %s",
                id, name, email, isPremium ? "Yes" : "No");
    }
}
