import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JPanel;

public class Game extends JPanel {

   private static final long serialVersionUID = 1L;
   static Player you, enemy1, enemy2, enemy3;
   private static java.util.HashMap<Long, BulletData> activeBullets = new java.util.HashMap<>();
   
   private boolean mousePressed = false;
   private int mouseX, mouseY;
   private Thread shootingThread = null;
   private static java.util.LinkedList<String> chatLog = new java.util.LinkedList<>();
   
   private static class BulletData {
      int x, y;
      String spriteType;
      
      BulletData(int x, int y, String spriteType) {
         this.x = x; this.y = y; 
         this.spriteType = spriteType;
      }
   }

   private static class PotionEffectViz {
      int x, y, radius;
      long startTime;
      long endTime;
      int durationMs;
      java.awt.Color color;
      String type; // HEALING or POISON
      PotionEffectViz(int x, int y, int radius, int durationMs, java.awt.Color color) {
         this.x = x; this.y = y; this.radius = radius;
         this.durationMs = Math.max(1, durationMs);
         this.startTime = System.currentTimeMillis();
         this.endTime = this.startTime + this.durationMs;
         this.color = color;
      }
      boolean isActive() { return System.currentTimeMillis() < endTime; }
   }
   private static java.util.List<PotionEffectViz> effectVizes = new java.util.ArrayList<>();

   Game(int width, int height) {
      setPreferredSize(new Dimension(width, height));
      
      addMouseListener(new MouseAdapter() {
         @Override
         public void mousePressed(MouseEvent e) {
            if (you.alive && Client.out != null) {
               mousePressed = true;
               mouseX = e.getX();
               mouseY = e.getY();
               if (e.getButton() == MouseEvent.BUTTON2) { // middle click throws potion once
                  Client.out.println("throw_potion " + mouseX + " " + mouseY);
               } else {
                  Client.out.println("shoot " + mouseX + " " + mouseY);
                  startContinuousShooting();
               }
            }
         }
         
         @Override
         public void mouseReleased(MouseEvent e) {
            mousePressed = false;
            stopContinuousShooting();
         }
      });
      
      addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
         @Override
         public void mouseDragged(MouseEvent e) {
            if (mousePressed) {
               mouseX = e.getX();
               mouseY = e.getY();
            }
         }
      });
      
      try {
         System.out.print("Initializing players...");
         you = new Player(Client.id, this);
         enemy1 = new Player((Client.id+1)%Const.QTY_PLAYERS, this);
         enemy2 = new Player((Client.id+2)%Const.QTY_PLAYERS, this);
         enemy3 = new Player((Client.id+3)%Const.QTY_PLAYERS, this);
      } catch (InterruptedException e) {
         System.out.println("Error: " + e + "\n");
         System.exit(1);
      }
      System.out.print(" ok\n");

      System.out.println("My player: " + Sprite.personColors[Client.id]);
   }

   //draws components, called by paint() and repaint()
   public void paintComponent(Graphics g) {
      super.paintComponent(g);
      drawMap(g);
      drawBlockHealth(g);
      drawBullets(g);
      drawPotionEffects(g);
      enemy1.draw(g);
      enemy2.draw(g);
      enemy3.draw(g);
      you.draw(g);
      drawChat(g);
      
      // System.out.format("%s: %s [%04d, %04d]\n", Game.you.color, Game.you.status, Game.you.x, Game.you.y);;
      Toolkit.getDefaultToolkit().sync();
   }

   private void drawChat(Graphics g) {
      int lines = Math.min(chatLog.size(), 8);
      if (lines == 0) return;
      int x = 10;
      int y = getHeight() - 10 - lines * 16;
      java.awt.Graphics2D g2 = (java.awt.Graphics2D) g;
      g2.setColor(new java.awt.Color(0,0,0,120));
      g2.fillRoundRect(x - 6, y - 6, 520, lines * 16 + 12, 8, 8);
      g2.setColor(java.awt.Color.WHITE);
      int i = 0;
      for (String msg : chatLog.subList(chatLog.size() - lines, chatLog.size())) {
         g2.drawString(msg, x, y + i * 16);
         i++;
      }
   }

   void drawPotionEffects(Graphics g) {
      java.util.Iterator<PotionEffectViz> it = effectVizes.iterator();
      while (it.hasNext()) {
         PotionEffectViz v = it.next();
         if (!v.isActive()) { it.remove(); continue; }
         // Try to draw animated splash frames if present; otherwise fallback to colored circle
         String base = v.type != null && v.type.equals("HEALING") ? "potion/potion-healing-" : "potion/potion-poison-";
         int frames = 0;
         // find frame count by probing existing keys once (cache omitted for simplicity)
         for (int i = 1; i <= 12; i++) {
            if (Sprite.ht.get(base + i) != null) frames++; else break;
         }
         if (frames > 0) {
            long now = System.currentTimeMillis();
            long elapsed = Math.max(0, now - v.startTime);
            double fraction = Math.max(0.0, Math.min(1.0, (double) elapsed / (double) v.durationMs));
            int frame = (int)Math.floor(fraction * frames);
            frame = Math.max(0, Math.min(frames - 1, frame)) + 1;
            java.awt.Image img = Sprite.ht.get(base + frame);
            if (img != null) {
               int size = v.radius * 2;
               g.drawImage(img, v.x - v.radius, v.y - v.radius, size, size, null);
               continue;
            }
         }
         java.awt.Graphics2D g2d = (java.awt.Graphics2D) g;
         g2d.setColor(new java.awt.Color(v.color.getRed(), v.color.getGreen(), v.color.getBlue(), 90));
         g2d.fillOval(v.x - v.radius, v.y - v.radius, v.radius * 2, v.radius * 2);
      }
   }
   
   void drawMap(Graphics g) {
      for (int i = 0; i < Const.LIN; i++)
         for (int j = 0; j < Const.COL; j++)
            g.drawImage(
               Sprite.ht.get(Client.map[i][j].img), 
               Client.map[i][j].x, Client.map[i][j].y, 
               Const.SIZE_SPRITE_MAP, Const.SIZE_SPRITE_MAP, null
            );
   }
   
   void drawBullets(Graphics g) {
      for (BulletData bullet : activeBullets.values()) {
         if (bullet != null) {
            java.awt.Image bulletSprite = Sprite.ht.get("bullet/" + bullet.spriteType + "-1");
            
            if (bulletSprite != null) {
               int spriteSize = 32;
               int drawX = bullet.x - spriteSize / 2;
               int drawY = bullet.y - spriteSize / 2;
               
               g.drawImage(bulletSprite, drawX, drawY, spriteSize, spriteSize, null);
            } else {
               g.setColor(java.awt.Color.WHITE);
               g.fillOval(bullet.x - 5, bullet.y - 5, 10, 10);
            }
         }
      }
   }
   
   void drawBlockHealth(Graphics g) {
      for (int i = 0; i < Const.LIN; i++) {
         for (int j = 0; j < Const.COL; j++) {
            if (Client.map[i][j].img.equals("block")) {
               String key = j + "," + i;
               int health = BlockHealthManager.getHealth(key);
               
               if (health < 1000) {
                  double healthPercent = (double) health / 1000;
                  int x = j * Const.SIZE_SPRITE_MAP;
                  int y = i * Const.SIZE_SPRITE_MAP;
                  
                  int barWidth = (int)(Const.SIZE_SPRITE_MAP * healthPercent);
                  int barHeight = 6;
                  
                  g.setColor(java.awt.Color.RED);
                  g.fillRect(x, y - 10, Const.SIZE_SPRITE_MAP, barHeight);
                  
                  if (healthPercent > 0.6) {
                     g.setColor(java.awt.Color.GREEN);
                  } else if (healthPercent > 0.3) {
                     g.setColor(java.awt.Color.ORANGE);
                  } else {
                     g.setColor(java.awt.Color.RED);
                  }
                  g.fillRect(x, y - 10, barWidth, barHeight);
                  
                  g.setColor(java.awt.Color.BLACK);
                  g.drawRect(x, y - 10, Const.SIZE_SPRITE_MAP, barHeight);
               }
            }
         }
      }
   }
   
   static void handleBulletUpdate(String bulletData) {
      String[] parts = bulletData.split(" ");
      String action = parts[0];
      
      if (action.equals("create")) {
         long bulletId = Long.parseLong(parts[1]);
         int x = Integer.parseInt(parts[2]);
         int y = Integer.parseInt(parts[3]);
         /* int direction = */ Integer.parseInt(parts[4]); // ignore direction for rendering
         String spriteType = parts[5];
         
         activeBullets.put(bulletId, new BulletData(x, y, spriteType));
      } else if (action.equals("move")) {
         long bulletId = Long.parseLong(parts[1]);
         int x = Integer.parseInt(parts[2]);
         int y = Integer.parseInt(parts[3]);
         
         BulletData bullet = activeBullets.get(bulletId);
         if (bullet != null) {
            bullet.x = x;
            bullet.y = y;
         }
      } else if (action.equals("destroy")) {
         long bulletId = Long.parseLong(parts[1]);
         activeBullets.remove(bulletId);
      }
   }

   static void handlePotionEffect(String type, int x, int y, int radius, int duration) {
      java.awt.Color color = type.equals("HEALING") ? new java.awt.Color(50,205,50) : new java.awt.Color(148,0,211);
      PotionEffectViz v = new PotionEffectViz(x, y, radius, duration, color);
      v.type = type;
      effectVizes.add(v);
   }

   static void handleChat(int senderId, String message) {
      String who = (senderId >= 0 && senderId < Const.QTY_PLAYERS)
         ? ("[" + Sprite.personColors[senderId] + "]")
         : "[sys]";
      String line = who + " " + message;
      chatLog.add(line);
      while (chatLog.size() > 50) chatLog.removeFirst();
   }
   
   static void handleBlockHealth(String blockKey, int health) {
      BlockHealthManager.setHealth(blockKey, health);
   }

   static void setSpriteMap(String keyWord, int l, int c) {
      Client.map[l][c].img = keyWord;
   }
   
   private void startContinuousShooting() {
      stopContinuousShooting();
      
      shootingThread = new Thread(() -> {
         try {
            Thread.sleep(200);
            
            while (mousePressed && you.alive && Client.out != null) {
               Client.out.println("shoot " + mouseX + " " + mouseY);
               
               Thread.sleep(200);
            }
         } catch (InterruptedException e) {
         }
      });
      
      shootingThread.setDaemon(true);
      shootingThread.start();
   }
   
   private void stopContinuousShooting() {
      if (shootingThread != null && shootingThread.isAlive()) {
         shootingThread.interrupt();
         shootingThread = null;
      }
   }
}