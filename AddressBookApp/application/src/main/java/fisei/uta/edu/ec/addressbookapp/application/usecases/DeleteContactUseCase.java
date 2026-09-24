package fisei.uta.edu.ec.addressbookapp.application.usecases;

import fisei.uta.edu.ec.addressbookapp.application.contracts.repositories.ContactRepository;

public final class DeleteContactUseCase {
    private final ContactRepository repository;
    public DeleteContactUseCase(ContactRepository repository) { this.repository = repository; }
    public boolean execute(long id) { return id > 0 && repository.delete(id); }
}
