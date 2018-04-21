package com.hmason;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.ImageObserver;

import javax.swing.ImageIcon;

public class ActionButton implements ImageObserver
{
   
   private Action action;
   
   private Image frame;
   private Image selectedFrame;
   private Image icon;
   private String key;

   private int x;
   private int y;
   private int width;
   private int height;
   private boolean selected;

   public ActionButton(int x, int y, String hotkey, Action action)
   {
      this.action = action;
      icon = action.getIcon();
      frame = new ImageIcon(FileLoader.loadImage("sprites/ui/hud/frame_icon.png")).getImage();
      selectedFrame = new ImageIcon(FileLoader.loadImage("sprites/ui/hud/selected_frame_icon.png")).getImage();
      key = hotkey;
      selected = false;
      this.x = x;
      this.y = y;
      width = 64;
      height = 64;
   }

   public Image getFrame()
   {
      return frame;
   }

   public Image getIcon()
   {
      return icon;
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

   public void setPosition(int x, int y)
   {
      this.x = x;
      this.y = y;
   }
   
   public void setAction(Action action)
   {
      this.action = action;
   }

   public boolean getSelected()
   {
      return selected;
   }
   
   public void setSelected(boolean selected)
   {
      this.selected = selected;
   }
   
   public Action getAction()
   {
      return action;
   }

   public void draw(Unit unit, Graphics g, FontMetrics fm)
   {
      g.drawImage(icon, x, y, this);

      if (selected)
      {
         g.drawImage(selectedFrame, x, y, this);
      } else
      {
         g.drawImage(frame, x, y, this);
      }

      int len = (fm.stringWidth(key)) / 2;

      g.setColor(Color.BLACK);
      g.drawString(key, x + width / 2 - len + 1, y + height + 12);
      g.drawString(key, x + width / 2 - len, y + height + 13);

      g.setColor(Color.WHITE);
      g.drawString(key, x + width / 2 - len, y + height + 12);
   }

   @Override
   public boolean imageUpdate(Image arg0, int arg1, int arg2, int arg3, int arg4, int arg5)
   {
      return false;
   }
}
