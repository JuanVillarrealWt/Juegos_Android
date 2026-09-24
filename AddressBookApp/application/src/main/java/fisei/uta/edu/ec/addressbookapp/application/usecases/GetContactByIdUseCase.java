package fisei.uta.edu.ec.addressbookapp.application.usecases;

import fisei.uta.edu.ec.addressbookapp.application.contracts.repositories.ContactRepository;
import fisei.uta.edu.ec.addressbookapp.domain.entities.Contact;

public final class GetContactByIdUseCase {
    private final ContactRepository repository;
    public GetContactByIdUseCase(ContactRepository repository) { this.repository = repository; }
    public Contact execute(long id) { return id > 0 ? repository.getById(id) : null; }
}
