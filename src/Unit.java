import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;

public class Unit
{
   
   
   
   private int maxHealth;
   private int currentHealth;
   
   Image idleSource;
   
   private Image[] idleAnimation;
   private final int ANIM_LENGTH = 4;
   private int idleAnimationFrame = 0;
   
   private Image[][] idleMips;
   public static final float[] MIP_SCALE =
      { 0.5f, 1f, 2f, 3f };
      
   public static final int MIP_LEVEL = 1;
   
   public Unit()
   {
      maxHealth = 10;
      currentHealth = 10;
      
      // Initialize Idle Animation
      idleAnimation = new Image[ANIM_LENGTH];
      idleSource = new ImageIcon("src/sprites/units/bat/bat_small_idle_sheet.png").getImage();
      for(int i = 0; i < ANIM_LENGTH; i++)
      {
         BufferedImage bufferedImage = new BufferedImage(idleSource.getWidth(null), idleSource.getHeight(null), BufferedImage.TYPE_INT_ARGB);
         Graphics2D bGr = bufferedImage.createGraphics();
         bGr.drawImage(idleSource, 0, 0, null);
         bGr.dispose();
         
         idleAnimation[i] = bufferedImage.getSubimage(i * Tile.WIDTH, 0, Tile.WIDTH, Tile.HEIGHT).getScaledInstance(Tile.WIDTH, Tile.HEIGHT, Image.SCALE_FAST);
      }
      
      generateMips();

   }
   
   public Image[] getAnimation()
   {
      return idleAnimation;
   }
   
   public Image getSprite()
   {
      return idleAnimation[idleAnimationFrame];
   }

   public int getIdleAnimationFrame()
   {
      return idleAnimationFrame;
   }
   
   public void incrementIdleAnimation()
   {
      idleAnimationFrame++;
      idleAnimationFrame = idleAnimationFrame % ANIM_LENGTH;
   }
   
   public void setMipLevel(int mipLevel)
   {
      if(idleMips != null)
      {
         for(int i = 0; i < ANIM_LENGTH; i++)
         {
            idleAnimation[i] = idleMips[i][mipLevel];
         }
      }
   }

   private void generateMips()
   {
      if (idleSource != null)
      {
         idleMips = new Image[ANIM_LENGTH][MIP_SCALE.length];
         for(int i = 0; i < ANIM_LENGTH; i++)
         {
            for (int j = 0; j < MIP_SCALE.length; j++)
            {
               idleMips[i][j] = idleAnimation[i].getScaledInstance((int) (Tile.WIDTH * MIP_SCALE[j]), (int) (Tile.HEIGHT * MIP_SCALE[j]),
                     Image.SCALE_FAST);
            }
         }
      } else 
      {
         System.out.println("Can't generate mips without a source image.");
      }
   }
}
