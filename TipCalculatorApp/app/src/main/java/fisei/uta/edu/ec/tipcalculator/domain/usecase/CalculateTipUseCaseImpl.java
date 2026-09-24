package fisei.uta.edu.ec.tipcalculator.domain.usecase;

import fisei.uta.edu.ec.tipcalculator.domain.model.TipResult;

/**
 * Implementación predeterminada del caso de uso de cálculo de propina.
 * Cumple con:
 * - SRP: única razón de cambio es la lógica matemática de negocio.
 * - OCP: métodos protegidos que permiten extender la lógica (ej. propina mínima,
 *   descuentos o redondeos) mediante herencia o composición sin modificar esta clase.
 * - LSP: cualquier subclase puede sustituir a esta implementación sin romper el contrato.
 */
public class CalculateTipUseCaseImpl implements CalculateTipUseCase {

    @Override
    public TipResult calculate(double billAmount, double percent) {
        double tip = computeTip(billAmount, percent);
        double total = computeTotal(billAmount, tip);
        return new TipResult(tip, total);
    }

    /**
     * Calcula la propina en base al monto y porcentaje.
     * Sobrescribible para aplicar reglas especiales (OCP).
     */
    protected double computeTip(double billAmount, double percent) {
        return billAmount * percent;
    }

    /**
     * Calcula el total sumando el monto de la cuenta y la propina.
     * Sobrescribible para aplicar reglas especiales (OCP).
     */
    protected double computeTotal(double billAmount, double tip) {
        return billAmount + tip;
    }
}
