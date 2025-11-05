package f304assignment2;

 import java.util.Objects; public class Room {
	public enum RoomType { SINGLE, DOUBLE, SUITE, STANDARD, DELUXE }


	private String id; private RoomType type;
	private double pricePerNight; private boolean isAvailable;
	public Room(String id, RoomType type, double pricePerNight) { this(id, type, pricePerNight, true);
	}

	public Room(String id, RoomType type, double pricePerNight, boolean isAvailable) { this.id = Objects.requireNonNull(id).trim();
	this.type = Objects.requireNonNull(type); this.pricePerNight = pricePerNight; this.isAvailable = isAvailable;
	}
	public String getId() { return id;
	}
	public RoomType getType() { return type;
	}
	public void setType(RoomType type) { this.type = type;
	}
	public double getPricePerNight() { return pricePerNight;
	}
	public void setPricePerNight(double pricePerNight) { this.pricePerNight = pricePerNight;
	}
	public boolean isAvailable() { return isAvailable;
	}
	public void setAvailable(boolean available) { isAvailable = available;
	}
	public String toCSV() { return String.join(",",
	escape(id), type.name(),
	Double.toString(pricePerNight), Boolean.toString(isAvailable)
	);

	}
	public static Room fromCSV(String csvLine) throws IllegalArgumentException { if (csvLine == null) throw new IllegalArgumentException("CSV line is null");


	String[] parts = csvLine.split(",", -1);
	if (parts.length < 4) throw new IllegalArgumentException("Invalid roomCSV: " + csvLine);
	String id = parts[0].trim();
	RoomType type = RoomType.valueOf(parts[1].trim().toUpperCase()); double price = Double.parseDouble(parts[2].trim());
	boolean avail = Boolean.parseBoolean(parts[3].trim());


	return new Room(id, type, price, avail);
	}
	private static String escape(String s) {
	// simple escape - currently we assume no commas in fields 
		return s.replace("\n", " ").replace("\r", " ").trim();
	}
	@Override
	public String toString() {
	return String.format("Room %s | %s | Rs.%.2f/night | Available: %s", id, type.name(), pricePerNight, isAvailable ? "Yes" : "No");
	}
	@Override
	public boolean equals(Object o) { if (this == o) return true;
	if (!(o instanceof Room)) return false; Room room = (Room) o;
	return id.equals(room.id);
	}
	@Override
	public int hashCode() { return Objects.hash(id);
	}
	}


