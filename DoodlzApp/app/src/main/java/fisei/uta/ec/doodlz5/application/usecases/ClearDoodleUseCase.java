package fisei.uta.ec.doodlz5.application.usecases;

public class ClearDoodleUseCase {
    public interface ClearableLienzo {
        void clear();
    }

    public void execute(ClearableLienzo lienzo) {
        if (lienzo != null) {
            lienzo.clear();
        }
    }
}
