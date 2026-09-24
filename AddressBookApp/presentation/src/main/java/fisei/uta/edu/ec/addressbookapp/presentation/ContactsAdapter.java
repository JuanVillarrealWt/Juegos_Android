package fisei.uta.edu.ec.addressbookapp.presentation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import fisei.uta.edu.ec.addressbookapp.domain.entities.Contact;
import java.util.ArrayList;
import java.util.List;

public final class ContactsAdapter extends RecyclerView.Adapter<ContactsAdapter.ViewHolder> {
    public interface ContactClickListener { void onClick(long contactId); }
    private final ContactClickListener clickListener;
    private final List<Contact> contacts = new ArrayList<>();
    public ContactsAdapter(ContactClickListener listener) { clickListener = listener; }
    public void setContacts(List<Contact> items) { contacts.clear(); if (items != null) contacts.addAll(items); notifyDataSetChanged(); }
    @NonNull @Override public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int type) {
        View item = LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_list_item_1, parent, false);
        return new ViewHolder(item);
    }
    @Override public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Contact contact = contacts.get(position);
        holder.text.setText(contact.getName());
        holder.itemView.setOnClickListener(v -> clickListener.onClick(contact.getId()));
    }
    @Override public int getItemCount() { return contacts.size(); }
    static final class ViewHolder extends RecyclerView.ViewHolder {
        final TextView text;
        ViewHolder(View item) { super(item); text = item.findViewById(android.R.id.text1); }
    }
}
