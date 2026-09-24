package fisei.uta.ec.doodlz5.presentation.ui.fragments;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import fisei.uta.ec.doodlz5.R;
import fisei.uta.ec.doodlz5.composition.DoodleCompositionRoot;

public class EraseImageDialogFragment extends DialogFragment {

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());

        builder.setMessage(R.string.message_erase);
        builder.setPositiveButton(R.string.button_erase,
                (dialog, id) -> {
                    MainActivityFragment fragment = getMainActivityFragment();
                    if (fragment != null) {
                        DoodleCompositionRoot.getInstance().getClearDoodleUseCase()
                                .execute(fragment.getDoodleView());
                    }
                }
        );

        builder.setNegativeButton(android.R.string.cancel, null);

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
}
