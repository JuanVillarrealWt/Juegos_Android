package fisei.uta.edu.ec.tipcalculator.util;

import java.text.NumberFormat;

/**
 * Formateador de porcentajes.
 * Cumple con:
 * - SRP: Su única responsabilidad es convertir valores decimales a texto con formato de porcentaje.
 * - LSP: Implementa IPercentFormatter asegurando la posibilidad de sustitución.
 */
public class PercentFormatter implements IPercentFormatter {

    private final NumberFormat percentFormat;

    public PercentFormatter() {
        this.percentFormat = NumberFormat.getPercentInstance();
    }

    public PercentFormatter(NumberFormat customFormat) {
        this.percentFormat = customFormat;
    }

    @Override
    public String format(double percent) {
        return percentFormat.format(percent);
    }
}
