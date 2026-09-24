package fisei.uta.edu.ec.addressbookapp.presentation;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.view.MenuHost;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import com.google.android.material.snackbar.Snackbar;
import fisei.uta.edu.ec.addressbookapp.application.contracts.AppDependencies;
import fisei.uta.edu.ec.addressbookapp.domain.entities.Contact;
import fisei.uta.edu.ec.addressbookapp.presentation.R;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DetailFragment extends Fragment {
    public interface DetailFragmentListener { void onContactDeleted(); void onEditContact(long id); }
    private static final String ARG_ID = "contact_id";
    public static DetailFragment newInstance(long id) {
        DetailFragment fragment = new DetailFragment(); Bundle args = new Bundle(); args.putLong(ARG_ID, id); fragment.setArguments(args); return fragment;
    }
    private final ExecutorService io = Executors.newSingleThreadExecutor();
    private final Handler main = new Handler(Looper.getMainLooper());
    private DetailFragmentListener listener;
    private long contactId;
    private TextView name, phone, email, street, city, stateText, zip;
    @Override public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof DetailFragmentListener) listener = (DetailFragmentListener) context;
        else throw new IllegalStateException(context + " must implement DetailFragmentListener");
    }
    @Override public void onDetach() { super.onDetach(); listener = null; }
    @Nullable @Override public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup parent, @Nullable Bundle state) {
        View view = inflater.inflate(R.layout.fragment_details, parent, false); contactId = requireArguments().getLong(ARG_ID);
        name = view.findViewById(R.id.nameTextView); phone = view.findViewById(R.id.phoneTextView);
        email = view.findViewById(R.id.emailTextView); street = view.findViewById(R.id.streetTextView);
        city = view.findViewById(R.id.cityTextView); stateText = view.findViewById(R.id.stateTextView); zip = view.findViewById(R.id.zipTextView);
        return view;
    }
    @Override public void onViewCreated(@NonNull View view, @Nullable Bundle state) {
        super.onViewCreated(view, state);
        getParentFragmentManager().setFragmentResultListener("delete_requested", getViewLifecycleOwner(), (key, result) -> deleteContact(result.getLong(ARG_ID)));
        getParentFragmentManager().setFragmentResultListener("delete_finished", getViewLifecycleOwner(), (key, result) -> {
            if (result.getBoolean("deleted")) { if (listener != null) listener.onContactDeleted(); }
            else if (getView() != null) Snackbar.make(getView(), R.string.contact_not_deleted, Snackbar.LENGTH_LONG).show();
        });
        MenuHost menuHost = requireActivity();
        menuHost.addMenuProvider(new MenuProvider() {
            @Override public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
                menu.removeItem(R.id.action_edit); menu.removeItem(R.id.action_delete); inflater.inflate(R.menu.fragment_details_menu, menu);
            }
            @Override public boolean onMenuItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.action_edit) { if (listener != null) listener.onEditContact(contactId); return true; }
                if (item.getItemId() == R.id.action_delete) { DeleteConfirmationDialog.newInstance(contactId).show(getParentFragmentManager(), "confirm_delete"); return true; }
                return false;
            }
        }, getViewLifecycleOwner(), Lifecycle.State.RESUMED);
        loadContact();
    }
    private void loadContact() {
        AppDependencies deps = (AppDependencies) requireActivity().getApplication();
        io.execute(() -> {
            Contact contact = null; boolean failed = false;
            try { contact = deps.getContactById().execute(contactId); } catch (RuntimeException e) { failed = true; }
            Contact loaded = contact; boolean error = failed;
            main.post(() -> { if (!isAdded() || getView() == null) return;
                if (loaded != null) display(loaded); else if (error) Snackbar.make(getView(), R.string.contact_load_failed, Snackbar.LENGTH_LONG).show();
            });
        });
    }
    private void display(Contact c) {
        name.setText(c.getName()); phone.setText(c.getPhone()); email.setText(c.getEmail());
        street.setText(c.getStreet()); city.setText(c.getCity()); stateText.setText(c.getState()); zip.setText(c.getZip());
    }
    private void deleteContact(long id) {
        AppDependencies deps = (AppDependencies) requireActivity().getApplication();
        io.execute(() -> {
            boolean deleted = false; try { deleted = deps.deleteContact().execute(id); } catch (RuntimeException ignored) { }
            Bundle result = new Bundle(); result.putBoolean("deleted", deleted);
            main.post(() -> { if (isAdded()) getParentFragmentManager().setFragmentResult("delete_finished", result); });
        });
    }
    @Override public void onDestroy() { io.shutdownNow(); super.onDestroy(); }

    public static class DeleteConfirmationDialog extends DialogFragment {
        private static final String ARG_ID = "contact_id";
        static DeleteConfirmationDialog newInstance(long id) {
            DeleteConfirmationDialog dialog = new DeleteConfirmationDialog(); Bundle args = new Bundle(); args.putLong(ARG_ID, id); dialog.setArguments(args); return dialog;
        }
        @NonNull @Override public Dialog onCreateDialog(@Nullable Bundle state) {
            long id = requireArguments().getLong(ARG_ID);
            return new AlertDialog.Builder(requireActivity()).setTitle(R.string.confirm_title).setMessage(R.string.confirm_message)
                    .setPositiveButton(R.string.button_delete, (d, which) -> { Bundle result = new Bundle(); result.putLong(ARG_ID, id); getParentFragmentManager().setFragmentResult("delete_requested", result); })
                    .setNegativeButton(R.string.button_cancel, (d, which) -> d.dismiss()).create();
        }
    }
}
