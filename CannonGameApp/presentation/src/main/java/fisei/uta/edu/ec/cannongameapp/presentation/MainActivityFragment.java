package fisei.uta.edu.ec.cannongameapp.presentation;

import android.media.AudioManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import fisei.uta.edu.ec.cannongameapp.presentation.R;

public class MainActivityFragment extends Fragment {
    private CannonView cannonView;
    private GameRuntime gameRuntime;

    public void configure(GameRuntime gameRuntime) {
        this.gameRuntime = gameRuntime;
        if (cannonView != null) {
            cannonView.setGameRuntime(gameRuntime);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        cannonView = view.findViewById(R.id.cannonView);
        if (gameRuntime != null) {
            cannonView.setGameRuntime(gameRuntime);
        }
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (getActivity() != null) {
            getActivity().setVolumeControlStream(AudioManager.STREAM_MUSIC);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (cannonView != null) {
            cannonView.stopGame();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (cannonView != null) {
            cannonView.releaseResources();
        }
    }
}
