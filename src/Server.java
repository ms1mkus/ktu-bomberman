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
   private int coveredBombLine = -1;
   private int coveredBombCol = -1;
   private boolean hasBombCover = false;

   private PlayerState currentState;
   private long lastUpdateTime;

   PlayerData(int x, int y) {
      this.x = x;
      this.y = y;
      this.logged = false;
      this.alive = false;
      this.numberOfBombs = 1; // for 2 bombs, each bomb must be handled in a separate thread
      this.abilities = new BasicPlayer();

      this.currentState = new NormalState();
      this.lastUpdateTime = System.currentTimeMillis();
   }

   public void updateState(int playerId) {
      long currentTime = System.currentTimeMillis();
      long deltaTime = currentTime - lastUpdateTime;
      lastUpdateTime = currentTime;
        
      PlayerState newState = currentState.update(deltaTime);
      if (newState != currentState) {
         currentState = newState;
         ClientManager.sendToAllClients(playerId + " stateChange " + getAnimationType());
      }
   }
    
   public void setMoving(boolean moving, int playerId) {
      if (moving) {
         PlayerState newState = currentState.onMovementInput();
         if (newState != currentState) {
            currentState = newState;
            ClientManager.sendToAllClients(playerId + " stateChange " + getAnimationType());
         }
      }
   }
    
   public void handleActionInput(int playerId) {
      PlayerState newState = currentState.onActionInput();
      if (newState != currentState) {
         currentState = newState;
         ClientManager.sendToAllClients(playerId + " stateChange " + getAnimationType());
      }
   }
    
   public void setState(PlayerState newState, int playerId) {
      if (newState != currentState) {
         currentState = newState;
         ClientManager.sendToAllClients(playerId + " stateChange " + getAnimationType());
      }
   }
    
   public int getMovementSpeed() {
      return abilities.getMovementSpeed();
   }
    
   public boolean canPlantBomb() {
      return currentState.canPlantBomb() && numberOfBombs > 0;
   }
    
   public boolean canMove() {
      return currentState.canMove();
   }
    
   public String getAnimationType() {
      String anim = currentState.getAnimationType();
      if (anim.equals("run") && !Sprite.maxLoopStatus.containsKey("run")) {
         return "down"; // Use walking animation instead
      }
      return anim;
   }

   public void setBombCover(int line, int col) {
      coveredBombLine = line;
      coveredBombCol = col;
      hasBombCover = true;
   }

   public void removeBombCover() {
      coveredBombLine = -1;
      coveredBombCol = -1;
      hasBombCover = false;
   }

   public boolean isBombCovered(int line, int col) {
      boolean result = hasBombCover && coveredBombLine == line && coveredBombCol == col;
      return result;
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
   private ServerConfig config = null;
   static PlayerData[] player = null;
   static Coordinate[][] map = null;

   
   Server(int portNumber) {

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

   boolean loggedIsFull() {
      for (int i = 0; i < Const.QTY_PLAYERS; i++)
         if (player[i].logged == false)
            return false;
      return true;
   }
   
   void setMap()
   {




      MapGeneratorTemplate strategy;

      switch (config.getMapType())
      {
          case HARD -> strategy = new MapGeneratorHard();
          case BARREN -> strategy = new MapGeneratorBarren();
          case  IMPOSSIBLE -> strategy = new MapGeneratorImpossible();


          // EASY
         default -> strategy = new MapGeneratorEasy();
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
      new Server(8383);
   }
}