package fisei.uta.edu.ec.addressbookapp;

import android.app.Application;
import fisei.uta.edu.ec.addressbookapp.application.contracts.AppDependencies;
import fisei.uta.edu.ec.addressbookapp.application.contracts.repositories.ContactRepository;
import fisei.uta.edu.ec.addressbookapp.application.usecases.CreateContactUseCase;
import fisei.uta.edu.ec.addressbookapp.application.usecases.DeleteContactUseCase;
import fisei.uta.edu.ec.addressbookapp.application.usecases.GetAllContactsUseCase;
import fisei.uta.edu.ec.addressbookapp.application.usecases.GetContactByIdUseCase;
import fisei.uta.edu.ec.addressbookapp.application.usecases.UpdateContactUseCase;
import fisei.uta.edu.ec.addressbookapp.infrastructure.repositories.ContentResolverContactRepository;

/** Composition root: the only place that chooses concrete persistence implementations. */
public final class AddressBookCompositionRoot extends Application implements AppDependencies {
    private ContactRepository contacts;
    @Override public void onCreate() {
        super.onCreate();
        contacts = new ContentResolverContactRepository(getContentResolver());
    }
    @Override public CreateContactUseCase createContact() { return new CreateContactUseCase(contacts); }
    @Override public UpdateContactUseCase updateContact() { return new UpdateContactUseCase(contacts); }
    @Override public DeleteContactUseCase deleteContact() { return new DeleteContactUseCase(contacts); }
    @Override public GetContactByIdUseCase getContactById() { return new GetContactByIdUseCase(contacts); }
    @Override public GetAllContactsUseCase getAllContacts() { return new GetAllContactsUseCase(contacts); }
}
