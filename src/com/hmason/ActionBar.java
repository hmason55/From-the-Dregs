package com.hmason;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.ImageObserver;
import java.util.ArrayList;

public class ActionBar implements ImageObserver
{

   private int x;
   private int y;
   private int width;
   private int height;
   private int columns;

   private int buttonWidth;
   private int buttonHeight;

   private int paddingHorizontal;
   private int paddingVertical;

   private ArrayList<ActionButton> actionButtons;
   private int selectedIndex;

   public ActionBar(int x, int y, int columns)
   {
      this.columns = columns;
      actionButtons = new ArrayList<ActionButton>();
      selectedIndex = 0;
      buttonWidth = 64;
      buttonHeight = 64;
      this.x = x;
      this.y = y;
      paddingHorizontal = 16;
      paddingVertical = 16;
   }

   public void add(ActionButton actionButton)
   {
      actionButtons.add(actionButton);
      calcWidth();
      calcHeight();
   }

   public void remove(int index)
   {
      actionButtons.remove(index);
      calcWidth();
      calcHeight();
   }
   
   public void selectAction(int index)
   {
      selectedIndex = index;
   }

   private void calcWidth()
   {
      width = paddingHorizontal * (actionButtons.size() + 1) + (actionButtons.size()) * buttonWidth;
   }

   private void calcHeight()
   {
      height = paddingVertical * 2 + ((actionButtons.size() - 1) / 4 + 1) * buttonHeight;
   }

   public void setPosition(int x, int y)
   {
      this.x = x;
      this.y = y;
   }

   public int[] getPosition()
   {
      return new int[]
      { x, y };
   }
   
   public Action getSelectedAction()
   {
      for(ActionButton actionButton : actionButtons)
      {
         if(actionButton.getSelected())
         {
            return actionButton.getAction();
         }
      }
      return null;
   }

   public void draw(Unit unit, Graphics g, FontMetrics fm)
   {
      g.setColor(Color.GRAY);
      g.drawRect(x, y, width, height);

      //g.setColor(Color.YELLOW);
      //g.drawRect(x + selectedIndex + paddingHorizontal/2, y, buttonWidth + paddingHorizontal, buttonHeight + paddingVertical);
      int rx = x + paddingHorizontal;
      int ry = y + paddingVertical;
      for (int i = 0; i < actionButtons.size(); i++)
      {

         rx = x + i * buttonWidth + i * paddingHorizontal + paddingHorizontal;

         ActionButton actionButton = actionButtons.get(i);
         if(i == selectedIndex)
         {
            actionButtons.get(i).setSelected(true);
         } else {
            actionButtons.get(i).setSelected(false);
         }
            
         actionButton.setPosition(rx, ry);
         actionButton.draw(unit, g, fm);
      }

   }

   @Override
   public boolean imageUpdate(Image arg0, int arg1, int arg2, int arg3, int arg4, int arg5)
   {
      // TODO Auto-generated method stub
      return false;
   }
}
