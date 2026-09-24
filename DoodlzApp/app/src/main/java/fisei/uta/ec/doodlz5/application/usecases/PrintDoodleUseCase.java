package fisei.uta.ec.doodlz5.application.usecases;

import android.content.Context;
import android.graphics.Bitmap;

import fisei.uta.ec.doodlz5.application.contracts.repositories.PrintRepository;

public class PrintDoodleUseCase {
    private final PrintRepository printRepository;

    public PrintDoodleUseCase(PrintRepository printRepository) {
        this.printRepository = printRepository;
    }

    public boolean execute(Context context, Bitmap bitmap, String jobName) {
        if (printRepository == null || bitmap == null) {
            return false;
        }
        return printRepository.printImage(context, bitmap, jobName);
    }
}
