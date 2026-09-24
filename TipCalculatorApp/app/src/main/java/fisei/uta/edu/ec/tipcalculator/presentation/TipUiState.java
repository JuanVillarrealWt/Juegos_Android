package fisei.uta.edu.ec.tipcalculator.presentation;

import java.util.Objects;

/**
 * Estado inmutable de la interfaz de usuario para la pantalla principal.
 * Contiene tanto los valores crudos como los valores ya formateados para que
 * la Activity no tenga que realizar ningún tipo de cálculo ni formateo.
 */
public class TipUiState {
    private final double billAmount;
    private final double percent;
    private final double tip;
    private final double total;
    private final String percentFormatted;
    private final String tipFormatted;
    private final String totalFormatted;
    private final String amountFormatted;

    public TipUiState(double billAmount,
                      double percent,
                      double tip,
                      double total,
                      String percentFormatted,
                      String tipFormatted,
                      String totalFormatted,
                      String amountFormatted) {
        this.billAmount = billAmount;
        this.percent = percent;
        this.tip = tip;
        this.total = total;
        this.percentFormatted = percentFormatted;
        this.tipFormatted = tipFormatted;
        this.totalFormatted = totalFormatted;
        this.amountFormatted = amountFormatted;
    }

    public double getBillAmount() {
        return billAmount;
    }

    public double getPercent() {
        return percent;
    }

    public double getTip() {
        return tip;
    }

    public double getTotal() {
        return total;
    }

    public String getPercentFormatted() {
        return percentFormatted;
    }

    public String getTipFormatted() {
        return tipFormatted;
    }

    public String getTotalFormatted() {
        return totalFormatted;
    }

    public String getAmountFormatted() {
        return amountFormatted;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TipUiState that = (TipUiState) o;
        return Double.compare(that.billAmount, billAmount) == 0 &&
                Double.compare(that.percent, percent) == 0 &&
                Double.compare(that.tip, tip) == 0 &&
                Double.compare(that.total, total) == 0 &&
                Objects.equals(percentFormatted, that.percentFormatted) &&
                Objects.equals(tipFormatted, that.tipFormatted) &&
                Objects.equals(totalFormatted, that.totalFormatted) &&
                Objects.equals(amountFormatted, that.amountFormatted);
    }

    @Override
    public int hashCode() {
        return Objects.hash(billAmount, percent, tip, total, percentFormatted, tipFormatted, totalFormatted, amountFormatted);
    }
}
