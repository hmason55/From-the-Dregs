package com.hmason;

public class Map
{
   private int width;
   private int height;
   private Tile[][] tiles;

   // Default constructor
   public Map(int width, int height)
   {
      this.width = width;
      this.height = height;
      tiles = new Tile[width][height];

      for (int y = 0; y < height; y++)
      {
         for (int x = 0; x < width; x++)
         {
            tiles[x][y] = new Tile(x, y);
         }
      }
   }

   public Tile[][] getTiles()
   {
      return tiles;
   }
   
   public int getWidth()
   {
      return width;
   }
   
   public int getHeight()
   {
      return height;
   }

   public void setTile(int x, int y, Tile t)
   {
      if (x < 0 || x > width - 1 || y < 0 || y > height - 1)
      {
         return;
      }

      tiles[x][y] = t;
   }

   // Spawns a unit u at position x, y
   public void spawnUnit(Unit u, int x, int y)
   {
      if (tiles[x][y].getUnit() == null)
      {
         tiles[x][y].setUnit(u);
         u.moveTo(tiles[x][y]);
      } else
      {
         System.out.println("A unit already exists at " + x + ", " + y);
      }
   }
   
   // Spawns an enemy at a random position
   public Unit spawnEnemy(int level)
   {
      int maxAttempts = 30;
      int attempts = 0;
      int x = 1;
      int y = 1;
      Unit unit = null;
      while(attempts < maxAttempts)
      {
         x = (int)(Math.random()*(width-2)) + 1;
         y = (int)(Math.random()*(height-2)) + 1;
         
         if(tiles[x][y].getUnit() == null && tiles[x][y].getTerrain().isWalkable())
         {
            int min = 1;
            int max = 6;
            Unit.Type type = Unit.Type.values()[(int) ((Math.random()*(max-min)+min))];

            String name = type.toString();
            unit = new Unit(name, false, type, level);
            tiles[x][y].setUnit(unit);
            unit.moveTo(tiles[x][y]);
            attempts = maxAttempts;
         } else {
            attempts++;
         }
      }
      
      System.out.println(x + ", " + y);
      return unit;
   }

   // Updates the fog using floodFill
   public void repaintFog()
   {
      // Reset fog
      for (int y = 0; y < height; y++)
      {
         for (int x = 0; x < width; x++)
         {
            tiles[x][y].getFog().setDensity(1f);
         }
      }

      // Show lights
      for (int y = 0; y < height; y++)
      {
         for (int x = 0; x < width; x++)
         {
            Tile tile = tiles[x][y];
            if (tile.getLightStrength() > -1)
            {
               int str = tile.getLightStrength();
               floodFill(x, y, new boolean[x + str + 1][y + str + 1], x + str + 1, y + str + 1, x, y, str);
            }
         }
      }
   }

   // Uses recursion to 4-way fill a 2D grid
   private void floodFill(int x, int y, boolean[][] visited, int n, int m, int originX, int originY, int lightStrength)
   {
      if (x < 0 || y < 0)
      {
         return;
      }

      if (x >= n || y >= m)
      {
         return;
      }

      if (visited[x][y] == true)
      {
         return;
      }

      visited[x][y] = true;

      if (x < originX - lightStrength || y < originY - lightStrength)
      {
         return;
      }

      int absDistance = Math.abs(originX - x) + Math.abs(originY - y);

      if (absDistance > lightStrength)
      {
         return;
      }

      // Check for collision
      float density = 1f;
      if (tiles[x][y] != null)
      {
         Fog fog = tiles[x][y].getFog();
         density = ((float) absDistance - (lightStrength * 0.25f)) / ((float) lightStrength);

         // Clamp fog density
         if (density > 1f)
         {
            density = 1f;
         } else if (density < 0f)
         {
            density = 0f;
         }

         fog.setDensity(density);
         if (!tiles[x][y].getLightPassthrough())
         {
            return;
         }
      } else {
         return;
      }

      // Fill other four directions
      floodFill(x - 1, y, visited, n, m, originX, originY, lightStrength);
      floodFill(x, y - 1, visited, n, m, originX, originY, lightStrength);
      floodFill(x + 1, y, visited, n, m, originX, originY, lightStrength);
      floodFill(x, y + 1, visited, n, m, originX, originY, lightStrength);
   }
}
