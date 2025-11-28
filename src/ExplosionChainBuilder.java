public class ExplosionChainBuilder {
    public static ExplosionEffectHandler buildExplosionChain() {
        ExplosionEffectHandler wallHandler = new WallProtectionHandler();
        ExplosionEffectHandler blockHandler = new BlockDestructionHandler();
        ExplosionEffectHandler bombHandler = new BombChainReactionHandler();
        ExplosionEffectHandler playerHandler = new PlayerDamageHandler();
        ExplosionEffectHandler visualHandler = new VisualEffectHandler();
        
        wallHandler.setNext(blockHandler);
        blockHandler.setNext(bombHandler);
        bombHandler.setNext(playerHandler);
        playerHandler.setNext(visualHandler);
        
        return wallHandler;
    }
}