package com.hmason;
import java.awt.Image;

import javax.swing.ImageIcon;

public class Terrain
{
   
   private Tile tile;
   private Image source;
   private Image sprite;
   private Image[] mips;
   private boolean walkable = true;
   
   public static final float[] MIP_SCALE =
   { 0.5f, 1f, 2f, 3f };
   
   public static final int MIP_LEVEL = 1;

   public Terrain(boolean isWall, String terrainType, Tile tile)
   {
      //System.out.println("loading");
      this.tile = tile;
      if(isWall)
      {
         walkable = false;
         tile.setLightPassthrough(false);
         if(terrainType == "top")
         { 
            source = new ImageIcon(FileLoader.loadImage("sprites/biomes/dirt_wall_top.png")).getImage();
         }else if(terrainType == "side")
         {
            source = new ImageIcon(FileLoader.loadImage("sprites/biomes/dirt_wall_side.png")).getImage();
         }
         
      }else
      {
         source = new ImageIcon(FileLoader.loadImage("sprites/biomes/dirt_tile.png")).getImage();
      }
      
      sprite = source;
      generateMips();
   }

   public Image getSprite()
   {
      return sprite;
   }

   public void setSprite(Image sprite)
   {
      this.sprite = sprite;
   }
   
   public void setMipLevel(int mipLevel)
   {
      if(mips != null)
      {
         sprite = mips[mipLevel];
      }
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
   
   public boolean isWalkable()
   {
      return walkable;
   }
}
