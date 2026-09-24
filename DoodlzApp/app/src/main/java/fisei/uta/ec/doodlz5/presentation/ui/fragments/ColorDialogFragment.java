package fisei.uta.ec.doodlz5.presentation.ui.fragments;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.SeekBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import fisei.uta.ec.doodlz5.R;
import fisei.uta.ec.doodlz5.composition.DoodleCompositionRoot;
import fisei.uta.ec.doodlz5.domain.entities.DoodleColor;
import fisei.uta.ec.doodlz5.presentation.ui.views.DoodleView;

public class ColorDialogFragment extends DialogFragment {
    private SeekBar alphaSeekBar;
    private SeekBar redSeekBar;
    private SeekBar greenSeekBar;
    private SeekBar blueSeekBar;
    private View colorView;
    private int color;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        View colorDialogView = requireActivity().getLayoutInflater().inflate(
                R.layout.fragment_color, null);
        builder.setView(colorDialogView);

        builder.setTitle(R.string.title_color_dialog);

        alphaSeekBar = colorDialogView.findViewById(R.id.alphaSeekBar);
        redSeekBar = colorDialogView.findViewById(R.id.redSeekBar);
        greenSeekBar = colorDialogView.findViewById(R.id.greenSeekBar);
        blueSeekBar = colorDialogView.findViewById(R.id.blueSeekBar);
        colorView = colorDialogView.findViewById(R.id.colorView);

        alphaSeekBar.setOnSeekBarChangeListener(colorChangedListener);
        redSeekBar.setOnSeekBarChangeListener(colorChangedListener);
        greenSeekBar.setOnSeekBarChangeListener(colorChangedListener);
        blueSeekBar.setOnSeekBarChangeListener(colorChangedListener);

        final MainActivityFragment doodleFragment = getMainActivityFragment();
        if (doodleFragment != null) {
            DoodleView doodleView = doodleFragment.getDoodleView();
            color = doodleView.getDrawingColor();
        }

        alphaSeekBar.setProgress(Color.alpha(color));
        redSeekBar.setProgress(Color.red(color));
        greenSeekBar.setProgress(Color.green(color));
        blueSeekBar.setProgress(Color.blue(color));

        builder.setPositiveButton(R.string.button_set_color,
                (dialog, id) -> {
                    if (doodleFragment != null) {
                        DoodleColor doodleColor = new DoodleColor(color);
                        DoodleCompositionRoot.getInstance().getChangeColorUseCase()
                                .execute(doodleFragment.getDoodleView(), doodleColor);
                    }
                }
        );

        return builder.create();
    }

    private MainActivityFragment getMainActivityFragment() {
        return (MainActivityFragment) getParentFragmentManager().findFragmentById(R.id.doodleFragment);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        MainActivityFragment fragment = getMainActivityFragment();
        if (fragment != null) {
            fragment.setDialogOnScreen(true);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        MainActivityFragment fragment = getMainActivityFragment();
        if (fragment != null) {
            fragment.setDialogOnScreen(false);
        }
    }

    private final SeekBar.OnSeekBarChangeListener colorChangedListener =
            new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    color = Color.argb(
                            alphaSeekBar.getProgress(),
                            redSeekBar.getProgress(),
                            greenSeekBar.getProgress(),
                            blueSeekBar.getProgress()
                    );
                    colorView.setBackgroundColor(color);
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {}

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {}
            };
}
