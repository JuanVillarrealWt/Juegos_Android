package fisei.uta.edu.ec.welcomeca.presentation.main;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import fisei.uta.edu.ec.welcomeca.R;
import fisei.uta.edu.ec.welcomeca.data.repository.WelcomeRepositoryImpl;
import fisei.uta.edu.ec.welcomeca.domain.model.WelcomeInfo;
import fisei.uta.edu.ec.welcomeca.domain.repository.WelcomeRepository;
import fisei.uta.edu.ec.welcomeca.domain.usecase.GetWelcomeMessageUseCase;

public class MainActivity extends AppCompatActivity {

    private GetWelcomeMessageUseCase getWelcomeMessageUseCase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Inicializamos las dependencias de forma limpia (Clean Architecture / Inyección manual)
        WelcomeRepository repository = new WelcomeRepositoryImpl(this);
        getWelcomeMessageUseCase = new GetWelcomeMessageUseCase(repository);

        // Referencias a las vistas de la UI
        TextView welcomeTextView = findViewById(R.id.welcomeTextView);
        ImageView bugImageView = findViewById(R.id.bugImageView);

        // Ejecutamos el Caso de Uso para obtener la información de negocio
        WelcomeInfo welcomeInfo = getWelcomeMessageUseCase.execute();

        // Pintamos los datos en la interfaz
        welcomeTextView.setText(welcomeInfo.getMessage());
        bugImageView.setImageResource(welcomeInfo.getImageResId());
    }
}