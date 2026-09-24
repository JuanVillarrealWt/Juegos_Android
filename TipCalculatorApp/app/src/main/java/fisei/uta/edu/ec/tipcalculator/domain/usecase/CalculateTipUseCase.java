package fisei.uta.edu.ec.tipcalculator.domain.usecase;

import fisei.uta.edu.ec.tipcalculator.domain.model.TipResult;

/**
 * Contrato de caso de uso para el cálculo de propina.
 * Cumple con:
 * - ISP: interfaz pequeña y específica (solo cálculo, sin formateo ni UI).
 * - DIP: permite que las capas superiores dependan de esta abstracción.
 */
public interface CalculateTipUseCase {
    TipResult calculate(double billAmount, double percent);
}
