package com.hmason;

public class Turn
{
   Unit unit;
   int priority = 0;
   
   public Turn(Unit unit, int priority)
   {
      this.unit = unit;
      this.priority = priority;
   }
   
   public Unit getUnit()
   {
      return unit;
   }
   
   public void setUnit(Unit unit)
   {
      this.unit = unit;
   }
   
   public int getPriority()
   {
      return priority;
   }
   
   public void setPriority(int priority)
   {
      this.priority = priority;
   }
   
   public void increasePriority()
   {
      priority++;
   }
   
   public void decreasePriority()
   {
      priority--;
   }
   
   public String toString()
   {
      return (unit.toString() + ", " + priority);
   }
}
