package fisei.uta.edu.ec.addressbookapp;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import fisei.uta.edu.ec.addressbookapp.presentation.AddEditFragment;
import fisei.uta.edu.ec.addressbookapp.presentation.ContactArguments;
import fisei.uta.edu.ec.addressbookapp.presentation.ContactsFragment;
import fisei.uta.edu.ec.addressbookapp.presentation.DetailFragment;
import fisei.uta.edu.ec.addressbookapp.presentation.R;

public class MainActivity extends AppCompatActivity implements ContactsFragment.ContactsFragmentListener,
        DetailFragment.DetailFragmentListener, AddEditFragment.AddEditFragmentListener {
    private ContactsFragment contactsFragment;
    @Override protected void onCreate(Bundle state) {
        super.onCreate(state); setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar); setSupportActionBar(toolbar);
        if (findViewById(R.id.fragmentContainer) != null) {
            Fragment existing = getSupportFragmentManager().findFragmentByTag("contacts");
            contactsFragment = existing instanceof ContactsFragment ? (ContactsFragment) existing : null;
            if (state == null && contactsFragment == null) {
                contactsFragment = new ContactsFragment();
                getSupportFragmentManager().beginTransaction().add(R.id.fragmentContainer, contactsFragment, "contacts").commit();
            }
        } else {
            contactsFragment = (ContactsFragment) getSupportFragmentManager().findFragmentById(R.id.contactsFragment);
        }
    }
    @Override public void onContactSelected(long id) {
        if (findViewById(R.id.fragmentContainer) != null) showContact(id, R.id.fragmentContainer);
        else { getSupportFragmentManager().popBackStack(); showContact(id, R.id.rightPaneContainer); }
    }
    @Override public void onAddContact() {
        showEditor(0, findViewById(R.id.fragmentContainer) != null ? R.id.fragmentContainer : R.id.rightPaneContainer);
    }
    @Override public void onEditContact(long id) {
        showEditor(id, findViewById(R.id.fragmentContainer) != null ? R.id.fragmentContainer : R.id.rightPaneContainer);
    }
    private void showContact(long id, int container) {
        FragmentTransaction tx = getSupportFragmentManager().beginTransaction();
        tx.replace(container, DetailFragment.newInstance(id), "detail").addToBackStack(null).commit();
    }
    private void showEditor(long id, int container) {
        AddEditFragment fragment = new AddEditFragment();
        if (id > 0) { Bundle args = new Bundle(); args.putLong(ContactArguments.CONTACT_ID, id); fragment.setArguments(args); }
        getSupportFragmentManager().beginTransaction().replace(container, fragment).addToBackStack(null).commit();
    }
    @Override public void onContactDeleted() {
        getSupportFragmentManager().popBackStack();
        if (contactsFragment != null) contactsFragment.updateContactList();
    }
    @Override public void onAddEditCompleted(long id) {
        getSupportFragmentManager().popBackStack();
        if (contactsFragment != null) contactsFragment.updateContactList();
        if (findViewById(R.id.fragmentContainer) == null) {
            getSupportFragmentManager().popBackStack(); showContact(id, R.id.rightPaneContainer);
        }
    }
}
