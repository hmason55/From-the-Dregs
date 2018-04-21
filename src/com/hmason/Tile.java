package com.hmason;

public class Tile
{
   
   public final static int WIDTH = 48;
   public final static int HEIGHT = 48;

   private Unit unit;
   private Terrain terrain;
   private Item item;
   private Fog fog;

   private int positionX;
   private int positionY;

   private int lightStrength;
   private boolean lightPassthrough;

   public Tile(int x, int y)
   {

      unit = null;
      item = null;
      fog = new Fog(1f);
      lightStrength = -1;
      lightPassthrough = false;

      if (y == Viewport.MAP_HEIGHT - 1 || x == 0 || x == Viewport.MAP_WIDTH - 1)
      {
         terrain = new Terrain(true, "top", this);
      } else if(y == 3 && x >= 3 && x < 6)
      {
         terrain = new Terrain(true, "top", this);
      } else if(y == 4 && x >= 3 && x < 6)
      {
         terrain = new Terrain(true, "side", this);
      } else if(y == 4 && x == 4)
      {
         terrain = new Terrain(true, "top", this);
      } else if (y == 0)
      {
         terrain = new Terrain(true, "side", this);
      } else
      {
         terrain = new Terrain(false, "floor", this);
      }

      positionX = x;
      positionY = y;
   }

   public Unit getUnit()
   {
      return unit;
   }

   public void setUnit(Unit u)
   {
      unit = u;
   }

   public Terrain getTerrain()
   {
      return terrain;
   }

   public void setTerrain(Terrain t)
   {
      terrain = t;
   }

   public Item getItem()
   {
      return item;
   }

   public void setItem(Item i)
   {
      item = i;
   }

   public Fog getFog()
   {
      return fog;
   }

   public void setFog(Fog fog)
   {
      this.fog = fog;
   }

   public int getLightStrength()
   {
      return lightStrength;
   }

   public void setLightStrength(int lightStrength)
   {
      this.lightStrength = lightStrength;
   }

   public boolean getLightPassthrough()
   {
      return lightPassthrough;
   }

   public void setLightPassthrough(boolean lightPassthrough)
   {
      this.lightPassthrough = lightPassthrough;
   }

   public int[] getPosition()
   {
      return new int[]
      { positionX, positionY };
   }

}
