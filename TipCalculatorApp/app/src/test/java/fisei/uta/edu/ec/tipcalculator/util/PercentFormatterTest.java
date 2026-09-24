package fisei.uta.edu.ec.tipcalculator.util;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class PercentFormatterTest {

    private IPercentFormatter formatter;

    @Before
    public void setUp() {
        formatter = new PercentFormatter();
    }

    @Test
    public void format_fifteenPercent_containsFifteenAndPercentSign() {
        String formatted = formatter.format(0.15);
        assertTrue(formatted.contains("15"));
        assertTrue(formatted.contains("%"));
    }

    @Test
    public void format_zeroPercent_containsZeroAndPercentSign() {
        String formatted = formatter.format(0.0);
        assertTrue(formatted.contains("0"));
        assertTrue(formatted.contains("%"));
    }

    @Test
    public void format_thirtyPercent_containsThirtyAndPercentSign() {
        String formatted = formatter.format(0.30);
        assertTrue(formatted.contains("30"));
        assertTrue(formatted.contains("%"));
    }
}
