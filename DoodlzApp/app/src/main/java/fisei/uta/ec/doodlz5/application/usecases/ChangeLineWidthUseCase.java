package fisei.uta.ec.doodlz5.application.usecases;

import fisei.uta.ec.doodlz5.domain.entities.DoodleLineWidth;

public class ChangeLineWidthUseCase {
    public interface WidthChangeableLienzo {
        void setLineWidth(int width);
    }

    public void execute(WidthChangeableLienzo lienzo, DoodleLineWidth lineWidth) {
        if (lienzo != null && lineWidth != null) {
            lienzo.setLineWidth(lineWidth.getWidth());
        }
    }
}
