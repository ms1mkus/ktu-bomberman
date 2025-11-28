import java.util.Random;
import java.time.LocalDateTime;

public class RandomGenerator {
    private static RandomGenerator instance = null;
    private final Random random;
    
    private RandomGenerator() {
        this.random = new Random();
        // System.out.println("RNG SINGLETON INSTANCE CREATED - " + LocalDateTime.now().toString());
    }

    public static synchronized RandomGenerator getInstance() {
        if (instance == null) {
            instance = new RandomGenerator();
        }
        // System.out.println("RNG INSTANCE ACCESSED - " + LocalDateTime.now().toString());
        return instance;
    }
    
    public int nextInt(int min, int max) {
        return random.nextInt(max - min + 1) + min;
    }
    
    public double nextDouble() {
        return random.nextDouble();
    }
    
    public boolean nextBoolean() {
        return random.nextBoolean();
    }
    
    public boolean checkProbability(double probability) {
        if (probability <= 0) return false;
        if (probability >= 1) return true;
        return random.nextDouble() < probability;
    }
}