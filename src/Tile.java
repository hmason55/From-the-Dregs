import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.ImageObserver;
import java.awt.image.ImageProducer;

public class Tile extends Image
{

   public final static int WIDTH = 48;
   public final static int HEIGHT = 48;
   
   private Unit unit;
   private Terrain terrain;
   private Item item;
   
   private int positionX;
   private int positionY;
   
   public Tile(int x, int y)
   {
      unit = null;
      if(y == 19 || x == 0 || x == 19)
      {
         terrain = new Terrain(true, "top");
      } else if(y == 0)
      {
         terrain = new Terrain(true, "side");
      } else {
         terrain = new Terrain(false, "floor");
      }
      item = null;
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
   
   public int[] getPosition()
   {
      return new int[] {positionX, positionY};
   }
   
   @Override
   public Graphics getGraphics()
   {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   public int getHeight(ImageObserver arg0)
   {
      // TODO Auto-generated method stub
      return 0;
   }

   @Override
   public Object getProperty(String arg0, ImageObserver arg1)
   {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   public ImageProducer getSource()
   {
      // TODO Auto-generated method stub
      return null;
   }

   @Override
   public int getWidth(ImageObserver arg0)
   {
      // TODO Auto-generated method stub
      return 0;
   }

}
