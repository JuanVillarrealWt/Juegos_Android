package fisei.uta.ec.doodlz5.application.usecases;

import fisei.uta.ec.doodlz5.domain.entities.DoodleColor;

public class ChangeColorUseCase {
    public interface ColorableLienzo {
        void setDrawingColor(int color);
    }

    public void execute(ColorableLienzo lienzo, DoodleColor doodleColor) {
        if (lienzo != null && doodleColor != null) {
            lienzo.setDrawingColor(doodleColor.getArgbColor());
        }
    }
}
