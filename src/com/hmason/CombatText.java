package com.hmason;
import java.awt.Color;

public class CombatText
{

   private String text;
   private Color color;
   private float[] origin;
   private float[] position;
   private float[] speed;
   private float[] acceleration;

   private int timeIndex = 0;
   private int killIndex = 45;

   public CombatText(String displayText, int positionX, int positionY, Color c)
   {

      color = c;
      text = displayText;
      origin = new float[]
      { (float) positionX, (float) positionY };
      // position = origin;

      position = new float[]
      { (float) positionX, (float) positionY };

      speed = new float[]
      { (float) ((Math.random()*2) -1f), -1.75f };
      acceleration = new float[]
      { 0, 0.1f };

   }

   public float[] getOrigin()
   {
      return origin;
   }

   public float[] getPosition()
   {
      return position;
   }

   public void incrementTime()
   {
      if (timeIndex < killIndex)
      {
         position[0] = acceleration[0] * timeIndex + speed[0] + position[0];
         position[1] = acceleration[1] * timeIndex + speed[1] + position[1];
         timeIndex++;
      }
   }

   public String getText()
   {
      return text;
   }

   public int getTimeRemaining()
   {
      return killIndex - timeIndex;
   }
   
   public int getTimeIndex()
   {
      return timeIndex;
   }
   
   public void setColor(Color c)
   {
      color = c;
   }
   
   public Color getColor()
   {
      return color;
   }
}
