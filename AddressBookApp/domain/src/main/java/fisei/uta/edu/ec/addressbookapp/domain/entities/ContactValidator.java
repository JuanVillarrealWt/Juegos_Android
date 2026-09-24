package fisei.uta.edu.ec.addressbookapp.domain.entities;

/** Business validation shared by every entry point, independent of Android. */
public final class ContactValidator {
    private ContactValidator() { }
    public static boolean isValid(Contact contact) {
        return contact != null && isValidName(contact.getName())
                && isValidPhone(contact.getPhone()) && isValidEmail(contact.getEmail())
                && isValidZip(contact.getZip());
    }
    public static boolean isValidName(String name) { return name != null && !name.trim().isEmpty(); }
    public static boolean isValidPhone(String phone) {
        if (phone == null) return false;
        String clean = phone.trim().replaceAll("[\\s\\-()]", "");
        return clean.isEmpty() || clean.matches("^(\\+593[0-9]{8,9}|0[0-9]{8,9}|[0-9]{9,10})$");
    }
    public static boolean isValidEmail(String email) {
        if (email == null) return false;
        String value = email.trim();
        return value.isEmpty() || value.matches("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$");
    }
    public static boolean isValidZip(String zip) {
        if (zip == null) return false;
        String value = zip.trim();
        return value.isEmpty() || value.matches("^[0-9]{6}$");
    }
}
