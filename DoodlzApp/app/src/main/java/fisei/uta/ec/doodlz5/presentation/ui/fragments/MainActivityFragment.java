package fisei.uta.ec.doodlz5.presentation.ui.fragments;

import android.Manifest;
import android.app.AlertDialog;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import fisei.uta.ec.doodlz5.R;
import fisei.uta.ec.doodlz5.composition.DoodleCompositionRoot;
import fisei.uta.ec.doodlz5.presentation.ui.views.DoodleView;

public class MainActivityFragment extends Fragment {
    private DoodleView doodleView;
    private boolean dialogOnScreen = false;

    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    performSaveImage();
                }
            });

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        setHasOptionsMenu(true);
        doodleView = view.findViewById(R.id.doodleView);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        DoodleCompositionRoot.getInstance().getShakeSensorRepository()
                .startListening(requireContext(), () -> {
                    if (!dialogOnScreen) {
                        confirmErase();
                    }
                });
    }

    @Override
    public void onPause() {
        super.onPause();
        DoodleCompositionRoot.getInstance().getShakeSensorRepository()
                .stopListening(requireContext());
    }

    public void confirmErase() {
        EraseImageDialogFragment fragment = new EraseImageDialogFragment();
        fragment.show(getParentFragmentManager(), "erase dialog");
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.doodle_fragment_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.color) {
            ColorDialogFragment colorDialog = new ColorDialogFragment();
            colorDialog.show(getParentFragmentManager(), "color dialog");
            return true;
        } else if (id == R.id.line_width) {
            LineWidthDialogFragment widthDialog = new LineWidthDialogFragment();
            widthDialog.show(getParentFragmentManager(), "line width dialog");
            return true;
        } else if (id == R.id.delete) {
            confirmErase();
            return true;
        } else if (id == R.id.save) {
            saveImage();
            return true;
        } else if (id == R.id.print) {
            printImage();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void saveImage() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            performSaveImage();
            return;
        }
        if (requireContext().checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            if (shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                new AlertDialog.Builder(requireActivity())
                        .setMessage(R.string.permission_explanation)
                        .setPositiveButton(android.R.string.ok, (dialog, which) ->
                                requestPermissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE))
                        .create()
                        .show();
            } else {
                requestPermissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            }
        } else {
            performSaveImage();
        }
    }

    private void performSaveImage() {
        String name = "Doodlz_" + System.currentTimeMillis() + ".jpg";
        boolean success = DoodleCompositionRoot.getInstance().getSaveDoodleUseCase()
                .execute(requireContext().getContentResolver(), doodleView.getBitmap(), name, "Doodlz Drawing");

        if (success) {
            Toast.makeText(getContext(), R.string.message_saved, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getContext(), R.string.message_error_saving, Toast.LENGTH_SHORT).show();
        }
    }

    private void printImage() {
        boolean success = DoodleCompositionRoot.getInstance().getPrintDoodleUseCase()
                .execute(requireContext(), doodleView.getBitmap(), "Doodlz Image");

        if (!success) {
            Toast.makeText(getContext(), R.string.message_cannot_print, Toast.LENGTH_SHORT).show();
        }
    }

    public DoodleView getDoodleView() {
        return doodleView;
    }

    public void setDialogOnScreen(boolean visible) {
        dialogOnScreen = visible;
    }
}
