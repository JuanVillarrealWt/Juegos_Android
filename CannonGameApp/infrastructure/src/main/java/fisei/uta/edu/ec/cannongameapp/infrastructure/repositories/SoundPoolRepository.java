package fisei.uta.edu.ec.cannongameapp.infrastructure.repositories;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.SoundPool;
import android.util.SparseIntArray;

import fisei.uta.edu.ec.cannongameapp.infrastructure.R;
import fisei.uta.edu.ec.cannongameapp.application.contracts.repositories.SoundRepository;

public class SoundPoolRepository implements SoundRepository {
    private static final int TARGET_SOUND_ID = 0;
    private static final int CANNON_SOUND_ID = 1;
    private static final int BLOCKER_SOUND_ID = 2;

    private SoundPool soundPool;
    private final SparseIntArray soundMap;

    public SoundPoolRepository(Context context) {
        AudioAttributes attrBuilder = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build();

        soundPool = new SoundPool.Builder()
                .setMaxStreams(3)
                .setAudioAttributes(attrBuilder)
                .build();

        soundMap = new SparseIntArray(3);
        soundMap.put(TARGET_SOUND_ID, soundPool.load(context, R.raw.target_hit, 1));
        soundMap.put(CANNON_SOUND_ID, soundPool.load(context, R.raw.cannon_fire, 1));
        soundMap.put(BLOCKER_SOUND_ID, soundPool.load(context, R.raw.blocker_hit, 1));
    }

    @Override
    public void playTargetHit() {
        playSound(TARGET_SOUND_ID);
    }

    @Override
    public void playCannonFire() {
        playSound(CANNON_SOUND_ID);
    }

    @Override
    public void playBlockerHit() {
        playSound(BLOCKER_SOUND_ID);
    }

    private void playSound(int soundId) {
        if (soundPool != null && soundMap.indexOfKey(soundId) >= 0) {
            soundPool.play(soundMap.get(soundId), 1, 1, 1, 0, 1.0f);
        }
    }

    @Override
    public void release() {
        if (soundPool != null) {
            soundPool.release();
            soundPool = null;
        }
    }
}
