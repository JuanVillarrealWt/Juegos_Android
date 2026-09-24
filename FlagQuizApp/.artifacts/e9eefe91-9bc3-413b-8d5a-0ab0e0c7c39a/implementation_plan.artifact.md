# Clean Architecture Refactoring Plan for FlagQuiz

Refactor the project's Java source code structure from a single package (`fisei.uta.edu.ec.flagquiz`) into Clean Architecture layers without modifying `res` or `assets`.

## User Review Required

> [!IMPORTANT]
> - No `res` or `assets` files will be modified.
> - Activities and Fragments remain in `presentation`.
> - Business logic and data loading will be separated into `domain`, `application`, and `infrastructure` layers.

## Proposed Changes

### [Domain Layer]
- `domain.entities.Country`: Entity representing country name, region, and flag file identifier.

### [Application Layer]
- `application.contracts.QuizRepository`: Interface defining data operations for loading quiz countries and assets.
- `application.usecases.GetQuizQuestionsUseCase` (or quiz management logic): Use case for selecting random flags and validating guesses.

### [Infrastructure Layer]
- `infrastructure.repositories.AssetQuizRepository`: Implementation of `QuizRepository` using `AssetManager` and `SharedPreferences`.

### [Presentation Layer]
- `presentation.MainActivity`
- `presentation.MainActivityFragment`
- `presentation.SettingsActivity`
- `presentation.SettingsActivityFragment`

### [Composition Layer]
- `composition.AppContainer` or dependency factory to wire infrastructure repositories to use cases and presentation fragments.

## Verification Plan

### Automated Tests
- Build project (`app:assembleDebug`) to ensure compilation and correct package imports.

### Manual Verification
- Run the app on an emulator/device to verify game flow, settings, preferences changes, and quiz scoring.
