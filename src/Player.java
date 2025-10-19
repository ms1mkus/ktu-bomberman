import java.awt.AlphaComposite;
import java.awt.Graphics;
import javax.swing.JPanel;

//for both you and enemy
public class Player {
   int x, y;
   String status, color;
   JPanel panel;
   boolean alive;

   PlayerAbilities abilities;

   StatusChanger sc;

   Player(int id, JPanel panel) throws InterruptedException {
      this.x = Client.spawn[id].x;
      this.y = Client.spawn[id].y;
      this.color = Sprite.personColors[id];
      this.panel = panel;
      this.alive = Client.alive[id];
      this.abilities = new BasicPlayer();

      (sc = new StatusChanger(this, "wait")).start();
   }

   public void draw(Graphics g) {
      if (alive) {
         // Apply transparency if player is in ghost mode (for other players)
         if (this != Game.getInstance().getYou() && isGhost()) {
            java.awt.Graphics2D g2d = (java.awt.Graphics2D) g;
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.1f));
            g.drawImage(Sprite.ht.get(color + "/" + status), x, y, Const.WIDTH_SPRITE_PLAYER, Const.HEIGHT_SPRITE_PLAYER, null);
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
         } 
         else {
            // Draw normally
            g.drawImage(Sprite.ht.get(color + "/" + status), x, y, Const.WIDTH_SPRITE_PLAYER, Const.HEIGHT_SPRITE_PLAYER, null);
         }
      }
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
         if (playerInMotion) {
            index = (++index) % Sprite.maxLoopStatus.get(status);
            p.panel.repaint();
         }

         try {
            Thread.sleep(Const.RATE_PLAYER_STATUS_UPDATE);
         } catch (InterruptedException e) {}

         if (p.status.equals("dead-4")) {
            p.alive = false;
            if (Game.getInstance().getYou() == p)
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