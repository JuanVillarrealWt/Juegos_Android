package fisei.uta.edu.ec.tipcalculator.domain.model;

import java.util.Objects;

/**
 * Modelo inmutable que representa el resultado del cálculo de propina y total.
 * Cumple con SRP al encapsular únicamente los datos calculados.
 */
public class TipResult {
    private final double tip;
    private final double total;

    public TipResult(double tip, double total) {
        this.tip = tip;
        this.total = total;
    }

    public double getTip() {
        return tip;
    }

    public double getTotal() {
        return total;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TipResult tipResult = (TipResult) o;
        return Double.compare(tipResult.tip, tip) == 0 &&
                Double.compare(tipResult.total, total) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(tip, total);
    }
}
