package fisei.uta.edu.ec.flagquiz.presentation;

import android.os.Bundle;
import androidx.preference.PreferenceFragmentCompat;

import fisei.uta.edu.ec.flagquiz.R;

public class SettingsActivityFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preferences, rootKey);
    }
}
