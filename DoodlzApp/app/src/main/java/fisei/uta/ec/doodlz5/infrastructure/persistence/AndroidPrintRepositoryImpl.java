package fisei.uta.ec.doodlz5.infrastructure.persistence;

import android.content.Context;
import android.graphics.Bitmap;

import androidx.print.PrintHelper;

import fisei.uta.ec.doodlz5.application.contracts.repositories.PrintRepository;

public class AndroidPrintRepositoryImpl implements PrintRepository {
    @Override
    public boolean printImage(Context context, Bitmap bitmap, String jobName) {
        if (context == null || bitmap == null) {
            return false;
        }
        if (PrintHelper.systemSupportsPrint()) {
            PrintHelper printHelper = new PrintHelper(context);
            printHelper.setScaleMode(PrintHelper.SCALE_MODE_FIT);
            printHelper.printBitmap(jobName, bitmap);
            return true;
        }
        return false;
    }
}
