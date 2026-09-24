package fisei.uta.ec.doodlz5.presentation.ui.fragments;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import fisei.uta.ec.doodlz5.R;
import fisei.uta.ec.doodlz5.composition.DoodleCompositionRoot;
import fisei.uta.ec.doodlz5.domain.entities.DoodleLineWidth;
import fisei.uta.ec.doodlz5.presentation.ui.views.DoodleView;

public class LineWidthDialogFragment extends DialogFragment {
    private ImageView widthImageView;
    private SeekBar widthSeekBar;
    private Bitmap bitmap;
    private Canvas canvas;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        View widthDialogView = requireActivity().getLayoutInflater().inflate(
                R.layout.fragment_line_width, null);
        builder.setView(widthDialogView);

        builder.setTitle(R.string.title_line_width_dialog);

        widthImageView = widthDialogView.findViewById(R.id.widthImageView);
        widthSeekBar = widthDialogView.findViewById(R.id.widthSeekBar);

        final MainActivityFragment doodleFragment = getMainActivityFragment();
        final DoodleView doodleView = (doodleFragment != null) ? doodleFragment.getDoodleView() : null;

        bitmap = Bitmap.createBitmap(400, 400, Bitmap.Config.ARGB_8888);
        canvas = new Canvas(bitmap);

        widthSeekBar.setOnSeekBarChangeListener(widthChangedListener);
        if (doodleView != null) {
            widthSeekBar.setProgress(doodleView.getLineWidth());
        }

        builder.setPositiveButton(R.string.button_set_line_width,
                (dialog, id) -> {
                    if (doodleView != null) {
                        DoodleLineWidth lineWidth = new DoodleLineWidth(widthSeekBar.getProgress());
                        DoodleCompositionRoot.getInstance().getChangeLineWidthUseCase()
                                .execute(doodleView, lineWidth);
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

    private final SeekBar.OnSeekBarChangeListener widthChangedListener =
            new SeekBar.OnSeekBarChangeListener() {
                final Paint paint = new Paint();

                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    bitmap.eraseColor(requireActivity().getResources().getColor(
                            android.R.color.transparent, requireActivity().getTheme()));

                    MainActivityFragment fragment = getMainActivityFragment();
                    if (fragment != null) {
                        paint.setColor(fragment.getDoodleView().getDrawingColor());
                    }

                    paint.setStrokeWidth(progress);
                    canvas.drawLine(100, 200, 300, 200, paint);
                    widthImageView.setImageBitmap(bitmap);
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {}

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {}
            };
}
