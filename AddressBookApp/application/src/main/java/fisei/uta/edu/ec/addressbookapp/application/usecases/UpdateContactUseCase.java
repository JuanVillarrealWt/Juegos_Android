package fisei.uta.edu.ec.addressbookapp.application.usecases;

import fisei.uta.edu.ec.addressbookapp.application.contracts.repositories.ContactRepository;
import fisei.uta.edu.ec.addressbookapp.domain.entities.Contact;
import fisei.uta.edu.ec.addressbookapp.domain.entities.ContactValidator;

public final class UpdateContactUseCase {
    private final ContactRepository repository;
    public UpdateContactUseCase(ContactRepository repository) { this.repository = repository; }
    public boolean execute(Contact contact) {
        if (contact == null || contact.getId() <= 0 || !ContactValidator.isValid(contact)) throw new IllegalArgumentException("Contacto inválido");
        return repository.update(contact);
    }
}
