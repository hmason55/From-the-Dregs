package com.hmason;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

public class FileLoader
{

   public static InputStream loadResource(String path)
   {
      return FileLoader.class.getClassLoader().getResourceAsStream(path);
   }
   
   public static BufferedImage loadImage(String path)
   {
      try
      {
         return ImageIO.read(FileLoader.class.getClassLoader().getResourceAsStream(path));
      } catch (IOException e)
      {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }
      return null;
   }
}
