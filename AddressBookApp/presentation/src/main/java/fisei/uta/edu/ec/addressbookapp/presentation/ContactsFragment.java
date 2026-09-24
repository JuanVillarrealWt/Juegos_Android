package fisei.uta.edu.ec.addressbookapp.presentation;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import fisei.uta.edu.ec.addressbookapp.application.contracts.AppDependencies;
import fisei.uta.edu.ec.addressbookapp.domain.entities.Contact;
import fisei.uta.edu.ec.addressbookapp.presentation.R;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ContactsFragment extends Fragment {
    public interface ContactsFragmentListener { void onContactSelected(long id); void onAddContact(); }
    private final ExecutorService io = Executors.newSingleThreadExecutor();
    private final Handler main = new Handler(Looper.getMainLooper());
    private ContactsFragmentListener listener;
    private ContactsAdapter adapter;
    @Override public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof ContactsFragmentListener) listener = (ContactsFragmentListener) context;
        else throw new IllegalStateException(context + " must implement ContactsFragmentListener");
    }
    @Override public void onDetach() { super.onDetach(); listener = null; }
    @Nullable @Override public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup parent, @Nullable Bundle state) {
        View view = inflater.inflate(R.layout.fragment_contacts, parent, false);
        RecyclerView recycler = view.findViewById(R.id.recyclerView);
        recycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new ContactsAdapter(id -> { if (listener != null) listener.onContactSelected(id); });
        recycler.setAdapter(adapter);
        if (getContext() != null) recycler.addItemDecoration(new ItemDivider(getContext()));
        recycler.setHasFixedSize(true);
        FloatingActionButton add = view.findViewById(R.id.addButton);
        add.setOnClickListener(v -> { if (listener != null) listener.onAddContact(); });
        return view;
    }
    @Override public void onViewCreated(@NonNull View view, @Nullable Bundle state) { super.onViewCreated(view, state); updateContactList(); }
    public void updateContactList() {
        if (adapter == null || !isAdded()) return;
        AppDependencies dependencies = (AppDependencies) requireActivity().getApplication();
        io.execute(() -> {
            List<Contact> contacts;
            boolean failed = false;
            try { contacts = dependencies.getAllContacts().execute(); }
            catch (RuntimeException error) { contacts = Collections.emptyList(); failed = true; }
            List<Contact> loaded = contacts;
            boolean loadFailed = failed;
            main.post(() -> {
                if (!isAdded() || adapter == null) return;
                adapter.setContacts(loaded);
                if (loadFailed && getView() != null) Snackbar.make(getView(), R.string.contacts_load_failed, Snackbar.LENGTH_LONG).show();
            });
        });
    }
    @Override public void onDestroyView() { adapter = null; super.onDestroyView(); }
    @Override public void onDestroy() { io.shutdownNow(); super.onDestroy(); }
}
