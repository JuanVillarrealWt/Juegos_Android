package fisei.uta.edu.ec.cannongameapp.application.contracts.repositories;

public interface SoundRepository {
    void playTargetHit();
    void playCannonFire();
    void playBlockerHit();
    void release();
}
