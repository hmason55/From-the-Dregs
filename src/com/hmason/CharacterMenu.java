package com.hmason;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.ImageObserver;

public class CharacterMenu implements ImageObserver
{

   private Image typeBar;
   private Image frameLeft;
   private Image frameRight;
   private Image frameCenter;

   private int x = 0;
   private int y = 0;
   private int width;
   private int height;
   private int panelSpacing = 16;
   private int panelWidth;
   private int panelHeight;

   public CharacterMenu()
   {
      panelWidth = 160;
      panelHeight = 240;
      width = panelWidth * 4 + panelSpacing * 3;
      height = panelHeight;
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

   public int getPanelWidth()
   {
      return panelWidth;
   }

   public int getPanelHeight()
   {
      return panelHeight;
   }

   public void draw(Unit unit, Graphics g, FontMetrics fm)
   {
      x = Viewport.viewWidth/2 - width/2;
      y = Viewport.viewHeight/2 - height/2;
      
      int currentX = x;
      int currentY = y;
      
      
      // Panel 1
      g.setColor(Color.GRAY);
      g.fillRect(currentX, y, panelWidth, panelHeight);
      g.setColor(Color.BLACK);
      g.drawRect(currentX, y, panelWidth, panelHeight);
      
      //Foreground
      g.setColor(Color.WHITE);
      String centeredString = "[ F1 ] to upgrade";
      g.drawString(centeredString, currentX + panelWidth/2 - fm.stringWidth(centeredString)/2, currentY+24);
      
      centeredString = "+5 Strength";
      g.drawString(centeredString, currentX + panelWidth/2 - fm.stringWidth(centeredString)/2, currentY+48);

      g.drawString("Increases the damage", currentX + 16, currentY+80);
      g.drawString("output of actions.", currentX + 16, currentY+96);
      
      centeredString = "+" + (5) + " Damage";
      g.drawString(centeredString, currentX + panelWidth/2 - fm.stringWidth(centeredString)/2, currentY+160);

      // Panel 2
      currentX = x + panelWidth * 1 + panelSpacing * 1;
      g.setColor(Color.GRAY);
      g.fillRect(currentX, y, panelWidth, panelHeight);
      g.setColor(Color.BLACK);
      g.drawRect(currentX, y, panelWidth, panelHeight);
      
      g.setColor(Color.WHITE);
      centeredString = "[ F2 ] to upgrade";
      g.drawString(centeredString, currentX + panelWidth/2 - fm.stringWidth(centeredString)/2, currentY+24);
      
      centeredString = "+5 Vitality";
      g.drawString(centeredString, currentX + panelWidth/2 - fm.stringWidth(centeredString)/2, currentY+48);

      g.drawString("Increases your", currentX + 16, currentY+80);
      g.drawString("Maximum Health and", currentX + 16, currentY+96);
      g.drawString("Health Recovery per", currentX + 16, currentY+112);
      g.drawString("turn.", currentX + 16, currentY+128);
      
      centeredString = "+" + (5*4) + " Maximum Health";
      g.drawString(centeredString, currentX + panelWidth/2 - fm.stringWidth(centeredString)/2, currentY+160);
      
      centeredString = "+" + (5/8f) + " Health Recovery";
      g.drawString(centeredString, currentX + panelWidth/2 - fm.stringWidth(centeredString)/2, currentY+176);
      
      // Panel 3
      currentX = x + panelWidth * 2 + panelSpacing * 2;
      g.setColor(Color.GRAY);
      g.fillRect(currentX, y, panelWidth, panelHeight);
      g.setColor(Color.BLACK);
      g.drawRect(currentX, y, panelWidth, panelHeight);
      
      g.setColor(Color.WHITE);
      centeredString = "[ F3 ] to upgrade";
      g.drawString(centeredString, currentX + panelWidth/2 - fm.stringWidth(centeredString)/2, currentY+24);
      
      centeredString = "+5 Fortitude";
      g.drawString(centeredString, currentX + panelWidth/2 - fm.stringWidth(centeredString)/2, currentY+48);

      g.drawString("Increases your", currentX + 16, currentY+80);
      g.drawString("Maximum Mana and", currentX + 16, currentY+96);
      g.drawString("Mana Recovery per", currentX + 16, currentY+112);
      g.drawString("turn.", currentX + 16, currentY+128);
      
      centeredString = "+" + (5*3) + " Maximum Mana";
      g.drawString(centeredString, currentX + panelWidth/2 - fm.stringWidth(centeredString)/2, currentY+160);
      
      centeredString = "+" + String.format("%.3f", (5/6f)) + " Mana Recovery";
      g.drawString(centeredString, currentX + panelWidth/2 - fm.stringWidth(centeredString)/2, currentY+176);

      // Panel 4
      currentX = x + panelWidth * 3 + panelSpacing * 3;
      g.setColor(Color.GRAY);
      g.fillRect(currentX, y, panelWidth, panelHeight);
      g.setColor(Color.BLACK);
      g.drawRect(currentX, y, panelWidth, panelHeight);
      
      g.setColor(Color.WHITE);
      centeredString = "[ F4 ] to upgrade";
      g.drawString(centeredString, currentX + panelWidth/2 - fm.stringWidth(centeredString)/2, currentY+24);
      
      centeredString = "+5 Tenacity";
      g.drawString(centeredString, currentX + panelWidth/2 - fm.stringWidth(centeredString)/2, currentY+48);

      g.drawString("Increases your", currentX + 16, currentY+80);
      g.drawString("Resistance to", currentX + 16, currentY+96);
      g.drawString("incoming damage.", currentX + 16, currentY+112);
      
      centeredString = "+" + (5) + " Mitigation";
      g.drawString(centeredString, currentX + panelWidth/2 - fm.stringWidth(centeredString)/2, currentY+160);

   }

   @Override
   public boolean imageUpdate(Image arg0, int arg1, int arg2, int arg3, int arg4, int arg5)
   {
      return false;
   }
}
