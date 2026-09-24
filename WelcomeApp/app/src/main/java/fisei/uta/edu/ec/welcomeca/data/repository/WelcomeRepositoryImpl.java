package fisei.uta.edu.ec.welcomeca.data.repository;

import android.content.Context;

import fisei.uta.edu.ec.welcomeca.R;
import fisei.uta.edu.ec.welcomeca.domain.model.WelcomeInfo;
import fisei.uta.edu.ec.welcomeca.domain.repository.WelcomeRepository;

public class WelcomeRepositoryImpl implements WelcomeRepository {
    private final Context context;

    // Recibimos el Context de Android para poder acceder a los recursos de strings y drawables
    public WelcomeRepositoryImpl(Context context) {
        this.context = context;
    }

    @Override
    public WelcomeInfo getWelcomeInfo() {
        // Obtenemos el texto desde los recursos (strings.xml) asegurando la internacionalización
        String message = context.getString(R.string.welcome);

        // Obtenemos la referencia de la imagen desde los recursos (drawable)
        int imageResId = R.drawable.bug;

        return new WelcomeInfo(message, imageResId);
    }
}