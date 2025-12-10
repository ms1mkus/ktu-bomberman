public class SoundFlyweightTest {
    public static void main(String[] args) throws Exception {
        System.out.println("=== Flyweight Pattern Performance Test ===\n");
        
        String[] sounds = {"bullet_shot", "bullet_hit", "bomb_plant", "bomb_explode", "powerup_picked"};
        int tests = 500;
        
        System.out.println("Without Flyweight: ");
        System.gc();
        
        long startTime1 = System.currentTimeMillis();
        long startMem1 = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        
        int loaded1 = 0;
        for (int i = 0; i < tests; i++) {
            SoundEffect sound = new SoundEffect(sounds[i % 4]);
            if (sound.isLoaded()) loaded1++;
        }
        
        long endTime1 = System.currentTimeMillis();
        long endMem1 = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        
        System.out.println("   Time: " + (endTime1 - startTime1) + " ms");
        System.out.println("   Memory: " + ((endMem1 - startMem1) / 1024) + " KB");
        System.out.println("   Loaded: " + loaded1 + "/" + tests);
        
        System.gc();
        Thread.sleep(100);
        
        System.out.println("\nWith Flyweight: ");
        
        SoundEffectFactory.getInstance().clearPool();
        
        long startTime2 = System.currentTimeMillis();
        long startMem2 = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        
        int loaded2 = 0;
        for (int i = 0; i < tests; i++) {
            SoundEffect sound = SoundEffectFactory.getSound(sounds[i % 4]);
            if (sound != null && sound.isLoaded()) loaded2++;
        }
        
        long endTime2 = System.currentTimeMillis();
        long endMem2 = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        
        System.out.println("   Time: " + (endTime2 - startTime2) + " ms");
        System.out.println("   Memory: " + ((endMem2 - startMem2) / 1024) + " KB");
        System.out.println("   Loaded: " + loaded2 + "/" + tests);
    }
}