package com.hmason;

import java.awt.Image;
import java.util.ArrayList;

import javax.swing.ImageIcon;

public class Action
{

   private Image icon;

   // On activation
   private float strengthScaling = 0f;

   ArrayList<StatusModifier> modifiers;

   public enum Moveset
   {
      Forward_Slash, Crushing_Swing, Defensive_Stance, Bite
   }

   public Action(Moveset action)
   {
      modifiers = new ArrayList<StatusModifier>();
      
      switch (action)
      {
      case Forward_Slash:
         strengthScaling = 1f;
         icon = new ImageIcon(FileLoader.loadImage("sprites/ui/hud/sword_icon.png")).getImage();
         break;

      case Crushing_Swing:
         strengthScaling = 2.5f;
         modifiers.add(new StatusModifier(true, StatusModifier.Type.incrementSpeed, -2, 1));
         icon = new ImageIcon(FileLoader.loadImage("sprites/ui/hud/sword_icon.png")).getImage();
         break;

      case Defensive_Stance:
         strengthScaling = 0.4f;
         modifiers.add(new StatusModifier(true, StatusModifier.Type.incrementSpeed, -1, 2));
         modifiers.add(new StatusModifier(true, StatusModifier.Type.setBlockStacks, 1, 2));
         icon = new ImageIcon(FileLoader.loadImage("sprites/ui/hud/shield_icon.png")).getImage();
         break;

      case Bite:
         strengthScaling = 1f;
         break;

      default:
         break;
      }
   }

   public Image getIcon()
   {
      return icon;
   }

   public float getStrengthScaling()
   {
      return strengthScaling;
   }
   
   public ArrayList<StatusModifier> getModifiers()
   {
      return modifiers;
   }
}
