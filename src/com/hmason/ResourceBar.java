package com.hmason;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.ImageObserver;

import javax.swing.ImageIcon;

public class ResourceBar implements ImageObserver
{

   private Image typeBar;
   private Image frameLeft;
   private Image frameRight;
   private Image frameCenter;

   private int x;
   private int y;
   private int width;
   private int height;

   public enum ResourceType
   {
      Health, Resource, Experience
   }

   private ResourceType type;

   public ResourceBar(int x, int y, ResourceType type)
   {
      this.type = type;
      this.x = x;
      this.y = y;
      width = 256;
      height = 16;

      switch (type)
      {
      case Health:
         typeBar = new ImageIcon(FileLoader.loadImage("sprites/ui/hud/resource_health_bar.png")).getImage();
         break;
      case Resource:
         typeBar = new ImageIcon(FileLoader.loadImage("sprites/ui/hud/resource_resource_bar.png")).getImage();
         break;
      case Experience:
         typeBar = new ImageIcon(FileLoader.loadImage("sprites/ui/hud/resource_exp_bar.png")).getImage();
         break;
      }
      frameLeft = new ImageIcon(FileLoader.loadImage("sprites/ui/hud/resource_frame_left.png")).getImage();
      frameRight = new ImageIcon(FileLoader.loadImage("sprites/ui/hud/resource_frame_right.png")).getImage();
      frameCenter = new ImageIcon(FileLoader.loadImage("sprites/ui/hud/resource_frame_center.png")).getImage();

   }

   public Image getTypeBar()
   {
      return typeBar;
   }

   public Image getFrameLeft()
   {
      return frameLeft;
   }

   public Image getFrameRight()
   {
      return frameRight;
   }

   public Image getFrameCenter()
   {
      return frameCenter;
   }

   public int[] getPosition()
   {
      return new int[]
      { x, y };
   }

   public int getWidth()
   {
      return width;
   }

   public int getHeight()
   {
      return height;
   }

   public void draw(Unit unit, Graphics g, FontMetrics fm)
   {
      
      // Draw Fill
      switch (type)
      {
      case Health:
         g.drawImage(typeBar, x + 14, y, (int) (unit.getHealthPercentage() * (width + 4)), height, this);
         break;
      case Resource:
         g.drawImage(typeBar, x + 14, y, (int) (unit.getResourcePercentage() * (width + 4)), height, this);
         break;
      case Experience:
         g.drawImage(typeBar, x + 14, y, (int) (unit.getExpPercentage() * (width + 4)), height, this);
         break;
      }
      

      // Draw Frame
      g.drawImage(frameLeft, x, y, 16, height, this);
      g.drawImage(frameCenter, x + 16, y, width, height, this);
      g.drawImage(frameRight, x + width + 16, y, 16, height, this);
      
      // Draw Text
      g.setFont(new Font("Arial", Font.BOLD, 12));
      String str = "";
      switch (type)
      {
      case Health:
         str = unit.getCurrentHealth() + " / " + unit.getMaxHealth();
         break;
      case Resource:
         str = unit.getCurrentResource() + " / " + unit.getMaxResource();
         break;
      case Experience:
         

         str = "Lv. " + unit.getLevel() + "   " + (String.format("%.2f", unit.getExpPercentage() * 100))
               + "%";
         break;
      }
      int len = (fm.stringWidth(str)) / 2;
      g.setColor(Color.BLACK);
      g.drawString(str, x + width / 2 - len + 17, y + height - 4);
      g.drawString(str, x + width / 2 - len + 16, y + height - 3);

      g.setColor(Color.WHITE);
      g.drawString(str, x + width / 2 - len + 16, y + height - 4);

   }

   @Override
   public boolean imageUpdate(Image arg0, int arg1, int arg2, int arg3, int arg4, int arg5)
   {
      // TODO Auto-generated method stub
      return false;
   }
}
