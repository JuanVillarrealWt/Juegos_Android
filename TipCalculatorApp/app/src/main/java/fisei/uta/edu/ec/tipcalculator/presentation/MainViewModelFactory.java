package fisei.uta.edu.ec.tipcalculator.presentation;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import fisei.uta.edu.ec.tipcalculator.domain.usecase.CalculateTipUseCase;
import fisei.uta.edu.ec.tipcalculator.util.ICurrencyFormatter;
import fisei.uta.edu.ec.tipcalculator.util.IPercentFormatter;

/**
 * Factory para la creación de MainViewModel con inyección de dependencias manual.
 * Cumple con DIP al recibir abstracciones (interfaces).
 */
public class MainViewModelFactory implements ViewModelProvider.Factory {

    private final CalculateTipUseCase calculateTipUseCase;
    private final ICurrencyFormatter currencyFormatter;
    private final IPercentFormatter percentFormatter;

    public MainViewModelFactory(CalculateTipUseCase calculateTipUseCase,
                                ICurrencyFormatter currencyFormatter,
                                IPercentFormatter percentFormatter) {
        this.calculateTipUseCase = calculateTipUseCase;
        this.currencyFormatter = currencyFormatter;
        this.percentFormatter = percentFormatter;
    }

    @NonNull
    @Override
    @SuppressWarnings("unchecked")
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(MainViewModel.class)) {
            return (T) new MainViewModel(calculateTipUseCase, currencyFormatter, percentFormatter);
        }
        throw new IllegalArgumentException("Unknown ViewModel class: " + modelClass.getName());
    }
}
