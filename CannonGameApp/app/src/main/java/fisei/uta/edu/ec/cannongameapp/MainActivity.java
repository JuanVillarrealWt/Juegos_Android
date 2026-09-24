package fisei.uta.edu.ec.cannongameapp;

import android.content.pm.ActivityInfo;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import fisei.uta.edu.ec.cannongameapp.presentation.R;
import fisei.uta.edu.ec.cannongameapp.presentation.MainActivityFragment;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        setContentView(R.layout.activity_main);
        getSupportFragmentManager().executePendingTransactions();
        MainActivityFragment fragment = (MainActivityFragment)
                getSupportFragmentManager().findFragmentById(R.id.fragment);
        if (fragment != null) {
            fragment.configure(GameCompositionRoot.create(this));
        }
    }
}
