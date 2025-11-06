import java.awt.AlphaComposite;
import java.awt.Graphics;
import javax.swing.JPanel;

//for both you and enemy
public class Player implements Cloneable {
   int x, y;
   String status, color;
   JPanel panel;
   boolean alive;

   PlayerAbilities abilities;
   private PlayerElement playerElement;
   public PlayerHat hat;

   StatusChanger sc;

   Player(int id, JPanel panel, SkinManager skinManager) throws InterruptedException {
      this.x = Client.spawn[id].x;
      this.y = Client.spawn[id].y;
      this.color = Sprite.personColors[id];
      this.panel = panel;
      this.alive = Client.alive[id];
      this.abilities = new BasicPlayer();
      this.hat = new PlayerHat();
      this.playerElement = skinManager.createPlayer(id, "wait");
      this.color = playerElement.getPlayerColor();

      (sc = new StatusChanger(this, "wait")).start();
   }

   public void draw(Graphics g) {
      if (alive) {
         String spriteKey = playerElement.getSpriteKey();
         // Apply transparency if player is in ghost mode (for other players)
         if (this != Game.you && isGhost()) {
            java.awt.Graphics2D g2d = (java.awt.Graphics2D) g;
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.1f));
            g.drawImage(Sprite.ht.get(color + "/" + status), x, y, Const.WIDTH_SPRITE_PLAYER, Const.HEIGHT_SPRITE_PLAYER, null);
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
         } 
         else {
            // Draw normally
            g.drawImage(Sprite.ht.get(spriteKey), x, y, Const.WIDTH_SPRITE_PLAYER, Const.HEIGHT_SPRITE_PLAYER, null);
         }
         
         if (hat != null) {
            hat.draw(g, x, y, Const.WIDTH_SPRITE_PLAYER, Const.HEIGHT_SPRITE_PLAYER);
         }
      }
   }
   public void updateStatus(String newStatus) {
      this.status = newStatus;
      playerElement.setStatus(newStatus);
   }

   public void addGhost() {
      this.abilities = new GhostDecorator(this.abilities);
   }

   public boolean isGhost() {
      return abilities.isGhost();
   }

   public void addBigBomb() {
      this.abilities = new BigBombDecorator(abilities);
   }

   public int getExplosionRange() {
      return abilities.getExplosionRange();
   }
   
   public int getMovementSpeed() {
      return abilities.getMovementSpeed();
   }

   public void addSpeedBoost() {
      this.abilities = new SpeedBoostDecorator(abilities);
   }
   
   public void triggerHat() {
      if (hat != null) {
         hat.toggle();
      }
   }
   
   public Player makeShallowCopy() {
      try {
         return (Player) this.clone();
      } catch (CloneNotSupportedException e) {
         return null;
      }
   }
   
   public Player makeDeepCopy() {
      try {
         Player copy = (Player) this.clone();
         copy.abilities = this.abilities.makeCopy();
         copy.hat = this.hat.clone();
         return copy;
      } catch (CloneNotSupportedException e) {
         return null;
      }
   }
   
   public void initializeForId(int id, JPanel panel, SkinManager skinManager) throws InterruptedException {
      this.x = Client.spawn[id].x;
      this.y = Client.spawn[id].y;
      this.color = Sprite.personColors[id];
      this.panel = panel;
      this.alive = Client.alive[id];
      this.abilities = new BasicPlayer();
      this.playerElement = skinManager.createPlayer(id, "wait");
      this.color = playerElement.getPlayerColor();
      (sc = new StatusChanger(this, "wait")).start();
   }
   
   public void printPlayerDetails() {
      System.out.println("Player @ " + System.identityHashCode(this) + 
                        " | Hat @ " + System.identityHashCode(hat) +
                        " (visible: " + hat.isVisible() + ")");
   }
}

class StatusChanger extends Thread {
   Player p;
   String status;
   int index;
   boolean playerInMotion;

   StatusChanger(Player p, String initialStatus) {
      this.p = p;
      this.status = initialStatus;
      index = 0;
      playerInMotion = true;
   }
   public void run() {
      while (true) {
         p.status = status + "-" + index;
         p.updateStatus(p.status);
         if (playerInMotion) {
            index = (++index) % Sprite.maxLoopStatus.get(status);
            p.panel.repaint();
         }

         try {
            Thread.sleep(Const.RATE_PLAYER_STATUS_UPDATE);
         } catch (InterruptedException e) {}

         if (p.status.equals("dead-4")) {
            p.alive = false;
            if (Game.you == p)
               System.exit(1);
         }
      }
   }
   void setLoopStatus(String status) {
      this.status = status;
      index = 1;
      playerInMotion = true;
   }
   void stopLoopStatus() {
      playerInMotion = false;
      index = 0;
   }
}