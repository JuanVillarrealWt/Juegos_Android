package fisei.uta.edu.ec.tipcalculator.util;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class CurrencyFormatterTest {

    private ICurrencyFormatter formatter;

    @Before
    public void setUp() {
        formatter = new CurrencyFormatter();
    }

    @Test
    public void format_zero_returnsZeroFormatted() {
        String formatted = formatter.format(0.0);
        assertEquals("$0.00", formatted);
    }

    @Test
    public void format_positiveAmount_returnsCorrectCurrency() {
        String formatted = formatter.format(15.5);
        assertEquals("$15.50", formatted);
    }
}
