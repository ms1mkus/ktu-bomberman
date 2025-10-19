import java.io.FileInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Properties;

class PlayerData {
   boolean logged, alive;
   int x, y; //current coordinate
   int numberOfBombs;

   PlayerAbilities abilities;

   PlayerData(int x, int y) {
      this.x = x;
      this.y = y;
      this.logged = false;
      this.alive = false;
      this.numberOfBombs = 1; // for 2 bombs, each bomb must be handled in a separate thread
      this.abilities = new BasicPlayer();
   }

   public void addGhost() {
      this.abilities = new GhostDecorator(this.abilities);
   }

   public void addBigBomb() {
      this.abilities = new BigBombDecorator(abilities);
   }

   public void addSpeedBoost() {
      this.abilities = new SpeedBoostDecorator(abilities);
   }

   public int getExplosionRange() {
      return abilities.getExplosionRange();
   }

   public int getMovementSpeed() {
      return abilities.getMovementSpeed();
   }
   
   public boolean useBigBombPowerUp() {
      if (abilities instanceof BigBombDecorator) {
         BigBombDecorator decorator = (BigBombDecorator) abilities;
         if (!decorator.isUsed()) {
            decorator.useBigBomb();
            return true;
         }
      }
      return false;
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

   private final MapType mapType;
   public MapType getMapType() { return this.mapType;}

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

      this.mapType = EnumPropertiesParser.parseEnum(properties_file,
              "map-type", MapType.class, MapType.EASY);
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
      BlockHealthManager.initializeAllBlocks();
      
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
   
   void setMap()
   {




      MapGenStrategy strategy;

      switch (config.getMapType())
      {
          case HARD -> strategy = new MapGenStrategyHard();

          // EASY
         default -> strategy = new MapGenStrategyEasy();
      }

      map = new Coordinate[Const.LIN][Const.COL];
      strategy.Generate(map);
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