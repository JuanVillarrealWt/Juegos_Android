package fisei.uta.edu.ec.addressbookapp.application.usecases;

import fisei.uta.edu.ec.addressbookapp.application.contracts.repositories.ContactRepository;
import fisei.uta.edu.ec.addressbookapp.domain.entities.Contact;
import java.util.List;

public final class GetAllContactsUseCase {
    private final ContactRepository repository;
    public GetAllContactsUseCase(ContactRepository repository) { this.repository = repository; }
    public List<Contact> execute() { return repository.getAll(); }
}
