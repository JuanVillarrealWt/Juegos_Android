// MainActivity.java
// Manages your favorite Twitter searches for easy
// access and display in the device's web browser
package fisei.uta.edu.ec.twittersearchesapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import fisei.uta.edu.ec.twittersearchesapp.application.contracts.SearchesDependencies;
import fisei.uta.edu.ec.twittersearchesapp.application.usecases.SearchesUseCases;
import fisei.uta.edu.ec.twittersearchesapp.domain.entities.TaggedSearch;
import fisei.uta.edu.ec.twittersearchesapp.presentation.ItemDivider;
import fisei.uta.edu.ec.twittersearchesapp.presentation.SearchesAdapter;

public class MainActivity extends AppCompatActivity {
    private EditText queryEditText; // where user enters a query
    private EditText tagEditText; // where user enters a query's tag
    private FloatingActionButton saveFloatingActionButton; // save search
    private SearchesUseCases searchesUseCases;
    private List<String> tags; // list of tags for saved searches
    private SearchesAdapter adapter; // for binding data to RecyclerView
    private TextInputLayout queryTextInputLayout;
    private TextInputLayout tagTextInputLayout;

    // configures the GUI and registers event listeners
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(R.string.app_name);
        }

        // get references to the TextInputLayouts and EditTexts
        queryTextInputLayout = (TextInputLayout) findViewById(
                R.id.queryTextInputLayout);
        tagTextInputLayout = (TextInputLayout) findViewById(
                R.id.tagTextInputLayout);

        queryEditText = queryTextInputLayout.getEditText();
        if (queryEditText != null) {
            queryEditText.addTextChangedListener(textWatcher);
        }

        tagEditText = tagTextInputLayout.getEditText();
        if (tagEditText != null) {
            tagEditText.addTextChangedListener(textWatcher);
        }

        searchesUseCases = ((SearchesDependencies) getApplication()).getSearchesUseCases();

        // store the saved tags in an ArrayList then sort them
        tags = new ArrayList<>();
        for (TaggedSearch search : searchesUseCases.getSavedSearches()) {
            tags.add(search.getTag());
        }
        Collections.sort(tags, String.CASE_INSENSITIVE_ORDER);

        // get reference to the RecyclerView to configure it
        RecyclerView recyclerView =
                (RecyclerView) findViewById(R.id.recyclerView);

        // use a LinearLayoutManager to display items in a vertical list
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // create RecyclerView.Adapter to bind tags to the RecyclerView
        adapter = new SearchesAdapter(
                tags, itemClickListener, itemLongClickListener);
        recyclerView.setAdapter(adapter);

        // specify a custom ItemDecorator to draw lines between list items
        recyclerView.addItemDecoration(new ItemDivider(this));

        // register listener to save a new or edited search
        saveFloatingActionButton =
                (FloatingActionButton) findViewById(R.id.fab);
        saveFloatingActionButton.setOnClickListener(saveButtonListener);
        updateSaveFAB(); // hides button because EditTexts initially empty
    }

    // hide/show saveFloatingActionButton based on EditTexts' contents
    private final TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count,
                                      int after) { }

        // hide/show the saveFloatingActionButton after user changes input
        @Override
        public void onTextChanged(CharSequence s, int start, int before,
                                  int count) {
            updateSaveFAB();
        }

        @Override
        public void afterTextChanged(Editable s) { }
    };

    // shows or hides the saveFloatingActionButton and adjusts EditTexts
    private void updateSaveFAB() {
        if (queryEditText == null || tagEditText == null) return;

        // check if there is input in both EditTexts
        if (queryEditText.getText().toString().trim().isEmpty() ||
                tagEditText.getText().toString().trim().isEmpty()) {
            // hide FAB and make EditTexts full width
            saveFloatingActionButton.hide();
            setEditTextsMarginEnd(16); // dp, normal margin
        } else {
            // show FAB and shrink EditTexts to make room for FAB
            saveFloatingActionButton.show();
            setEditTextsMarginEnd(76); // dp, room for the FAB
        }
    }

    // helper to set the end margin on both TextInputLayouts in dp
    private void setEditTextsMarginEnd(int marginDp) {
        int marginPx = (int) (marginDp * getResources().getDisplayMetrics().density);

        // update queryTextInputLayout margin
        android.widget.RelativeLayout.LayoutParams queryParams =
                (android.widget.RelativeLayout.LayoutParams)
                        queryTextInputLayout.getLayoutParams();
        queryParams.setMarginEnd(marginPx);
        queryTextInputLayout.setLayoutParams(queryParams);

        // update tagTextInputLayout margin
        android.widget.RelativeLayout.LayoutParams tagParams =
                (android.widget.RelativeLayout.LayoutParams)
                        tagTextInputLayout.getLayoutParams();
        tagParams.setMarginEnd(marginPx);
        tagTextInputLayout.setLayoutParams(tagParams);
    }

    // saveButtonListener stores a tag-query pair through the application use case
    private final OnClickListener saveButtonListener =
            new OnClickListener() {
                // add/update search if neither query nor tag is empty
                @Override
                public void onClick(View view) {
                    String query = queryEditText.getText().toString().trim();
                    String tag = tagEditText.getText().toString().trim();

                    if (!query.isEmpty() && !tag.isEmpty()) {
                        // hide the virtual keyboard
                        InputMethodManager imm = (InputMethodManager) getSystemService(
                                Context.INPUT_METHOD_SERVICE);
                        if (imm != null) {
                            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                        }

                        addTaggedSearch(tag, query); // add/update the search
                        queryEditText.setText(""); // clear queryEditText
                        tagEditText.setText(""); // clear tagEditText
                        queryEditText.requestFocus(); // queryEditText gets focus
                    }
                }
            };

    // add new search to file, then refresh all buttons
    private void addTaggedSearch(String tag, String query) {
        searchesUseCases.saveSearch(tag, query);

        // if tag is new, add to and sort tags, then display updated list
        if (!tags.contains(tag)) {
            tags.add(tag); // add new tag
            Collections.sort(tags, String.CASE_INSENSITIVE_ORDER);
        }
        adapter.notifyDataSetChanged(); // update tags in RecyclerView
    }

    // itemClickListener launches web browser to display search results
    private final OnClickListener itemClickListener =
            new OnClickListener() {
                @Override
                public void onClick(View view) {
                    // get query string and create a URL representing the search
                    String tag = ((TextView) view).getText().toString();
                    try {
                        String urlString = getString(R.string.search_URL) +
                                Uri.encode(getQueryForTag(tag), "UTF-8");

                        // create an Intent to launch a web browser
                        Intent webIntent = new Intent(Intent.ACTION_VIEW,
                                Uri.parse(urlString));
                        startActivity(webIntent); // show results in web browser
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            };

    // itemLongClickListener displays a dialog allowing the user to share
    // edit or delete a saved search
    private final OnLongClickListener itemLongClickListener =
            new OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    // get the tag that the user long touched
                    final String tag = ((TextView) view).getText().toString();

                    // create a new AlertDialog
                    AlertDialog.Builder builder =
                            new AlertDialog.Builder(MainActivity.this);

                    // set the AlertDialog's title
                    builder.setTitle(
                            getString(R.string.share_edit_delete_title, tag));

                    // set list of items to display and create event handler
                    builder.setItems(R.array.dialog_items,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    switch (which) {
                                        case 0: // share
                                            shareSearch(tag);
                                            break;
                                        case 1: // edit
                                            // set EditTexts to match chosen tag and query
                                            tagEditText.setText(tag);
                                            queryEditText.setText(
                                                    getQueryForTag(tag));
                                            break;
                                        case 2: // delete
                                            deleteSearch(tag);
                                            break;
                                    }
                                }
                            }
                    );

                    // set the AlertDialog's negative Button
                    builder.setNegativeButton(getString(R.string.cancel), null);

                    builder.create().show(); // display the AlertDialog
                    return true;
                }
            };

    // allow user to choose an app for sharing URL of a saved search
    private void shareSearch(String tag) {
        // create the URL representing the search
        try {
            String urlString = getString(R.string.search_URL) +
                    Uri.encode(getQueryForTag(tag), "UTF-8");

            // create Intent to share urlString
            Intent shareIntent = new Intent();
            shareIntent.setAction(Intent.ACTION_SEND);
            shareIntent.putExtra(Intent.EXTRA_SUBJECT,
                    getString(R.string.share_subject));
            shareIntent.putExtra(Intent.EXTRA_TEXT,
                    getString(R.string.share_message, urlString));
            shareIntent.setType("text/plain");

            // display apps that can share plain text
            startActivity(Intent.createChooser(shareIntent,
                    getString(R.string.share_search)));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // deletes a search after the user confirms the delete operation
    private void deleteSearch(final String tag) {
        // create a new AlertDialog and set its message
        AlertDialog.Builder confirmBuilder = new AlertDialog.Builder(this);
        confirmBuilder.setMessage(getString(R.string.confirm_message, tag));

        // configure the negative (CANCEL) Button
        confirmBuilder.setNegativeButton(getString(R.string.cancel), null);

        // configure the positive (DELETE) Button
        confirmBuilder.setPositiveButton(getString(R.string.delete),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        tags.remove(tag); // remove tag from tags

                        searchesUseCases.deleteSearch(tag);

                        // rebind tags to RecyclerView to show updated list
                        adapter.notifyDataSetChanged();
                    }
                }
        );

        confirmBuilder.create().show(); // display AlertDialog
    }

    private String getQueryForTag(String tag) {
        for (TaggedSearch search : searchesUseCases.getSavedSearches()) {
            if (search.getTag().equals(tag)) return search.getQuery();
        }
        return "";
    }
}
