package fisei.uta.edu.ec.addressbookapp.application.contracts.repositories;

import fisei.uta.edu.ec.addressbookapp.domain.entities.Contact;
import java.util.List;

/** Persistence boundary owned by the application/domain layers. */
public interface ContactRepository {
    long create(Contact contact);
    boolean update(Contact contact);
    boolean delete(long id);
    Contact getById(long id);
    List<Contact> getAll();
}
