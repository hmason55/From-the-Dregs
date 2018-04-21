package com.hmason;

public class StatusModifier
{
   public enum Type
   {
      multSpeed, multStrength, incrementBlockStacks, incrementSpeed, incrementStrength, setBlockStacks, setSpeed, setStrength
   }

   private boolean selfEffect;
   private Unit owner;
   private Type type;
   private float value = 0;
   private int duration = 0; // -1 = Infinite, 0 = remove immediately, 1 = until end of current turn, 
                             // 2 = until end of next turn

   // Default constructor for int values
   public StatusModifier(boolean selfEffect, Type modifierType, int value, int duration)
   {
      this.selfEffect = selfEffect;
      type = modifierType;
      this.value = (float) value;
      this.duration = duration;
   }
   
   // Default constructor for float values
   public StatusModifier(boolean selfEffect, Type modifierType, float value, int duration)
   {
      this.selfEffect = selfEffect;
      type = modifierType;
      this.value = value;
      this.duration = duration;
   }

   public boolean isSelfEffect()
   {
      return selfEffect;
   }

   public Unit getOwner()
   {
      return owner;
   }

   public void setOwner(Unit owner)
   {
      this.owner = owner;
   }

   public Type getType()
   {
      return type;
   }

   public float getValue()
   {
      return value;
   }

   public void setValue(float value)
   {
      this.value = value;
   }

   public int getDuration()
   {
      return duration;
   }

   public void setDuration(int duration)
   {
      this.duration = duration;
   }

}
