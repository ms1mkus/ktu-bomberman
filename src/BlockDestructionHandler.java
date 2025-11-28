public class BlockDestructionHandler implements ExplosionEffectHandler {
    private ExplosionEffectHandler next;
    
    @Override
    public boolean canHandle(int line, int col) {
        return Server.map[line][col].img.contains("block");
    }
    
    @Override
    public ExplosionResult handleExplosion(int line, int col, int bombOwnerId, int bombL, int bombC, int dirL, int dirC, int distance, int range) {
        //System.out.println("Destroying block at (" + line + "," + col + ")");
        
        // Start block burning animation
        new Thrower("block-on-fire", Const.indexBlockOnFire, Const.RATE_BLOCK_UPDATE, line, col) {
            @Override
            public void run() {
                super.run();
                spawnRandomPowerUp(line, col);
            }
        }.start();
        
        return ExplosionResult.STOP;
    }
    
    private void spawnRandomPowerUp(int line, int col) {
        RandomGenerator randomGen = RandomGenerator.getInstance();
        if (randomGen.checkProbability(0.5)) {
            double p = randomGen.nextDouble();
            if (p < 0.5) {
                MapUpdatesThrowerHandler.changeMap("powerup-bigbomb", line, col);
            } else if (p < 0.8) {
                MapUpdatesThrowerHandler.changeMap("powerup-speedboost", line, col);
            } else {
                MapUpdatesThrowerHandler.changeMap("powerup-ghost", line, col);
            }
            System.out.println("Power-up spawned at (" + line + "," + col + ")");
        }
    }
    
    @Override
    public void setNext(ExplosionEffectHandler next) {
        this.next = next;
    }
}