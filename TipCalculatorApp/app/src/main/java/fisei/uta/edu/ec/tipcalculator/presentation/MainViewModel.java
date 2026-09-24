package fisei.uta.edu.ec.tipcalculator.presentation;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import fisei.uta.edu.ec.tipcalculator.domain.model.TipResult;
import fisei.uta.edu.ec.tipcalculator.domain.usecase.CalculateTipUseCase;
import fisei.uta.edu.ec.tipcalculator.util.ICurrencyFormatter;
import fisei.uta.edu.ec.tipcalculator.util.IPercentFormatter;

/**
 * ViewModel encargado de mantener el estado de la UI (monto, porcentaje, propina, total)
 * y coordinar con el caso de uso y los formateadores.
 *
 * Cumple con:
 * - SRP: Su única responsabilidad es gestionar el estado de presentación.
 * - DIP: Depende de abstracciones (CalculateTipUseCase, ICurrencyFormatter, IPercentFormatter)
 *   inyectadas a través de su constructor, no de implementaciones concretas.
 */
public class MainViewModel extends ViewModel {

    private final CalculateTipUseCase calculateTipUseCase;
    private final ICurrencyFormatter currencyFormatter;
    private final IPercentFormatter percentFormatter;

    private double billAmount = 0.0;
    private double percent = 0.15;

    private final MutableLiveData<TipUiState> uiState = new MutableLiveData<>();

    public MainViewModel(CalculateTipUseCase calculateTipUseCase,
                         ICurrencyFormatter currencyFormatter,
                         IPercentFormatter percentFormatter) {
        this.calculateTipUseCase = calculateTipUseCase;
        this.currencyFormatter = currencyFormatter;
        this.percentFormatter = percentFormatter;

        // Estado inicial
        updateState();
    }

    public LiveData<TipUiState> getUiState() {
        return uiState;
    }

    public void setBillAmount(double amount) {
        this.billAmount = amount;
        updateState();
    }

    public void setPercent(double percent) {
        this.percent = percent;
        updateState();
    }

    public double getBillAmount() {
        return billAmount;
    }

    public double getPercent() {
        return percent;
    }

    private void updateState() {
        TipResult result = calculateTipUseCase.calculate(billAmount, percent);

        String formattedPercent = percentFormatter.format(percent);
        String formattedTip = currencyFormatter.format(result.getTip());
        String formattedTotal = currencyFormatter.format(result.getTotal());
        String formattedAmount = currencyFormatter.format(billAmount);

        TipUiState state = new TipUiState(
                billAmount,
                percent,
                result.getTip(),
                result.getTotal(),
                formattedPercent,
                formattedTip,
                formattedTotal,
                formattedAmount
        );

        uiState.setValue(state);
    }
}
