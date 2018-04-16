package com.hmason;

public class Map
{
   public int width = 20;
   public int height = 20;
   private Tile[][] tiles;

   public Map()
   {
      tiles = new Tile[width][height];

      for (int y = 0; y < height; y++)
      {
         for (int x = 0; x < width; x++)
         {
            tiles[x][y] = new Tile(x, y);
         }
      }

      if (tiles[0][0].getUnit() != null)
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
      if (x < 0 || x > width - 1 || y < 0 || y > height - 1)
      {
         return;
      }

      tiles[x][y] = t;
   }

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
      }

      // Fill other four directions
      floodFill(x - 1, y, visited, n, m, originX, originY, lightStrength);
      floodFill(x, y - 1, visited, n, m, originX, originY, lightStrength);
      floodFill(x + 1, y, visited, n, m, originX, originY, lightStrength);
      floodFill(x, y + 1, visited, n, m, originX, originY, lightStrength);
   }
}
