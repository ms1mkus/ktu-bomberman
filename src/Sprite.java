import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.util.Hashtable;
import javax.imageio.ImageIO;

public class Sprite {
   final static String personColors[] = {
      "white", 
      "black", 
      "red", 
      "yellow"
   };

   final static Hashtable<String, Image> ht = new Hashtable<String, Image>();
   //not in spritesheet order
   final static String mapKeyWords[] = { 
      "background", 
      "block", 
      "block-on-fire-1", "block-on-fire-2", "block-on-fire-3", "block-on-fire-4", "block-on-fire-5", "block-on-fire-6",
      "block-white", "block-black", "block-red", "block-yellow",
      "bomb-icone-1", "bomb-icone-2", 
      "bomb-red-icone-1", "bomb-red-icone-2", 
      "bomb-planted-1", "bomb-planted-2", "bomb-planted-3", 
      "bomb-planted-red-1", "bomb-planted-red-2", "bomb-planted-red-3", 
      "center-explosion-1", "center-explosion-2", "center-explosion-3", "center-explosion-4", "center-explosion-5", 
      "down-explosion-1", "down-explosion-2", "down-explosion-3", "down-explosion-4", "down-explosion-5", 
      "right-explosion-1", "right-explosion-2", "right-explosion-3", "right-explosion-4", "right-explosion-5", 
      "up-explosion-1", "up-explosion-2", "up-explosion-3", "up-explosion-4", "up-explosion-5", 
      "floor-1", "floor-2",
      "item-destruction-1", "item-destruction-2", "item-destruction-3", "item-destruction-4", "item-destruction-5", "item-destruction-6", "item-destruction-7", 
      "left-explosion-1", "left-explosion-2", "left-explosion-3", "left-explosion-4", "left-explosion-5", 
      "mid-hori-explosion-1", "mid-hori-explosion-2", "mid-hori-explosion-3", "mid-hori-explosion-4", "mid-hori-explosion-5", 
   "mid-vert-explosion-1", "mid-vert-explosion-2", "mid-vert-explosion-3", "mid-vert-explosion-4", "mid-vert-explosion-5", 
   "wall-center", "wall-down-left", "wall-down-right", "wall-up-left", "wall-up-right", "powerup-bigbomb", "powerup-speedboost", "powerup-ghost",
   "floor-1-protan", "block-protan",
   // potion ground tiles (user-provided art)
   "potion-healing", "potion-poison", 
   // Protanonia
   "center-explosion-protan-1", "center-explosion-protan-2", "center-explosion-protan-3", "center-explosion-protan-4", "center-explosion-protan-5",
   "down-explosion-protan-1", "down-explosion-protan-2", "down-explosion-protan-3", "down-explosion-protan-4", "down-explosion-protan-5",
   "right-explosion-protan-1", "right-explosion-protan-2", "right-explosion-protan-3", "right-explosion-protan-4", "right-explosion-protan-5",
   "up-explosion-protan-1", "up-explosion-protan-2", "up-explosion-protan-3", "up-explosion-protan-4", "up-explosion-protan-5",
   "left-explosion-protan-1", "left-explosion-protan-2", "left-explosion-protan-3", "left-explosion-protan-4", "left-explosion-protan-5",
   "bomb-planted-protan-1", "bomb-planted-protan-2", "bomb-planted-protan-3", "mid-hori-explosion-protan-1", "mid-hori-explosion-protan-2",
   "mid-hori-explosion-protan-3", "mid-hori-explosion-protan-4", "mid-hori-explosion-protan-5", "mid-vert-explosion-protan-1", "mid-vert-explosion-protan-2",
   "mid-vert-explosion-protan-3", "mid-vert-explosion-protan-4", "mid-vert-explosion-protan-5"
   };
   
   final static String bulletKeyWords[] = {
      "minigun-1", "shotgun-1", "tornado-1"
   };
   
   // We'll dynamically load potion splash frames from resources/images/map/basic/
   // and store them under keys like "potion/potion-healing-1" so Game can find them.
   private static void loadPotionFrames() {
      String[] types = {"potion-healing", "potion-poison"};
      int maxFrames = 12; // upper bound; will stop when first missing frame encountered per type
      for (String type : types) {
         for (int i = 1; i <= maxFrames; i++) {
            String key = "potion/" + type + "-" + i;
            File f = new File(Const.BOMBERMAN_RESOURCES_DIR + "images/map/basic/" + type + "-" + i + ".png");
            if (!f.exists()) {
               break; // stop at first missing frame for this type
            }
            try {
               ht.put(key, ImageIO.read(f));
            } catch (IOException ignored) {
               break;
            }
         }
         // It's okay if none are loaded for one type; code will fallback to color circle
      }
   }
   //already in spritesheet order for use with autoCropAndRename.cpp
   static final String personKeyWords[] = {
      "dead-0", "dead-1", "dead-2", "dead-3", "dead-4", 
      "down-0", "down-1", "down-2", "down-3", "down-4", "down-5", "down-6", "down-7", 
      "left-0", "left-1", "left-2", "left-3", "left-4", "left-5", "left-6", "left-7", 
      "right-0", "right-1", "right-2", "right-3", "right-4", "right-5", "right-6", "right-7", 
      "uhu-0", "uhu-1", "uhu-2", "uhu-3", 
      "up-0", "up-1", "up-2", "up-3", "up-4", "up-5", "up-6", "up-7", 
      "wait-0", "wait-1", "wait-2", "wait-3", "wait-4", 
      "win-0", "win-1", "win-2", "win-3", "win-4"
   };

   static String getPersonSpriteFaceDirection(String status)
   {

      String[] tokens = status.split("-");
      if (tokens.length != 2)
      {
         throw new IllegalArgumentException("Bad status name: " + status);
      }

       return switch (tokens[0]) {
           case "left" -> "left";
           case "right" -> "right";
           case "up" -> "up";
           default -> "down";
       };

   }
   
   final static Hashtable<String, Integer> maxLoopStatus = new Hashtable<String, Integer>();
   static void setMaxLoopStatus() {
      maxLoopStatus.put("dead", 5);
      maxLoopStatus.put("down", 8);
      maxLoopStatus.put("left", 8);
      maxLoopStatus.put("right", 8);
      maxLoopStatus.put("uhu", 4);
      maxLoopStatus.put("up", 8);
      maxLoopStatus.put("wait", 5);
      maxLoopStatus.put("win", 5);
   }

   static void loadImages() {
      try {
         System.out.print("Loading images...");
         for (String keyWord : mapKeyWords)
            ht.put(keyWord, ImageIO.read(new File(Const.BOMBERMAN_RESOURCES_DIR + "images/map/basic/" + keyWord + ".png")));

         for (String color : personColors)
            for (String keyWord : personKeyWords)
               ht.put(color + "/" + keyWord, ImageIO.read(new File(Const.BOMBERMAN_RESOURCES_DIR + "images/person/" + color + "/" + keyWord + ".png")));
         
         for (String keyWord : bulletKeyWords)
            ht.put("bullet/" + keyWord, ImageIO.read(new File(Const.BOMBERMAN_RESOURCES_DIR + "images/bullet/" + keyWord + ".png")));
         
         // potion splash frames (optional; will only load if present)
         loadPotionFrames();
      } catch (IOException e) {
         System.out.print("Error!\n");
         System.exit(1);
      }
      System.out.print(" ok!\n");
   }
}