package fisei.uta.edu.ec.cannongameapp.presentation;

import android.app.Activity;
import android.content.DialogInterface;

import androidx.appcompat.app.AlertDialog;

import fisei.uta.edu.ec.cannongameapp.presentation.R;

public class GameOverDialogHelper {

    public interface OnResetGameListener {
        void onResetGame();
    }

    public static void showGameOverDialog(final Activity activity, final int titleMessageId,
                                          final int shotsFired, final double totalElapsedTime,
                                          final OnResetGameListener listener) {
        if (activity == null || activity.isFinishing() || activity.isDestroyed()) {
            return;
        }

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (activity.isFinishing() || activity.isDestroyed()) {
                    return;
                }

                AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                builder.setTitle(activity.getString(titleMessageId));
                builder.setMessage(activity.getString(R.string.results_format,
                        shotsFired, totalElapsedTime));
                builder.setCancelable(false);
                builder.setPositiveButton(R.string.reset_game, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (listener != null) {
                            listener.onResetGame();
                        }
                    }
                });

                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
    }
}
