package com.hmason;

import java.awt.Image;
import java.util.ArrayList;

import javax.swing.ImageIcon;

public class Action
{

   // This action's icon as it appears on the action bar
   private Image icon;

   // How much damage this action deals based on the unit's strength
   private float strengthScaling = 0f;

   // A list of status modifiers that this action applies
   ArrayList<StatusModifier> modifiers;

   // All current actions
   public enum Moveset
   {
      Forward_Slash, Crushing_Swing, Defensive_Stance, Bite
   }

   public Action(Moveset action)
   {
      modifiers = new ArrayList<StatusModifier>();
      
      switch (action)
      {
      
      // Deals 100% of the unit's strength as damage, no status modifiers
      case Forward_Slash:
         strengthScaling = 1f;
         icon = new ImageIcon(FileLoader.loadImage("sprites/ui/hud/sword_icon.png")).getImage();
         break;

      // Deals 180% of the unit's strength as damage, greatly delays this unit's next turn
      case Crushing_Swing:
         strengthScaling = 1.80f;
         modifiers.add(new StatusModifier(true, StatusModifier.Type.incrementSpeed, -10, 1));
         icon = new ImageIcon(FileLoader.loadImage("sprites/ui/hud/sword_icon.png")).getImage();
         break;

      // Deals only 70% of a unit's strength as damage and makes them slightly slower, 
      // but allows them to block the next incoming attack if it has enough resources
      case Defensive_Stance:
         strengthScaling = 0.70f;
         modifiers.add(new StatusModifier(true, StatusModifier.Type.incrementSpeed, -2, 2));
         modifiers.add(new StatusModifier(true, StatusModifier.Type.setBlockStacks, 1, 2));
         icon = new ImageIcon(FileLoader.loadImage("sprites/ui/hud/shield_icon.png")).getImage();
         break;

      // Deals 100% of the unit's strength as damage, no status modifiers (generic enemy action)
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
