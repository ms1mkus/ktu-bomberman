import java.io.FileInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Properties;
import java.util.logging.Level;

class PlayerData {
   boolean logged, alive;
   int x, y; //current coordinate
   int numberOfBombs;

   PlayerData(int x, int y) {
      this.x = x;
      this.y = y;
      this.logged = false;
      this.alive = false;
   this.numberOfBombs = 1; // for 2 bombs, each bomb must be handled in a separate thread
   }
}

class ServerConfig
{

   private static class EnumPropertiesParser
   {
      public static <T extends Enum<T>> T parseEnum(
              Properties properties_file,
              String key,
              Class<T> enumClass,
              T defaultValue)
      {

         String value = properties_file.getProperty(key);
         if (value == null)
         {
            return defaultValue;
         }

         try
         {
            return Enum.valueOf(enumClass, value.toUpperCase());
         }
         catch (IllegalArgumentException e)
         {
            System.out.println("Error: Invalid value '" + value + "' for " + enumClass.getSimpleName() + ". Using default: " + defaultValue);
            return defaultValue;
         }
      }
   }

   private LevelType levelType;
   public LevelType getLevelType() { return this.levelType;}

   public ServerConfig()
   {
      Properties properties_file = new Properties();
      try (FileInputStream fis = new FileInputStream(Const.BOMBERMAN_RESOURCES_DIR + "server.properties"))
      {
         properties_file.load(fis);
      }
      catch (IOException e)
      {
         System.out.println("Error: Couldn't load server properties file: " + e.getMessage());
      }

      this.levelType = EnumPropertiesParser.parseEnum(properties_file,
              "level-type", LevelType.class, LevelType.SMALL);
   }


}

class Server {
   private static Server instance = null;
   private ServerConfig config = null;
   static PlayerData[] player = null;
   static Coordinate[][] map = null;

   
   private Server(int portNumber) {

      this.config = new ServerConfig();

      ServerSocket ss;

      setMap();
      setPlayerData();
      
      try {
         System.out.print("Opening port " + portNumber + "...");
         ss = new ServerSocket(portNumber); // socket listens to the port
         System.out.print(" ok\n");

         for (int id = 0; !loggedIsFull(); id = (++id)%Const.QTY_PLAYERS)
            if (!player[id].logged) {
               Socket clientSocket = ss.accept();
               new ClientManager(clientSocket, id).start();
            }
         //do not close the server while client threads are still running
      } catch (IOException e) {
         System.out.println("Error: " + e + "\n");
         System.exit(1);
      }
   }
   // Get single instance of Server
   public static Server getInstance(int portNumber) {
      if (instance == null){
         instance = new Server(portNumber);
      }
      return instance;
   }

   boolean loggedIsFull() {
      for (int i = 0; i < Const.QTY_PLAYERS; i++)
         if (player[i].logged == false)
            return false;
      return true;
   }
   
   void setMap() {

      map = new Coordinate[Const.LIN][Const.COL];

      for (int i = 0; i < Const.LIN; i++)
         for (int j = 0; j < Const.COL; j++)
            map[i][j] = new Coordinate(Const.SIZE_SPRITE_MAP * j, Const.SIZE_SPRITE_MAP * i, "block");

   // fixed border walls
      for (int j = 1; j < Const.COL - 1; j++) {
         map[0][j].img = "wall-center";
         map[Const.LIN - 1][j].img = "wall-center";
      }
      for (int i = 1; i < Const.LIN - 1; i++) {
         map[i][0].img = "wall-center";
         map[i][Const.COL - 1].img = "wall-center";
      }
      map[0][0].img = "wall-up-left";
      map[0][Const.COL - 1].img = "wall-up-right";
      map[Const.LIN - 1][0].img = "wall-down-left";
      map[Const.LIN - 1][Const.COL - 1].img = "wall-down-right";

   // fixed central walls
      for (int i = 2; i < Const.LIN - 2; i++)
         for (int j = 2; j < Const.COL - 2; j++)
            if (i % 2 == 0 && j % 2 == 0)
               map[i][j].img = "wall-center";

   // spawn surroundings
      map[1][1].img = "floor-1";
      map[1][2].img = "floor-1";
      map[2][1].img = "floor-1";
      map[Const.LIN - 2][Const.COL - 2].img = "floor-1";
      map[Const.LIN - 3][Const.COL - 2].img = "floor-1";
      map[Const.LIN - 2][Const.COL - 3].img = "floor-1";
      map[Const.LIN - 2][1].img = "floor-1";
      map[Const.LIN - 3][1].img = "floor-1";
      map[Const.LIN - 2][2].img = "floor-1";
      map[1][Const.COL - 2].img = "floor-1";
      map[2][Const.COL - 2].img = "floor-1";
      map[1][Const.COL - 3].img = "floor-1";
   }
   
   void setPlayerData() {

      player = new PlayerData[Const.QTY_PLAYERS];

      player[0] = new PlayerData(
         map[1][1].x - Const.VAR_X_SPRITES, 
         map[1][1].y - Const.VAR_Y_SPRITES
      );

      player[1] = new PlayerData(
         map[Const.LIN - 2][Const.COL - 2].x - Const.VAR_X_SPRITES,   
         map[Const.LIN - 2][Const.COL - 2].y - Const.VAR_Y_SPRITES
      );
      player[2] = new PlayerData(
         map[Const.LIN - 2][1].x - Const.VAR_X_SPRITES,   
         map[Const.LIN - 2][1].y - Const.VAR_Y_SPRITES
      );
      player[3] = new PlayerData(
         map[1][Const.COL - 2].x - Const.VAR_X_SPRITES,   
         map[1][Const.COL - 2].y - Const.VAR_Y_SPRITES
      );
   }

   public static void main(String[] args) {
      Server.getInstance(8383);
   }
}