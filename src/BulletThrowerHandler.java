class BulletThrowerHandler implements ThrowerHandler {
    boolean bulletFired;
    int id, startX, startY, targetX, targetY;
    AbstractWeaponFactory abstractWeaponFactory;
    Magazine magazine;
    Barrel barrel;
    long lastShotTime = 0;
    int consecutiveShotCount = 0;
    boolean isReloading = false;
    long reloadStartTime = 0;

    BulletThrowerHandler(int id) {
        this.id = id;
        this.bulletFired = false;
        this.abstractWeaponFactory = getAbstractWeaponFactory(id);
        this.magazine = (Magazine) abstractWeaponFactory.getMagazine();
        this.barrel = (Barrel) abstractWeaponFactory.getBarrel();
    }

    AbstractWeaponFactory getAbstractWeaponFactory(int id) {
        if(id%3 == 0) {
            return new TornadoWeaponFactory();
        } else if (id%3 == 1) {
            return new FastWeaponFactory();
        } else {
            return new HeavyWeaponFactory();
        }
    }

    void setBulletFired(int startX, int startY, int targetX, int targetY) {
        long currentTime = System.currentTimeMillis();
        
        if (isReloading) {
            if (currentTime - reloadStartTime >= magazine.getReloadTime()) {
                isReloading = false;
                magazine.reload();
            } else {
                return;
            }
        }
        
        if (magazine.getCurrentCapacity() <= 0) {
            isReloading = true;
            reloadStartTime = currentTime;
            return;
        }
        
        if (lastShotTime > 0 && currentTime - lastShotTime < barrel.getFireRate()) {
            return;
        }
        
        if (lastShotTime == 0 || currentTime - lastShotTime > 1000) {
            consecutiveShotCount = 0;
        }
        consecutiveShotCount++;
        
        magazine.setCurrentCapacity(magazine.getCurrentCapacity() - 1);
        
        this.startX = startX;
        this.startY = startY;
        this.targetX = targetX;
        this.targetY = targetY;
        this.bulletFired = true;
        this.lastShotTime = currentTime;
    }

    static void sendBulletUpdate(String bulletData) {
        ClientManager.sendToAllClients("-1 bulletUpdate " + bulletData);
    }

    private int calculateDirection(int sx, int sy, int tx, int ty) {
        int deltaX = tx - sx;
        int deltaY = ty - sy;
        
        if (Math.abs(deltaX) > Math.abs(deltaY)) {
            return deltaX > 0 ? 1 : 3;
        } else {
            return deltaY > 0 ? 2 : 0;
        }
    }

    private boolean checkWallCollision(int x, int y) {
        int mapX = x / Const.SIZE_SPRITE_MAP;
        int mapY = y / Const.SIZE_SPRITE_MAP;
        
        if (mapX < 0 || mapX >= Const.COL || mapY < 0 || mapY >= Const.LIN) {
            return true;
        }
        
        return Server.map[mapY][mapX].img.contains("wall");
    }

    private boolean checkBlockCollision(int x, int y, int damage, Bullet bullet) {
        int mapX = x / Const.SIZE_SPRITE_MAP;
        int mapY = y / Const.SIZE_SPRITE_MAP;
        
        if (mapX >= 0 && mapX < Const.COL && mapY >= 0 && mapY < Const.LIN) {
            if (Server.map[mapY][mapX].img.equals("block")) {
                String blockKey = mapX + "," + mapY;
                int currentHealth = BlockHealthManager.getHealth(blockKey);
                
                double penetration = bullet.calculatePenetration(currentHealth);
                int actualDamage = (int)(damage * penetration);
                
                currentHealth -= actualDamage;
                BlockHealthManager.setHealth(blockKey, currentHealth);
                
                ClientManager.sendToAllClients("-1 blockHealth " + blockKey + " " + currentHealth);
                
                if (currentHealth <= 0) {
                    new Thrower("block-on-fire", Const.indexBlockOnFire, Const.RATE_BLOCK_UPDATE, mapY, mapX) {
                        @Override
                        public void run() {
                            super.run();
                            // After destruction, spawn a potion occasionally
                            RandomGenerator rng = RandomGenerator.getInstance();
                            if (rng.checkProbability(0.5)) {
                                Potion.Type t = rng.nextDouble() < 0.5 ? Potion.Type.HEALING : Potion.Type.POISON;
                                PotionManager.spawnPotionOnGround(mapY, mapX, t);
                            } else {
                                MapUpdatesThrowerHandler.changeMap("floor-1", mapY, mapX);
                            }
                        }
                    }.start();
                }
                
                return penetration < 1.0;
            }
        }
        return false;
    }

    private boolean checkPlayerCollision(int x, int y) {
        for (int playerId = 0; playerId < Const.QTY_PLAYERS; playerId++) {
            if (playerId != id && Server.player[playerId].alive) {
                int px = Server.player[playerId].x;
                int py = Server.player[playerId].y;
                
                if (x >= px && x <= px + Const.WIDTH_SPRITE_PLAYER && 
                    y >= py && y <= py + Const.HEIGHT_SPRITE_PLAYER) {
                    Server.player[playerId].alive = false;
                    ClientManager.sendToAllClients(playerId + " newStatus dead");
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void run() {
        while (true) {
            if (bulletFired) {
                bulletFired = false;
                
                Bullet bullet = (Bullet) abstractWeaponFactory.getBullet();
                
                long currentTime = System.currentTimeMillis();
                long timeSinceLastShot = lastShotTime > 0 ? (currentTime - lastShotTime) : 1000;
                
                int bulletCount = bullet.getBulletCount();

                
                for (int i = 0; i < bulletCount; i++) {
                    long bulletId = System.currentTimeMillis() + id * 1000 + i;
                    
                    int shotNumberForSpray = Math.max(0, consecutiveShotCount - 1);
                    int[] individualSprayOffset = bullet.calculateSprayOffset(shotNumberForSpray, timeSinceLastShot);
                    
                    int pelletTargetX = targetX + individualSprayOffset[0];
                    int pelletTargetY = targetY + individualSprayOffset[1];
                    
                    if (bulletCount > 1) {
                        pelletTargetX += (int)(Math.random() * 160 - 80);
                        pelletTargetY += (int)(Math.random() * 160 - 80);
                    }
                    
                    pelletTargetX += (int)(Math.random() * 100 - 50);
                    pelletTargetY += (int)(Math.random() * 100 - 50);
                    
                    int bulletDirection = calculateDirection(startX, startY, pelletTargetX, pelletTargetY);
                    
                    sendBulletUpdate("create " + bulletId + " " + startX + " " + startY + " " + bulletDirection + " " + bullet.getSpriteType());

                    
                    new BulletMovementThread(bulletId, startX, startY, pelletTargetX, pelletTargetY, bulletDirection, bullet.getSpeed(), bullet.getDamage(), bullet).start();
                }
                
            }
            try { 
                Thread.sleep(10);
            } catch (InterruptedException e) {}
        }
    }
    
    private class BulletMovementThread extends Thread {
    private long bulletId;
    private int targetX, targetY, speed, damage;
        private int[] pos;
        private double velocityX, velocityY;
        private Bullet bullet;
        
        BulletMovementThread(long bulletId, int startX, int startY, int targetX, int targetY, int direction, int speed, int damage, Bullet bullet) {
            this.bulletId = bulletId;
            this.targetX = targetX;
            this.targetY = targetY;
            this.speed = speed;
            this.damage = damage;
            this.bullet = bullet;
            this.pos = new int[]{startX, startY};
            
            int deltaX = targetX - startX;
            int deltaY = targetY - startY;
            double distance = Math.sqrt(deltaX * deltaX + deltaY * deltaY);
            
            if (distance > 0) {
                this.velocityX = (deltaX / distance) * speed;
                this.velocityY = (deltaY / distance) * speed;
            } else {
                this.velocityX = speed;
                this.velocityY = 0;
            }
        }
        
        @Override
        public void run() {
            boolean active = true;
            double currentX = pos[0];
            double currentY = pos[1];
            
            while (active) {
                currentX += velocityX;
                currentY += velocityY;
                pos[0] = (int) Math.round(currentX);
                pos[1] = (int) Math.round(currentY);
                
                sendBulletUpdate("move " + bulletId + " " + pos[0] + " " + pos[1]);
                
                if (checkWallCollision(pos[0], pos[1]) || 
                    checkBlockCollision(pos[0], pos[1], damage, bullet) || 
                    checkPlayerCollision(pos[0], pos[1])) {
                    active = false;
                }
                
                int distance = (int) Math.sqrt(Math.pow(pos[0] - targetX, 2) + Math.pow(pos[1] - targetY, 2));
                if (distance <= speed) {
                    active = false;
                }
                
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {}
            }
            
            sendBulletUpdate("destroy " + bulletId);
        }
    }
}