package fisei.uta.edu.ec.tipcalculator.util;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

/**
 * TextWatcher encargado exclusivamente de:
 * 1. Parsear el monto ingresado en el EditText ignorando el símbolo "$".
 * 2. Formatear el campo anteponiendo el símbolo "$" cuando corresponda.
 * 3. Mantener el correcto reposicionamiento del cursor.
 * 4. Notificar a un listener desacoplado (OnAmountChangedListener) el nuevo valor numérico.
 *
 * Cumple con SRP (solo manipulación y parseo de input) e ISP (listener de método único).
 */
public class AmountInputWatcher implements TextWatcher {

    /**
     * Interfaz segregada para notificar el cambio del monto numérico parseado.
     * Cumple con ISP y DIP.
     */
    public interface OnAmountChangedListener {
        void onAmountChanged(double amount);
    }

    private final EditText editText;
    private final OnAmountChangedListener listener;
    private boolean isEditing = false;

    public AmountInputWatcher(EditText editText, OnAmountChangedListener listener) {
        this.editText = editText;
        this.listener = listener;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        // No requerido
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (isEditing) return;

        String original = s.toString();
        String cleanString = original.replace("$", "");
        double billAmount;

        try {
            if (cleanString.isEmpty() || cleanString.equals(".")) {
                billAmount = 0.0;
            } else {
                billAmount = Double.parseDouble(cleanString);
            }
        } catch (NumberFormatException e) {
            billAmount = 0.0;
        }

        if (listener != null) {
            listener.onAmountChanged(billAmount);
        }

        String targetText = cleanString.isEmpty() ? "" : "$" + cleanString;

        if (!original.equals(targetText)) {
            isEditing = true;

            int selectionStart = editText.getSelectionStart();
            int selectionEnd = editText.getSelectionEnd();

            int newStart = selectionStart;
            int newEnd = selectionEnd;

            if (!original.startsWith("$") && targetText.startsWith("$")) {
                newStart += 1;
                newEnd += 1;
            } else if (original.startsWith("$") && !targetText.startsWith("$")) {
                newStart = 0;
                newEnd = 0;
            }

            newStart = Math.max(0, Math.min(newStart, targetText.length()));
            newEnd = Math.max(0, Math.min(newEnd, targetText.length()));

            editText.setText(targetText);
            editText.setSelection(newStart, newEnd);

            isEditing = false;
        }
    }

    @Override
    public void afterTextChanged(Editable s) {
        // No requerido
    }
}
