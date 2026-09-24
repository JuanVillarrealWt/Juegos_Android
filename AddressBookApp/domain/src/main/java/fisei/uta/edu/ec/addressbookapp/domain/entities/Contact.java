package fisei.uta.edu.ec.addressbookapp.domain.entities;

/** Domain representation of an address book contact. Android and database types are intentionally absent. */
public final class Contact {
    private final long id;
    private final String name, phone, email, street, city, state, zip;

    public Contact(long id, String name, String phone, String email, String street,
                   String city, String state, String zip) {
        this.id = id;
        this.name = value(name);
        this.phone = value(phone);
        this.email = value(email);
        this.street = value(street);
        this.city = value(city);
        this.state = value(state);
        this.zip = value(zip);
    }

    private static String value(String value) { return value == null ? "" : value; }
    public long getId() { return id; }
    public String getName() { return name; }
    public String getPhone() { return phone; }
    public String getEmail() { return email; }
    public String getStreet() { return street; }
    public String getCity() { return city; }
    public String getState() { return state; }
    public String getZip() { return zip; }
}
