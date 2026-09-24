package fisei.uta.edu.ec.tipcalculator.util;

/**
 * Contrato para formateadores de porcentajes.
 * Cumple con ISP (interfaz enfocada exclusivamente a porcentaje)
 * y DIP (abstracción para inversión de dependencias).
 */
public interface IPercentFormatter {
    String format(double percent);
}
