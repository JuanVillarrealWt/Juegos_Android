package fisei.uta.edu.ec.addressbookapp.presentation;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import fisei.uta.edu.ec.addressbookapp.application.contracts.AppDependencies;
import fisei.uta.edu.ec.addressbookapp.domain.entities.Contact;
import fisei.uta.edu.ec.addressbookapp.domain.entities.ContactValidator;
import fisei.uta.edu.ec.addressbookapp.presentation.R;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AddEditFragment extends Fragment {
    public interface AddEditFragmentListener { void onAddEditCompleted(long id); }
    private AddEditFragmentListener listener;
    private long contactId;
    private boolean adding = true;
    private final ExecutorService io = Executors.newSingleThreadExecutor();
    private final Handler main = new Handler(Looper.getMainLooper());
    private TextInputLayout name, phone, email, street, city, state, zip;
    private CoordinatorLayout coordinator;
    @Override public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof AddEditFragmentListener) listener = (AddEditFragmentListener) context;
        else throw new IllegalStateException(context + " must implement AddEditFragmentListener");
    }
    @Override public void onDetach() { super.onDetach(); listener = null; }
    @Nullable @Override public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup parent, @Nullable Bundle stateBundle) {
        View view = inflater.inflate(R.layout.fragment_add_edit, parent, false);
        name = view.findViewById(R.id.nameTextInputLayout); phone = view.findViewById(R.id.phoneTextInputLayout);
        email = view.findViewById(R.id.emailTextInputLayout); street = view.findViewById(R.id.streetTextInputLayout);
        city = view.findViewById(R.id.cityTextInputLayout); state = view.findViewById(R.id.stateTextInputLayout);
        zip = view.findViewById(R.id.zipTextInputLayout);
        watch(name); watch(phone); watch(email); watch(zip);
        FloatingActionButton save = view.findViewById(R.id.saveFloatingActionButton); save.setOnClickListener(this::save);
        Bundle args = getArguments();
        if (args != null && args.getLong(ContactArguments.CONTACT_ID) > 0) { adding = false; contactId = args.getLong(ContactArguments.CONTACT_ID); }
        coordinator = requireActivity().findViewById(R.id.coordinatorLayout);
        return view;
    }
    private void watch(TextInputLayout field) {
        if (field.getEditText() == null) return;
        field.getEditText().addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) { field.setError(null); }
            @Override public void afterTextChanged(Editable s) { }
        });
    }
    @Override public void onViewCreated(@NonNull View view, @Nullable Bundle state) {
        super.onViewCreated(view, state);
        if (!adding) loadContact();
    }
    private void loadContact() {
        AppDependencies deps = (AppDependencies) requireActivity().getApplication();
        io.execute(() -> {
            Contact contact = null; boolean failed = false;
            try { contact = deps.getContactById().execute(contactId); } catch (RuntimeException e) { failed = true; }
            Contact loaded = contact; boolean error = failed;
            main.post(() -> {
                if (!isAdded() || getView() == null) return;
                if (loaded != null) populate(loaded);
                else if (error && coordinator != null) Snackbar.make(coordinator, R.string.contact_load_failed, Snackbar.LENGTH_LONG).show();
            });
        });
    }
    private void populate(Contact c) {
        set(name, c.getName()); set(phone, c.getPhone()); set(email, c.getEmail()); set(street, c.getStreet());
        set(city, c.getCity()); set(state, c.getState()); set(zip, c.getZip());
    }
    private static void set(TextInputLayout field, String value) { if (field.getEditText() != null) field.getEditText().setText(value); }
    private String value(TextInputLayout field) { return field.getEditText() == null ? "" : field.getEditText().getText().toString().trim(); }
    private void save(View ignored) {
        if (getActivity() == null) return;
        InputMethodManager keyboard = (InputMethodManager) requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (keyboard != null && getView() != null) keyboard.hideSoftInputFromWindow(getView().getWindowToken(), 0);
        String n = value(name), p = value(phone), e = value(email), st = value(street), c = value(city), s = value(state), z = value(zip);
        name.setError(null); phone.setError(null); email.setError(null); zip.setError(null);
        if (!ContactValidator.isValidName(n)) { name.setError(getString(R.string.error_name_required)); name.requestFocus(); return; }
        if (!ContactValidator.isValidPhone(p)) { phone.setError(getString(R.string.error_invalid_phone)); phone.requestFocus(); return; }
        if (!ContactValidator.isValidEmail(e)) { email.setError(getString(R.string.error_invalid_email)); email.requestFocus(); return; }
        if (!ContactValidator.isValidZip(z)) { zip.setError(getString(R.string.error_invalid_zip)); zip.requestFocus(); return; }
        Contact contact = new Contact(adding ? 0 : contactId, n, p, e, st, c, s, z);
        AppDependencies deps = (AppDependencies) requireActivity().getApplication();
        boolean creating = adding; long oldId = contactId;
        io.execute(() -> {
            boolean success = false; long savedId = oldId;
            try { if (creating) { savedId = deps.createContact().execute(contact); success = savedId > 0; }
                  else success = deps.updateContact().execute(contact); }
            catch (RuntimeException ignoredError) { }
            long id = savedId; boolean saved = success;
            main.post(() -> {
                if (!isAdded() || getView() == null) return;
                int message = creating ? (saved ? R.string.contact_added : R.string.contact_not_added)
                        : (saved ? R.string.contact_updated : R.string.contact_not_updated);
                if (coordinator != null) Snackbar.make(coordinator, message, Snackbar.LENGTH_LONG).show();
                if (saved && listener != null) listener.onAddEditCompleted(id);
            });
        });
    }
    @Override public void onDestroy() { io.shutdownNow(); super.onDestroy(); }
}
