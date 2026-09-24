package fisei.uta.ec.doodlz5.composition;

import fisei.uta.ec.doodlz5.application.contracts.repositories.ImageRepository;
import fisei.uta.ec.doodlz5.application.contracts.repositories.PrintRepository;
import fisei.uta.ec.doodlz5.application.contracts.repositories.ShakeSensorRepository;
import fisei.uta.ec.doodlz5.application.usecases.ChangeColorUseCase;
import fisei.uta.ec.doodlz5.application.usecases.ChangeLineWidthUseCase;
import fisei.uta.ec.doodlz5.application.usecases.ClearDoodleUseCase;
import fisei.uta.ec.doodlz5.application.usecases.EvaluateShakeUseCase;
import fisei.uta.ec.doodlz5.application.usecases.PrintDoodleUseCase;
import fisei.uta.ec.doodlz5.application.usecases.SaveDoodleUseCase;
import fisei.uta.ec.doodlz5.infrastructure.persistence.AndroidPrintRepositoryImpl;
import fisei.uta.ec.doodlz5.infrastructure.persistence.MediaStoreImageRepositoryImpl;
import fisei.uta.ec.doodlz5.infrastructure.repositories.AccelerometerSensorRepositoryImpl;

public class DoodleCompositionRoot {
    private static DoodleCompositionRoot instance;

    private final ImageRepository imageRepository;
    private final PrintRepository printRepository;
    private final ShakeSensorRepository shakeSensorRepository;

    private final ClearDoodleUseCase clearDoodleUseCase;
    private final ChangeColorUseCase changeColorUseCase;
    private final ChangeLineWidthUseCase changeLineWidthUseCase;
    private final SaveDoodleUseCase saveDoodleUseCase;
    private final PrintDoodleUseCase printDoodleUseCase;
    private final EvaluateShakeUseCase evaluateShakeUseCase;

    private DoodleCompositionRoot() {
        this.imageRepository = new MediaStoreImageRepositoryImpl();
        this.printRepository = new AndroidPrintRepositoryImpl();
        this.evaluateShakeUseCase = new EvaluateShakeUseCase();
        this.shakeSensorRepository = new AccelerometerSensorRepositoryImpl(evaluateShakeUseCase);

        this.clearDoodleUseCase = new ClearDoodleUseCase();
        this.changeColorUseCase = new ChangeColorUseCase();
        this.changeLineWidthUseCase = new ChangeLineWidthUseCase();
        this.saveDoodleUseCase = new SaveDoodleUseCase(imageRepository);
        this.printDoodleUseCase = new PrintDoodleUseCase(printRepository);
    }

    public static synchronized DoodleCompositionRoot getInstance() {
        if (instance == null) {
            instance = new DoodleCompositionRoot();
        }
        return instance;
    }

    public ImageRepository getImageRepository() {
        return imageRepository;
    }

    public PrintRepository getPrintRepository() {
        return printRepository;
    }

    public ShakeSensorRepository getShakeSensorRepository() {
        return shakeSensorRepository;
    }

    public ClearDoodleUseCase getClearDoodleUseCase() {
        return clearDoodleUseCase;
    }

    public ChangeColorUseCase getChangeColorUseCase() {
        return changeColorUseCase;
    }

    public ChangeLineWidthUseCase getChangeLineWidthUseCase() {
        return changeLineWidthUseCase;
    }

    public SaveDoodleUseCase getSaveDoodleUseCase() {
        return saveDoodleUseCase;
    }

    public PrintDoodleUseCase getPrintDoodleUseCase() {
        return printDoodleUseCase;
    }

    public EvaluateShakeUseCase getEvaluateShakeUseCase() {
        return evaluateShakeUseCase;
    }
}
