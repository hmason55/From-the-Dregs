
public class Map
{
   public int width = 20;
   public int height = 20;
   private Tile[][] tiles;
   
   public Map()
   {
      tiles = new Tile[width][height];
      
      for(int y = 0; y < height; y++)
      {
         for(int x = 0; x < width; x++)
         {
            tiles[x][y] = new Tile(x, y);
         } 
      }
      
      
      Unit bat = new Unit("Bat", false);
      tiles[1][3].setUnit(bat);
      bat.moveTo(tiles[1][3]);
      
      if(tiles[0][0].getUnit() != null)
      {
         System.out.println("not null");
      }
   }
   
   public Tile[][] getTiles()
   {
      return tiles;
   }
   
   public void setTile(int x, int y, Tile t)
   {
      if(x < 0 || x > width-1 || y < 0 || y > height-1)
      {
         return;
      }
      
      tiles[x][y] = t;
   }
}
