package fisei.uta.edu.ec.tipcalculator.util;

/**
 * Contrato para formateadores de moneda.
 * Cumple con ISP (interfaz enfocada exclusivamente a formateo de moneda)
 * y DIP (abstracción para desacoplar las capas que lo consumen).
 */
public interface ICurrencyFormatter {
    String format(double amount);
}
