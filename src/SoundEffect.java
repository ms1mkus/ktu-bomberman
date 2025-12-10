import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;

public class SoundEffect {
    private Clip clip;
    private boolean isLoaded;
    private String soundKey;
    
    public SoundEffect(String soundKey) {
        this.soundKey = soundKey;
        this.isLoaded = false;
        loadSound();
    }
    
    private void loadSound() {
        try {
            String path = Const.BOMBERMAN_RESOURCES_DIR + "sounds/" + soundKey + ".wav";
            File soundFile = new File(path);
            
            if (!soundFile.exists()) {
                System.err.println("Sound file not found: " + path);
                return;
            }
            
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(soundFile);
            
            clip = AudioSystem.getClip();
            clip.open(audioStream);
            isLoaded = true;
            
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            System.err.println("Error loading sound: " + soundKey + " - " + e.getMessage());
        }
    }
    
    public void play(float volume) {
        if (!isLoaded || clip == null) return;
        
        if (clip.isRunning()) {
            clip.stop();
        }
        
        // Reset to beginning
        clip.setFramePosition(0);
        
        // Set volume 
        if (clip.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
            FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
            float range = gainControl.getMaximum() - gainControl.getMinimum();
            float gain = (range * volume) + gainControl.getMinimum();
            gainControl.setValue(gain);
        }
        
        clip.start();
    }
    
    public void play() {
        play(1.0f); // Default volume
    }
    
    public void stop() {
        if (clip != null && clip.isRunning()) {
            clip.stop();
        }
    }
    
    public boolean isLoaded() {
        return isLoaded;
    }
}