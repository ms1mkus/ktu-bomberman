class PlayerAbilitiesInfoProvider {
    private PlayerAbilities abilities;
    private String playerColor;
    
    public PlayerAbilitiesInfoProvider(PlayerAbilities abilities, String playerColor) {
        this.abilities = abilities;
        this.playerColor = playerColor;
    }
    
    public String fetchMovementVelocity() {
        return abilities.getMovementSpeed() + " px/s";
    }
    
    public String fetchExplosionRadius() {
        return abilities.getExplosionRange() + " blocks";
    }
    
    public String checkGhostModeStatus() {
        return abilities.isGhost() ? "ACTIVE" : "";
    }
    
    public String retrievePlayerIdentifier() {
        return playerColor.toUpperCase() + " Player (ADAPTER)";
    }
    
    public String getBombTypeDescription() {
        return "Standard";
    }
    
    public String getPrimaryStatLabel() {
        return "[MOVEMENT]";
    }
    
    public String getPrimaryDataName() {
        return "Speed";
    }
    
    public String getSecondaryStatLabel() {
        return "[BOMB POWER]";
    }
    
    public String getSecondaryDataName() {
        return "Range";
    }
    
    public String getStatusFieldName() {
        return abilities.isGhost() ? "Ghost Mode" : "Mode";
    }
    
    public String getTertiaryStatLabel() {
        return "[BOMB TYPE]";
    }
    
    public String getTertiaryDataName() {
        return "Type";
    }
}
