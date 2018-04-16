package com.hmason;
import java.awt.Image;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

public class Fog
{
   private float density;
   private Image source;
   private Image sprite;
   
   private Image[] mips;
   
   public static final float[] MIP_SCALE =
      { 0.5f, 1f, 2f, 3f };
      
   public static final int MIP_LEVEL = 1;

   public Fog(float density)
   {
      source = new ImageIcon(FileLoader.loadImage("sprites/biomes/fog_tile.png")).getImage();
      this.density = density;
      
      sprite = source;
      generateMips();
   }
   
   private void generateMips()
   {
      if (source != null)
      {
         mips = new Image[MIP_SCALE.length];
         for (int i = 0; i < MIP_SCALE.length; i++)
         {
            mips[i] = source.getScaledInstance((int) (Tile.WIDTH * MIP_SCALE[i]), (int) (Tile.HEIGHT/2 * MIP_SCALE[i]),
                  Image.SCALE_FAST);
         }
      } else 
      {
         System.out.println("Can't generate mips without a source image.");
      }
   }
   
   public Image getSprite()
   {
      return sprite;
   }
   
   public float getDensity()
   {
      return density;
   }
   
   public void setDensity(float density)
   {
      this.density = density;
   }
   
   public void setMipLevel(int mipLevel)
   {
      if(mips != null)
      {
         sprite = mips[mipLevel];
      }
   }
}
