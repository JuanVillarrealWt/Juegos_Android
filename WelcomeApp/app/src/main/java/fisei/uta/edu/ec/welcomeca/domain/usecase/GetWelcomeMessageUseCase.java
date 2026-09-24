package fisei.uta.edu.ec.welcomeca.domain.usecase;

import fisei.uta.edu.ec.welcomeca.domain.model.WelcomeInfo;
import fisei.uta.edu.ec.welcomeca.domain.repository.WelcomeRepository;

public class GetWelcomeMessageUseCase {
    private final WelcomeRepository repository;

    public GetWelcomeMessageUseCase(WelcomeRepository repository) {
        this.repository = repository;
    }

    public WelcomeInfo execute() {
        return repository.getWelcomeInfo();
    }
}