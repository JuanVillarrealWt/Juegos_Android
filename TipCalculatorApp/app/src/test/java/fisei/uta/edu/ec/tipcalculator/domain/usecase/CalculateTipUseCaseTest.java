package fisei.uta.edu.ec.tipcalculator.domain.usecase;

import org.junit.Before;
import org.junit.Test;

import fisei.uta.edu.ec.tipcalculator.domain.model.TipResult;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class CalculateTipUseCaseTest {

    private CalculateTipUseCase calculateTipUseCase;

    @Before
    public void setUp() {
        calculateTipUseCase = new CalculateTipUseCaseImpl();
    }

    @Test
    public void calculate_standardAmountAndPercent_returnsCorrectTipAndTotal() {
        double billAmount = 100.0;
        double percent = 0.15;

        TipResult result = calculateTipUseCase.calculate(billAmount, percent);

        assertNotNull(result);
        assertEquals(15.0, result.getTip(), 0.001);
        assertEquals(115.0, result.getTotal(), 0.001);
    }

    @Test
    public void calculate_zeroAmount_returnsZeroTipAndTotal() {
        double billAmount = 0.0;
        double percent = 0.15;

        TipResult result = calculateTipUseCase.calculate(billAmount, percent);

        assertNotNull(result);
        assertEquals(0.0, result.getTip(), 0.001);
        assertEquals(0.0, result.getTotal(), 0.001);
    }

    @Test
    public void calculate_zeroPercent_returnsZeroTipAndFullTotal() {
        double billAmount = 50.0;
        double percent = 0.0;

        TipResult result = calculateTipUseCase.calculate(billAmount, percent);

        assertNotNull(result);
        assertEquals(0.0, result.getTip(), 0.001);
        assertEquals(50.0, result.getTotal(), 0.001);
    }

    @Test
    public void calculate_extensionWithMinimumTip_demonstratesOCPAndLSP() {
        // Demostración de OCP y LSP: ampliamos el cálculo sin modificar CalculateTipUseCaseImpl
        CalculateTipUseCase customUseCase = new CalculateTipUseCaseImpl() {
            private static final double MIN_TIP = 2.0;

            @Override
            protected double computeTip(double billAmount, double percent) {
                double normalTip = super.computeTip(billAmount, percent);
                return Math.max(normalTip, MIN_TIP);
            }
        };

        // Si la propina normal es 1.50 (10.0 * 0.15), aplica el mínimo de 2.0
        TipResult result = customUseCase.calculate(10.0, 0.15);
        assertEquals(2.0, result.getTip(), 0.001);
        assertEquals(12.0, result.getTotal(), 0.001);
    }
}
