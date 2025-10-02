// accessed by server and client
interface Const {
   // if not 4, many things must not be instantiated
   final static int QTY_PLAYERS = 4; 

   final static int LIN = 9, COL = 9; // always odd
   final static int RESIZE = 4; // pixel size

   final static int SIZE_SPRITE_MAP = 16 * RESIZE;
   final static int WIDTH_SPRITE_PLAYER = 22 * RESIZE;
   final static int HEIGHT_SPRITE_PLAYER = 33 * RESIZE;

   // pixel difference between map sprite and player sprite
   final static int VAR_X_SPRITES = 3 * RESIZE;
   final static int VAR_Y_SPRITES = 16 * RESIZE;

   final static int RATE_BOMB_UPDATE = 90;
   final static int RATE_BLOCK_UPDATE = 100;
   final static int RATE_FIRE_UPDATE = 35;
   final static int RATE_PLAYER_STATUS_UPDATE = 90;
   final static int RATE_COORDINATES_UPDATE = 27;

   final static String indexBombPlanted[] = {
      "1", "2", "3", "2", "1", "2", "3", "2", "1", "2", "3", "2", "1", "2", 
      "red-3", "red-2", "red-1", "red-2", "red-3", "red-2", "red-3", "red-2", "red-3", "red-2", "red-3"
   };
   final static String indexExplosion[] = {
      "1", "2", "3", "4", "5", "4", "3", "4", "5", "4", "3", "4", "5", "4", "3", "4", "5", "4", "3", "2", "1"
   };
   final static String indexBlockOnFire[] = {
      "1", "2", "1", "2", "1", "2", "3", "4", "5", "6"
   };

   final static String BOMBERMAN_RESOURCES_DIR = System.getProperty("user.dir") + "/resources/";
}

class Coordinate {
   public int x, y;
   String img;

   Coordinate(int x, int y) {
      this.x = x;
      this.y = y;
   }

   Coordinate(int x, int y, String img) {
      this.x = x;
      this.y = y;
      this.img = img;
   }
}