import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.Color;
import java.util.Arrays;
import java.util.List;

import javax.swing.JPanel;

public class Game extends JPanel {

   private static final long serialVersionUID = 1L;
   static Player you, enemy1, enemy2, enemy3;
   private static java.util.HashMap<Long, BulletData> activeBullets = new java.util.HashMap<>();
   
   private boolean mousePressed = false;
   private int mouseX, mouseY;
   private Thread shootingThread = null;

   private SkinManager skinManager;
   
   private static DrawSpriteStrategyBase drawSpriteStrategy = new DrawSpriteStrategyDefault();

   public static void setDrawSpriteStrategy(DrawSpriteStrategyBase strategy)
   {
       drawSpriteStrategy = strategy;
   }

   public static DrawSpriteStrategyBase getDrawSpriteStrategy()
   {
       return drawSpriteStrategy;
   }


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
      this.skinManager = new SkinManager();
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
         you = new Player(Client.id, this, skinManager);
         enemy1 = new Player((Client.id+1)%Const.QTY_PLAYERS, this, skinManager);
         enemy2 = new Player((Client.id+2)%Const.QTY_PLAYERS, this, skinManager);
         enemy3 = new Player((Client.id+3)%Const.QTY_PLAYERS, this, skinManager);
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

      drawSpriteStrategy.setGraphics(g);

      drawMap(g);
      drawBlockHealth(g);
      drawBullets(g);
      drawPotionEffects(g);
      enemy1.draw(g);
      enemy2.draw(g);
      enemy3.draw(g);
      you.draw(g);
      g.setColor(Color.WHITE);
      if (skinManager.isFullProtanopia()) {
         g.drawString("FULL PROTANOPIA (F1+F2)", 10, 20);
      } 
      else if (skinManager.isMapProtanopia()) {
         g.drawString("MAP PROTANOPIA (F1)", 10, 20);
      } 
      else if (skinManager.isPlayerProtanopia()) {
         g.drawString("PLAYER PROTANOPIA (F2)", 10, 20);
      } 
      else {
         g.drawString("CLASSIC MODE (F1: Map, F2: Players)", 10, 20);
      }
      
      drawSpriteStrategy.drawOverlay(getWidth(), getHeight());

      // System.out.format("%s: %s [%04d, %04d]\n", Game.you.color, Game.you.status, Game.you.x, Game.you.y);;
      Toolkit.getDefaultToolkit().sync();
   }

   void drawPlayers(Graphics g)
   {
       List<Player> players = Arrays.asList(enemy1, enemy2, enemy3, you);
       for (Player p : players)
       {
           if (p.alive) {
               // Apply transparency if player is in ghost mode (for other players)
               if (p != you && p.isGhost())
               {
                   java.awt.Graphics2D g2d = (java.awt.Graphics2D) g;
                   g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.1f));
                   drawSpriteStrategy.drawImage(Sprite.ht.get(p.color + "/" + p.status), p.x, p.y, Const.WIDTH_SPRITE_PLAYER, Const.HEIGHT_SPRITE_PLAYER);
                   g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
               }
               else
               {
                    // Draw normally
                    drawSpriteStrategy.drawImage(Sprite.ht.get(p.color + "/" + p.status), p.x, p.y, Const.WIDTH_SPRITE_PLAYER, Const.HEIGHT_SPRITE_PLAYER);
               }
           }
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
               drawSpriteStrategy.drawImage(img, v.x - v.radius, v.y - v.radius, size, size);
               continue;
            }
         }

         drawSpriteStrategy.drawOval(new java.awt.Color(v.color.getRed(), v.color.getGreen(), v.color.getBlue(), 90),
                 v.x - v.radius, v.y - v.radius, v.radius * 2, v.radius * 2);
      }
   }
   
   void drawMap(Graphics g) {
      for (int i = 0; i < Const.LIN; i++){
         for (int j = 0; j < Const.COL; j++) {
            MapElement mapElement = skinManager.createMapTile(Client.map[i][j].img);
            String spriteKey = mapElement.getSpriteKey();
                
            drawSpriteStrategy.drawImage(
               Sprite.ht.get(spriteKey), 
               Client.map[i][j].x, Client.map[i][j].y, 
               Const.SIZE_SPRITE_MAP, Const.SIZE_SPRITE_MAP
            );
         }
      }
   }
   public void toggleMapProtanopia() {
      skinManager.toggleMapProtanopia();
      repaint();
   }
    
   public void togglePlayerProtanopia() {
      skinManager.togglePlayerProtanopia();
      repaint();
   }
   
   void drawBullets(Graphics g) {
      for (BulletData bullet : activeBullets.values()) {
         if (bullet != null) {
            java.awt.Image bulletSprite = Sprite.ht.get("bullet/" + bullet.spriteType + "-1");
            
            if (bulletSprite != null) {
               int spriteSize = 32;
               int drawX = bullet.x - spriteSize / 2;
               int drawY = bullet.y - spriteSize / 2;

                drawSpriteStrategy.drawImage(bulletSprite, drawX, drawY, spriteSize, spriteSize);

            } else {

                drawSpriteStrategy.drawOval(java.awt.Color.WHITE, bullet.x - 5, bullet.y - 5, 10, 10);
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

                  drawSpriteStrategy.drawRect(java.awt.Color.RED, x, y - 10, Const.SIZE_SPRITE_MAP, barHeight);

                  java.awt.Color col;

                  if (healthPercent > 0.6) {
                      col=(java.awt.Color.GREEN);
                  } else if (healthPercent > 0.3) {
                      col=(java.awt.Color.ORANGE);
                  } else {
                      col=(java.awt.Color.RED);
                  }

                  drawSpriteStrategy.drawRect(col, x, y - 10, barWidth, barHeight);

                  drawSpriteStrategy.drawRect(java.awt.Color.BLACK, x, y - 10, Const.SIZE_SPRITE_MAP, barHeight);
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