package fisei.uta.edu.ec.addressbookapp.infrastructure.repositories;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import fisei.uta.edu.ec.addressbookapp.application.contracts.repositories.ContactRepository;
import fisei.uta.edu.ec.addressbookapp.infrastructure.persistence.DatabaseDescription;
import fisei.uta.edu.ec.addressbookapp.domain.entities.Contact;
import java.util.ArrayList;
import java.util.List;

/** Android ContentProvider/SQLite implementation of the application persistence port. */
public final class ContentResolverContactRepository implements ContactRepository {
    private final ContentResolver resolver;
    public ContentResolverContactRepository(ContentResolver resolver) { this.resolver = resolver; }

    @Override public long create(Contact contact) {
        Uri uri = resolver.insert(DatabaseDescription.Contact.CONTENT_URI, toValues(contact));
        return uri == null ? -1 : android.content.ContentUris.parseId(uri);
    }
    @Override public boolean update(Contact contact) {
        return resolver.update(DatabaseDescription.Contact.buildContactUri(contact.getId()), toValues(contact), null, null) > 0;
    }
    @Override public boolean delete(long id) {
        return resolver.delete(DatabaseDescription.Contact.buildContactUri(id), null, null) > 0;
    }
    @Override public Contact getById(long id) {
        try (Cursor cursor = resolver.query(DatabaseDescription.Contact.buildContactUri(id), null, null, null, null)) {
            return cursor != null && cursor.moveToFirst() ? fromCursor(cursor) : null;
        }
    }
    @Override public List<Contact> getAll() {
        List<Contact> contacts = new ArrayList<>();
        try (Cursor cursor = resolver.query(DatabaseDescription.Contact.CONTENT_URI, null, null, null,
                DatabaseDescription.Contact.COLUMN_NAME + " COLLATE NOCASE ASC")) {
            if (cursor != null) while (cursor.moveToNext()) contacts.add(fromCursor(cursor));
        }
        return contacts;
    }
    private static ContentValues toValues(Contact contact) {
        ContentValues values = new ContentValues();
        values.put(DatabaseDescription.Contact.COLUMN_NAME, contact.getName());
        values.put(DatabaseDescription.Contact.COLUMN_PHONE, contact.getPhone());
        values.put(DatabaseDescription.Contact.COLUMN_EMAIL, contact.getEmail());
        values.put(DatabaseDescription.Contact.COLUMN_STREET, contact.getStreet());
        values.put(DatabaseDescription.Contact.COLUMN_CITY, contact.getCity());
        values.put(DatabaseDescription.Contact.COLUMN_STATE, contact.getState());
        values.put(DatabaseDescription.Contact.COLUMN_ZIP, contact.getZip());
        return values;
    }
    private static Contact fromCursor(Cursor cursor) {
        return new Contact(cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseDescription.Contact._ID)),
                text(cursor, DatabaseDescription.Contact.COLUMN_NAME), text(cursor, DatabaseDescription.Contact.COLUMN_PHONE),
                text(cursor, DatabaseDescription.Contact.COLUMN_EMAIL), text(cursor, DatabaseDescription.Contact.COLUMN_STREET),
                text(cursor, DatabaseDescription.Contact.COLUMN_CITY), text(cursor, DatabaseDescription.Contact.COLUMN_STATE),
                text(cursor, DatabaseDescription.Contact.COLUMN_ZIP));
    }
    private static String text(Cursor cursor, String column) {
        int index = cursor.getColumnIndex(column);
        return index < 0 || cursor.isNull(index) ? "" : cursor.getString(index);
    }
}
