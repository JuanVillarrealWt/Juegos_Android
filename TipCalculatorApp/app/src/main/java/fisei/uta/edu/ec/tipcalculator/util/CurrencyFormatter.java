package fisei.uta.edu.ec.tipcalculator.util;

import java.text.NumberFormat;
import java.util.Locale;

/**
 * Formateador de moneda.
 * Cumple con:
 * - SRP: Su única responsabilidad es convertir valores numéricos a representación de moneda.
 * - LSP: Implementa ICurrencyFormatter de manera que puede ser sustituido libremente.
 */
public class CurrencyFormatter implements ICurrencyFormatter {

    private final NumberFormat currencyFormat;

    public CurrencyFormatter() {
        this(Locale.US);
    }

    public CurrencyFormatter(Locale locale) {
        this.currencyFormat = NumberFormat.getCurrencyInstance(locale);
    }

    @Override
    public String format(double amount) {
        return currencyFormat.format(amount);
    }
}
