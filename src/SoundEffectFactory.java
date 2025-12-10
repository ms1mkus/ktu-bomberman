import java.util.Hashtable;

// Factory that manages the flyweight SoundEffect objects
public class SoundEffectFactory {
    private static SoundEffectFactory instance;
    private final static Hashtable<String, SoundEffect> soundPool = new Hashtable<String, SoundEffect>();
    
    private SoundEffectFactory() {
    }
    
    public static SoundEffectFactory getInstance() {
        if (instance == null) {
            instance = new SoundEffectFactory();
        }
        return instance;
    }
    
    public static SoundEffect getSound(String soundType) {
        SoundEffect sound = soundPool.get(soundType);
        
        if (sound == null) {
            if (soundType.equals("bullet_shot")) {
                sound = new SoundEffect(soundType);
            } else if (soundType.equals("bullet_hit")) {
                sound = new SoundEffect(soundType);
            } else if (soundType.equals("bomb_plant")) {
                sound = new SoundEffect(soundType);
            } else if (soundType.equals("bomb_explode")) {
                sound = new SoundEffect(soundType);
            } else if (soundType.equals("explosion")) {
                sound = new SoundEffect(soundType);
            } else if (soundType.equals("powerup_pickup")) {
                sound = new SoundEffect(soundType);
            }
            
            if (sound != null) {
                soundPool.put(soundType, sound);
            }
        }
        
        return sound;
    }
    
    public int getPoolSize() {
        return soundPool.size();
    }
    
    public void clearPool() {
        soundPool.clear();
    }
}