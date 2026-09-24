package fisei.uta.edu.ec.addressbookapp.application.contracts;

import fisei.uta.edu.ec.addressbookapp.application.usecases.CreateContactUseCase;
import fisei.uta.edu.ec.addressbookapp.application.usecases.DeleteContactUseCase;
import fisei.uta.edu.ec.addressbookapp.application.usecases.GetAllContactsUseCase;
import fisei.uta.edu.ec.addressbookapp.application.usecases.GetContactByIdUseCase;
import fisei.uta.edu.ec.addressbookapp.application.usecases.UpdateContactUseCase;

/** Dependency surface available to presentation; concrete implementations live in infrastructure. */
public interface AppDependencies {
    CreateContactUseCase createContact();
    UpdateContactUseCase updateContact();
    DeleteContactUseCase deleteContact();
    GetContactByIdUseCase getContactById();
    GetAllContactsUseCase getAllContacts();
}
