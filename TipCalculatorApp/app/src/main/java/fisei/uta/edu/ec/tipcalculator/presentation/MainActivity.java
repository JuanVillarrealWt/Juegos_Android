package fisei.uta.edu.ec.tipcalculator.presentation;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import fisei.uta.edu.ec.tipcalculator.R;
import fisei.uta.edu.ec.tipcalculator.domain.usecase.CalculateTipUseCase;
import fisei.uta.edu.ec.tipcalculator.domain.usecase.CalculateTipUseCaseImpl;
import fisei.uta.edu.ec.tipcalculator.util.AmountInputWatcher;
import fisei.uta.edu.ec.tipcalculator.util.CurrencyFormatter;
import fisei.uta.edu.ec.tipcalculator.util.ICurrencyFormatter;
import fisei.uta.edu.ec.tipcalculator.util.IPercentFormatter;
import fisei.uta.edu.ec.tipcalculator.util.PercentFormatter;

/**
 * MainActivity actúa exclusivamente como vista (capa de presentación).
 *
 * Cumple con:
 * - SRP: Su única responsabilidad es el ciclo de vida de la UI, inflar la vista,
 *   conectar listeners y observar el ViewModel. No realiza ningún cálculo ni formateo.
 * - DIP: Depende de abstracciones (interfaces) que son inyectadas en la factory del ViewModel.
 */
public class MainActivity extends AppCompatActivity {

    private MainViewModel viewModel;

    private EditText editTextAmount;
    private TextView textViewAmount;
    private TextView textViewPercent;
    private SeekBar seekBarPercent;
    private TextView textViewTip;
    private TextView textViewTotal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
        initViewModel();
        initListeners();
        observeViewModel();

        editTextAmount.requestFocus();
    }

    private void initViews() {
        editTextAmount = findViewById(R.id.editTextAmount);
        textViewAmount = findViewById(R.id.textViewAmount);
        textViewPercent = findViewById(R.id.textViewPercent);
        seekBarPercent = findViewById(R.id.seekBarPercent);
        textViewTip = findViewById(R.id.textViewTip);
        textViewTotal = findViewById(R.id.textViewTotal);
    }

    private void initViewModel() {
        // Inversión de dependencias (DIP): dependemos de interfaces/abstracciones
        CalculateTipUseCase calculateTipUseCase = new CalculateTipUseCaseImpl();
        ICurrencyFormatter currencyFormatter = new CurrencyFormatter();
        IPercentFormatter percentFormatter = new PercentFormatter();

        MainViewModelFactory factory = new MainViewModelFactory(
                calculateTipUseCase,
                currencyFormatter,
                percentFormatter
        );

        viewModel = new ViewModelProvider(this, factory).get(MainViewModel.class);
    }

    private void initListeners() {
        // Delegamos la manipulación de texto al watcher utilitario desacoplado
        editTextAmount.addTextChangedListener(new AmountInputWatcher(
                editTextAmount,
                amount -> viewModel.setBillAmount(amount)
        ));

        // El listener de la barra deslizante solo delega el nuevo porcentaje al ViewModel
        seekBarPercent.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                viewModel.setPercent(progress / 100.0);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) { }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) { }
        });
    }

    private void observeViewModel() {
        viewModel.getUiState().observe(this, state -> {
            textViewPercent.setText(state.getPercentFormatted());
            textViewTip.setText(state.getTipFormatted());
            textViewTotal.setText(state.getTotalFormatted());
            if (textViewAmount != null) {
                textViewAmount.setText(state.getAmountFormatted());
            }
        });
    }
}
