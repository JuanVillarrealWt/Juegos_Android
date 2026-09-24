package fisei.uta.edu.ec.addressbookapp.application.usecases;

import fisei.uta.edu.ec.addressbookapp.application.contracts.repositories.ContactRepository;
import fisei.uta.edu.ec.addressbookapp.domain.entities.Contact;
import fisei.uta.edu.ec.addressbookapp.domain.entities.ContactValidator;

public final class CreateContactUseCase {
    private final ContactRepository repository;
    public CreateContactUseCase(ContactRepository repository) { this.repository = repository; }
    public long execute(Contact contact) {
        if (!ContactValidator.isValid(contact)) throw new IllegalArgumentException("Contacto inválido");
        return repository.create(contact);
    }
}
